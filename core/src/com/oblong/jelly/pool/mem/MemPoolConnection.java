// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.mem;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import net.jcip.annotations.NotThreadSafe;

import com.oblong.jelly.Hose;
import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolOptions;
import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.pool.PoolProtein;
import com.oblong.jelly.pool.ServerErrorCode;
import com.oblong.jelly.pool.net.NetConnection;
import com.oblong.jelly.pool.net.NetConnectionFactory;
import com.oblong.jelly.pool.net.Request;
import com.oblong.jelly.slaw.SlawFactory;
import com.oblong.util.Pair;

import static com.oblong.jelly.pool.ServerErrorCode.*;
import static com.oblong.jelly.pool.net.Request.*;
import static com.oblong.jelly.pool.net.NetProteinMetadata.*;


@NotThreadSafe
final class MemPoolConnection implements NetConnection {

    static class Factory implements NetConnectionFactory {
        @Override public NetConnection get(PoolServer srv) {
            return new MemPoolConnection(srv);
        }

        @Override public String serviceName() {
            return "_pool-server._tcp";
        }
    }

    public MemPoolConnection(PoolServer srv) {
        address = srv.address();
        pool = null;
        index = Protein.NO_INDEX;
        open = true;
        polled = null;
        polledIndex = -1;
    }

    @Override public void close() {
        pool = null;
        open = false;
    }

    @Override public PoolServerAddress address() { return address; }

    @Override public Set<Request> supportedRequests() { return SUPPORTED; }

    @Override public boolean isOpen() { return open; }

    @Override public SlawFactory factory() { return factory; }

    @Override public int version() { return 3; }

    @Override public void setTimeout(long t, TimeUnit u) {}

    @Override public PoolProtein polled() { return polled; }

    @Override public PoolProtein resetPolled() {
        final PoolProtein p = polled;
        polled = null;
        return p;
    }

    @Override public Slaw send(Request request, Slaw... args)
        throws PoolException {
        if (POOL_OPS.contains(request))
            return poolRequest(request, args[0].emitString());
        if (LIST == request) return listRequest();
        return hoseRequest(request, args);
    }

    @Override public void setHose(Hose h) {}

    private Slaw poolRequest(Request request, String poolName) {
        switch (request) {
        case CREATE:
            MemPool p = MemPool.create(poolName, PoolOptions.MEDIUM);
            open = false;
            return makeResponse(p == null ? POOL_EXISTS : OK);
        case DISPOSE:
            boolean r = MemPool.dispose(poolName);
            open = false;
            return makeResponse(r ? OK : NO_SUCH_POOL);
        case PARTICIPATE:
            pool = MemPool.get(poolName);
            open = pool != null;
            return makeResponse(open ? OK : NO_SUCH_POOL);
        case PARTICIPATE_C:
            pool = MemPool.exists(poolName) ?
                MemPool.get(poolName) :  MemPool.create(poolName, PoolOptions.MEDIUM);
            open = pool != null;
            return makeResponse(OK);
        default:
            return makeResponse(NOP);
        }
    }

    private Slaw hoseRequest(Request request, Slaw[] args)
        throws PoolException {
        if (pool == null) return makeResponse(NULL_HOSE);
        switch (request) {
        case OLDEST_INDEX:
            return makeResponse(makeIndex(pool.oldestIndex()), OK);
        case NEWEST_INDEX:
            return makeResponse(makeIndex(pool.newestIndex()), OK);
        case DEPOSIT:
            return deposit(args[0]);
        case NTH_PROTEIN:
            return nth(args[0].emitLong());
        case NEXT:
            return next(args[0].emitLong(), null);
        case PROBE_FWD:
            return next(args[0].emitLong(), args[1]);
        case PREV:
            return prev(args[0].emitLong(), null);
        case PROBE_BACK:
            return prev(args[0].emitLong(), args[1]);
        case AWAIT_NEXT:
            return await(args[0].emitDouble());
        case FANCY_ADD_AWAITER:
            return pollProtein(args[0].emitLong(), args[1]);
        case SUB_FETCH:
            return subfetch(args[0]);
        case WITHDRAW:
            close();
            return makeResponse(OK);
        default:
            return makeResponse(NOP);
        }
    }

    private Slaw listRequest() {
        open = false;
        return makeResponse(OK, factory.list(MemPool.slawNames()));
    }

    private Slaw deposit(Slaw s) {
        final PoolProtein p = pool.deposit(s.toProtein());
        if (polledIndex > -1 && polledIndex <= p.index()) {
            polled = p;
        }
        return makeResponse(makeIndex(p.index()),
                            OK,
                            makeStamp(p.timestamp()));
    }

    private Slaw nth(long idx) {
        final PoolProtein p = pool.nth(idx);
        final Slaw time = makeStamp(p == null ? 0 : p.timestamp());
        final Slaw prot = p == null ? NULL_PROT : p.bareProtein();
        final Slaw ret =
            p == null ? makeResponse(NO_SUCH_PROTEIN) : OK;
        return makeResponse(prot, time, ret);
    }

    private PoolProtein nextProtein(long idx, Slaw desc) {
        idx = Math.max(pool.oldestIndex(), idx);
        final Slaw[] m = desc != null && desc.isList() ?
            desc.emitArray() : new Slaw[0];
        final PoolProtein p =
            desc == null ? pool.next(idx, 0) : pool.find(idx, m, true);
        return p;
    }

    private Slaw pollProtein(long idx, Slaw desc) {
        polled = nextProtein(idx, desc);
        polledIndex = polled == null ? idx : -1;
        return polled == null
            ? makeResponse(NO_SUCH_PROTEIN, makeStamp(0), makeIndex(0))
            : makeResponse(OK, makeStamp(polled.timestamp()),
                           makeIndex(polled.index()));
    }

    private Slaw next(long idx, Slaw desc) {
        final PoolProtein p = nextProtein(idx, desc);
        if (p != null) index = p.index() + 1;
        return makePTIR(p);
    }

    private Slaw prev(long idx, Slaw desc) {
        idx = Math.min(pool.newestIndex(), idx - 1);
        final Slaw[] m = desc != null && desc.isList() ?
            desc.emitArray() : new Slaw[0];
        final PoolProtein p =
            desc == null ? pool.nth(idx) : pool.find(idx, m, false);
        if (p != null) index = p.index();
        return makePTIR(p);
    }

    private Slaw makePTIR(PoolProtein p) {
        final Slaw time = makeStamp(p == null ? 0 : p.timestamp());
        final Slaw prot = p == null ? NULL_PROT : p.bareProtein();
        final Slaw ret = p == null ? NO_SUCH_PROTEIN : OK;
        final long idx = p == null ? Protein.NO_INDEX : p.index();
        return makeResponse(prot, time, makeIndex(idx), ret);
    }

    private Slaw await(double timeout) throws PoolException {
        final PoolProtein p = pool.next(index, timeout);
        if (p == null && timeout > 0) return null;
        final Slaw time = makeStamp(p == null ? 0 : p.timestamp());
        final Slaw prot = p == null ? NULL_PROT : p.bareProtein();
        final Slaw ret = p == null ? NO_SUCH_PROTEIN : OK;
        final long idx = p == null ? Protein.NO_INDEX : p.index();
        if (p != null) index = idx + 1;
        return makeResponse(ret, prot, time, makeIndex(idx));
    }

    private Slaw subfetch(Slaw args) {
        final List<Slaw> info = new ArrayList<Slaw>(args.count());
        long min = -1;
        long max = -1;
        for (Slaw m : args) {
            final Pair<Long, Slaw> dl = subfetchOne(m);
            if (dl != null) {
                if (min < 0 || min > dl.first()) min = dl.first();
                if (dl.first() > max) max = dl.first();
                info.add(dl.second());
            }
        }
        return makeResponse(factory.list(info),
                            makeIndex(min),
                            makeIndex(max));
    }

    private Pair<Long, Slaw> subfetchOne(Slaw req) {
        final Slaw IDX_K = factory.string("idx");
        final Slaw DES_K = factory.string("des");
        final Slaw ING_K = factory.string("ing");
        final Slaw ROF_K = factory.string("roff");
        final Slaw RNO_K = factory.string("rbytes");
        try {
            final long idx = req.find(IDX_K).emitLong();
            final boolean descs = req.find(DES_K).emitBoolean();
            final boolean ings = req.find(ING_K).emitBoolean();
            final long ds = req.find(ROF_K).emitLong();
            final long dl = req.find(RNO_K).emitLong();
            final PoolProtein prot = pool.nth(idx, descs, ings, ds, dl);
            if (prot == null) return null;
            final MemProteinMetadata md =
                new MemProteinMetadata(pool.nth(idx), prot);
            final Slaw r = factory.map(RKEY, OK,
                                       INDEX_KEY, makeIndex(idx),
                                       STAMP_KEY, makeStamp(prot.timestamp()),
                                       SIZE_KEY, makeLong(md.size()),
                                       ISIZE_KEY, makeLong(md.ingestsSize()),
                                       DSIZE_KEY, makeLong(md.descripsSize()),
                                       RSIZE_KEY, makeLong(md.dataSize()),
                                       INO_KEY, makeLong(md.ingestsNumber()),
                                       DNO_KEY, makeLong(md.descripsNumber()),
                                       PROTEIN_KEY, prot);
            return Pair.create(new Long(idx), r);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Slaw makeResponse(Slaw... args) {
        return factory.list(args);
    }

    private static Slaw makeRet(ServerErrorCode c) {
        return factory.number(NumericIlk.INT64, c.code());
    }

    private static Slaw makeStamp(double ts) {
        return factory.number(NumericIlk.FLOAT64, ts);
    }

    private static Slaw makeIndex(long idx) {
        return makeLong(idx);
    }

    private static Slaw makeLong(long idx) {
        return factory.number(NumericIlk.INT64, idx);
    }

    private final PoolServerAddress address;
    private MemPool pool;
    private long index;
    private boolean open;
    private PoolProtein polled;
    private long polledIndex;

    private static final com.oblong.jelly.slaw.SlawFactory factory =
        new com.oblong.jelly.slaw.java.JavaSlawFactory();
    private static final Slaw OK = makeRet(POOL_SPLEND);
    private static final Slaw NOP = makeRet(POOL_UNSUPPORTED_OPERATION);
    private static final Slaw NO_SUCH_POOL = makeRet(POOL_NO_SUCH_POOL);
    private static final Slaw NO_SUCH_PROTEIN = makeRet(POOL_NO_SUCH_PROTEIN);
    private static final Slaw POOL_EXISTS = makeRet(POOL_EXISTS_V3);
    private static final Slaw NULL_HOSE = makeRet(POOL_NULL_HOSE);
    private static final Slaw NULL_PROT = factory.protein(null, null, null);

    static final Set<Request> SUPPORTED =
        EnumSet.of(CREATE, DISPOSE, PARTICIPATE, PARTICIPATE_C, WITHDRAW,
                   OLDEST_INDEX, NEWEST_INDEX, DEPOSIT, NTH_PROTEIN, NEXT,
                   PROBE_FWD, PREV, PROBE_BACK, AWAIT_NEXT, LIST,
                   FANCY_ADD_AWAITER, SUB_FETCH);
    static final Set<Request> POOL_OPS =
        EnumSet.of(CREATE, DISPOSE, PARTICIPATE, PARTICIPATE_C);
}
