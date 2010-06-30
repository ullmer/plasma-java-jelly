// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.mem;

import java.util.EnumSet;
import java.util.Set;

import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.pool.impl.Request;
import static com.oblong.jelly.pool.impl.Request.*;
import static com.oblong.jelly.pool.impl.ServerErrorCode.*;

import com.oblong.jelly.pool.impl.PoolProtein;
import com.oblong.jelly.pool.impl.PoolConnection;
import com.oblong.jelly.pool.impl.PoolConnectionFactory;
import com.oblong.jelly.pool.impl.ServerErrorCode;
import com.oblong.jelly.slaw.SlawFactory;


/**
 *
 * Created: Tue Jun 29 19:13:51 2010
 *
 * @author jao
 */
final class MemPoolConnection implements PoolConnection {

    static class Factory implements PoolConnectionFactory {
        @Override public PoolConnection get(PoolServerAddress addr) {
            return new MemPoolConnection(addr);
        }
    }

    public MemPoolConnection(PoolServerAddress addr) {
        address = addr;
        pool = null;
        index = Protein.INVALID_INDEX;
        open = true;
    }

    @Override public void close() {
        pool = null;
        open = false;
    }

    @Override public PoolServerAddress address() {
        return address;
    }

    @Override public Set<Request> supportedRequests() { return SUPPORTED; }

    @Override public boolean isOpen() {
        return open;
    }

    @Override public SlawFactory factory() {
        return factory;
    }

    @Override public int version() {
        return 3;
    }

    @Override public Slaw send(Request request, Slaw... args)
        throws PoolException {
        if (POOL_OPS.contains(request))
            return poolRequest(request, args[0].emitString());
        if (LIST == request)
            return makeResponse(OK, factory.list(MemPool.names()));
        return hoseRequest(request, args);
    }

    private Slaw poolRequest(Request request, String poolName) {
        switch (request) {
        case CREATE:
            MemPool p = MemPool.create(poolName);
            return makeResponse(p == null ? POOL_EXISTS : OK);
        case DISPOSE:
            boolean r = MemPool.dispose(poolName);
            return makeResponse(r ? OK : NO_SUCH_POOL);
        case PARTICIPATE:
            pool = MemPool.get(poolName);
            open = pool != null;
            return makeResponse(open ? NO_SUCH_POOL : OK);
        case PARTICIPATE_C:
            pool = MemPool.get(poolName);
            if (pool == null) pool = MemPool.create(poolName);
            open = pool != null;
            return makeResponse(OK);
        default:
            return makeResponse(NOP);
        }
    }

    private Slaw hoseRequest(Request request, Slaw[] args) {
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
        case WITHDRAW:
            close();
            return makeResponse(OK);
        default:
            return makeResponse(NOP);
        }
    }

    private Slaw deposit(Slaw s) {
        final PoolProtein p = pool.deposit(s.toProtein());
        return makeResponse(makeIndex(p.index()),
                            OK,
                            makeStamp(p.timestamp()));
    }

    private Slaw nth(long idx) {
        final PoolProtein p = pool.nth(idx);
        final Slaw time = makeStamp(p == null ? 0 : p.timestamp());
        final Slaw prot = p == null ? NULL_PROT : p;
        final Slaw ret =
            p == null ? makeResponse(NO_SUCH_PROTEIN) : OK;
        return makeResponse(prot, time, ret);
    }

    private Slaw next(long idx, Slaw desc) {
        final PoolProtein p =
            desc == null ? pool.next(idx, 0) : pool.find(idx, desc, false);
        return makePTIR(p);
    }

    private Slaw prev(long idx, Slaw desc) {
        final PoolProtein p =
            desc == null ? pool.nth(idx - 2) : pool.find(idx, desc, true);
        return makePTIR(p);
    }

    private Slaw makePTIR(PoolProtein p) {
        final Slaw time = makeStamp(p == null ? 0 : p.timestamp());
        final Slaw prot = p == null ? NULL_PROT : p;
        final Slaw ret = p == null ? NO_SUCH_PROTEIN : OK;
        if (p != null) index = p.index();
        return makeResponse(prot, time, makeIndex(index), ret);
    }

    private Slaw await(double timeout) {
        final PoolProtein p = pool.next(index, timeout);
        final Slaw time = makeStamp(p == null ? 0 : p.timestamp());
        final Slaw prot = p == null ? NULL_PROT : p;
        final Slaw ret = p == null ? NO_SUCH_PROTEIN : OK;
        if (p != null) index = p.index();
        return makeResponse(ret, prot, time, makeIndex(index));
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
        return factory.number(NumericIlk.INT64, idx);
    }

    private final PoolServerAddress address;
    private MemPool pool;
    private long index;
    private boolean open;

    private static final com.oblong.jelly.slaw.SlawFactory factory =
        new com.oblong.jelly.slaw.JavaSlawFactory();
    private static final Slaw OK = makeRet(POOL_SPLEND);
    private static final Slaw NOP = makeRet(POOL_UNSUPPORTED_OPERATION);
    private static final Slaw NO_SUCH_POOL = makeRet(POOL_NO_SUCH_POOL);
    private static final Slaw NO_SUCH_PROTEIN = makeRet(POOL_NO_SUCH_PROTEIN);
    private static final Slaw POOL_EXISTS = makeRet(POOL_EXISTS_V3);
    private static final Slaw NULL_HOSE = makeRet(POOL_NULL_HOSE);
    private static final Slaw NULL_PROT = factory.protein(null, null, null);

    private static final Set<Request> SUPPORTED =
        EnumSet.of(CREATE, DISPOSE, PARTICIPATE, PARTICIPATE_C, WITHDRAW,
                   OLDEST_INDEX, NEWEST_INDEX, DEPOSIT, NTH_PROTEIN, NEXT,
                   PROBE_FWD, PREV, PROBE_BACK, AWAIT_NEXT, LIST);
    private static final Set<Request> POOL_OPS =
        EnumSet.of(CREATE, DISPOSE, PARTICIPATE, PARTICIPATE_C);
}
