
/* (c)  oblong industries */

package com.oblong.jelly.pool.net;

import com.oblong.jelly.*;
import com.oblong.jelly.pool.PoolProtein;
import com.oblong.jelly.slaw.SlawFactory;
import com.oblong.util.ThreadChecker;
//import com.oblong.util.logging.ObLog;
import net.jcip.annotations.NotThreadSafe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@NotThreadSafe
public final class NetHose implements Hose {

    protected final ThreadChecker threadChecker = new ThreadChecker(Thread.currentThread());

    public bool isThreadCheckerEngaged() {return threadChecker.isThreadCheckerEngaged();} //BAU
    public void engageThreadChecker()    {threadChecker.engageThreadChecker();}           //BAU
    public void disengageThreadChecker() {threadChecker.disengageThreadChecker();}        //BAU

    //private static final ObLog log = ObLog.get(NetHose.class);

    @Override public int version() {
        threadChecker.check();
        return connection.version();
    }

    @Override public PoolMetadata metadata() throws PoolException {
        threadChecker.check();
        final Slaw res = Request.INFO.send(connection, longSlaw(-1));
        return new NetPoolMetadata(res.nth(1).toProtein().ingests());
    }

    @Override public String name() {
        threadChecker.check();
        return name;
    }

    @Override public void setName(String n) {
        threadChecker.check();
        name = n == null ? poolAddress.toString() : n;
    }

    @Override public PoolAddress poolAddress() {
        threadChecker.check();
        return poolAddress;
    }

    @Override public boolean isConnected() {
        threadChecker.check();
        return connection != null && connection.isOpen();
    }

    @Override public void withdraw() {
        threadChecker.check();
        try {
            if (isConnected()) Request.WITHDRAW.sendAndClose(connection);
        } catch (PoolException e) {
            //log.i("Error withdrawing Hose '" + name() + "':\n\t" + e);
            System.out.println("Error withdrawing Hose '" + name()); 
        }
    }

    @Override public long index() {
        threadChecker.check();
        return index;
    }

    @Override public long newestIndex() throws PoolException {
        threadChecker.check();
        try {
            final Slaw res = Request.NEWEST_INDEX.send(connection).nth(0);
            return res.emitLong();
        } catch (NoSuchProteinException e) {
            return Protein.NO_INDEX;
        }
    }

    @Override public long oldestIndex() throws PoolException {
        threadChecker.check();
        try {
            final Slaw res = Request.OLDEST_INDEX.send(connection).nth(0);
            return res.emitLong();
        } catch (NoSuchProteinException e) {
            return Protein.NO_INDEX;
        }
    }

    @Override public void seekTo(long idx) {
        threadChecker.check();
        index = idx;
        dirtyIndex = true;
    }

    @Override public void seekBy(long offset) {
        threadChecker.check();
        seekTo(index + offset);
    }

    @Override public void toLast() throws PoolException {
        threadChecker.check();
        seekTo(newestIndex());
    }

    @Override public void runOut() throws PoolException {
        threadChecker.check();
        seekTo(1 + newestIndex());
    }

    @Override public void rewind() throws PoolException {
        threadChecker.check();
        seekTo(oldestIndex());
    }

    @Override public Protein deposit(Protein p) throws PoolException {
        threadChecker.check();
        final Slaw res = Request.DEPOSIT.send(connection, p);
        final long index = res.nth(0).emitLong();
        final double stamp = res.nth(2).emitDouble();
        return new PoolProtein(p, index, stamp, this);
    }

    @Override public Protein current() throws PoolException {
        threadChecker.check();
        return nth(index);
    }

    @Override public Protein next(Slaw... descrips) throws PoolException {
        threadChecker.check();
        if (descrips.length == 0) return next();
        final Slaw res = Request.PROBE_FWD.send(connection,
                                                indexSlaw(),
                                                matcher(descrips));
        return new PoolProtein(res.nth(0).toProtein(),
                               cleanIndex(res.nth(2).emitLong() + 1) - 1,
                               res.nth(1).emitDouble(),
                               this);
    }

    @Override public Protein awaitNext(long t, TimeUnit unit)
            throws PoolException, TimeoutException {
        threadChecker.check();
        if (t == 0) return next();
        if (dirtyIndex)
            try {
                return next();
            } catch (NoSuchProteinException e) {
                cleanIndex(index);
            }
        return await(t, unit);
    }

    @Override public Protein awaitNext() throws PoolException {
        threadChecker.check();
        try {
            return awaitNext(-1, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            assert false : "Timeout while waiting forever";
            return null;
        }
    }

    @Override public Protein previous(Slaw... descrips) throws PoolException {
        threadChecker.check();
        if (descrips.length == 0) return previous();
        final Slaw res = Request.PROBE_BACK.send(connection,
                                                 indexSlaw(),
                                                 matcher(descrips));
        return new PoolProtein(res.nth(0).toProtein(),
                               cleanIndex(res.nth(2).emitLong()),
                               res.nth(1).emitDouble(),
                               this);
    }

    @Override public Protein nth(long idx) throws PoolException {
        threadChecker.check();
        final Slaw sidx = longSlaw(idx);
        final Slaw res = Request.NTH_PROTEIN.send(connection, sidx);
        return new PoolProtein(res.nth(0).toProtein(),
                               sidx.emitLong(),
                               res.nth(1).emitDouble(),
                               this);
    }

    @Override public Protein nth(long idx, boolean d, boolean i, boolean dt)
            throws PoolException {
        threadChecker.check();
        final NetProteinMetadata md =
            firstFetch(subFetch(idx, idx + 1, d, i, dt));
        final Protein p = md.partialProtein();
        if (p == null) throw new NoSuchProteinException(0);
        return p;
    }

    @Override public List<Protein> range(long f, long t)
            throws PoolException {
        threadChecker.check();
        return NetProteinMetadata.parseProteins(
            subFetch(f, t, true, true, true), this);
    }

    @Override public ProteinMetadata metadata(MetadataRequest req)
            throws PoolException {
        threadChecker.check();
        return firstFetch(subFetch(req));
    }

    @Override public List<ProteinMetadata> metadata(MetadataRequest... rs)
            throws PoolException {
        threadChecker.check();
        return NetProteinMetadata.parseMeta(subFetch(rs), this);
    }

    @Override public Hose dup() throws PoolException {
        threadChecker.check();
        final Hose result = Pool.participate(poolAddress);
        result.setName(name);
        result.seekTo(index);
        return result;
    }

    @Override public Hose dupAndClose() throws PoolException {
        threadChecker.check();
        if (!isConnected()) return dup();
        final NetConnection c = connection;
        connection = null;
        return new NetHose(c, poolAddress, name, index);
    }

    NetHose(NetConnection con, String pn) throws PoolException {
        this(con, new PoolAddress(con.address(), pn), null, 0);
        threadChecker.check();
        cleanIndex(newestIndex()); // TODO: Shouldn't it be +1 ??
    }

    NetHose(NetConnection conn, PoolAddress addr, String name, long idx) {
        threadChecker.check();
        connection = conn;
        factory = connection.factory();
        poolAddress = addr;
        setName(name);
        cleanIndex(idx);
        connection.setHose(this);
    }

    private Slaw subFetch(long f, long t, boolean d, boolean i, boolean dt)
            throws PoolException {
        threadChecker.check();
        if (f < 0) f = 0;
        if (f >= t) return factory.list();
        final List<Slaw> req = new ArrayList<Slaw>((int)(t - f));
        for (long k = f; k < t; ++k)
            req.add(new MetadataRequest(k, d, i, dt ? 0 : -1, -1).toSlaw());
        return Request.SUB_FETCH.send(connection, factory.list(req)).nth(0);
    }

    private Slaw subFetch(MetadataRequest... rs) throws PoolException {
        threadChecker.check();
        if (rs.length == 0) return factory.list();
        final List<Slaw> req = new ArrayList<Slaw>(rs.length);
        for (MetadataRequest r : rs) req.add(r.toSlaw());
        return Request.SUB_FETCH.send(connection, factory.list(req)).nth(0);
    }

    private NetProteinMetadata firstFetch(Slaw f)
            throws NoSuchProteinException {
        threadChecker.check();
        if (f.count() == 0) throw new NoSuchProteinException(0);
        final NetProteinMetadata md = new NetProteinMetadata(f.nth(0), this);
        if (md.retort() != 0) throw new NoSuchProteinException(md.retort());
        return md;
    }

    private Protein next() throws PoolException {
        threadChecker.check();
        final Slaw res = Request.NEXT.send(connection, indexSlaw());
        return new PoolProtein(res.nth(0).toProtein(),
                               cleanIndex(res.nth(2).emitLong() + 1) - 1,
                               res.nth(1).emitDouble(),
                               this);
    }

    private Protein previous() throws PoolException {
        threadChecker.check();
        final Slaw res = Request.PREV.send(connection, indexSlaw());
        return new PoolProtein(res.nth(0).toProtein(),
                               cleanIndex(res.nth(2).emitLong()),
                               res.nth(1).emitDouble(),
                               this);
    }

    private Protein await(long t, TimeUnit u)
            throws PoolException, TimeoutException {
        threadChecker.check();
        checkConnection();
        if (t > 0) connection.setTimeout(u.toMillis(t) + 100, // wtf: why + 100 ms ?
                                         TimeUnit.MILLISECONDS);
        try {
            final Slaw res =
                Request.AWAIT_NEXT.send(connection, timeSlaw(t, u));
            if (res == null) throw new TimeoutException();
            return new PoolProtein(res.nth(1).toProtein(),
                                   cleanIndex(res.nth(3).emitLong() + 1) - 1,
                                   res.nth(2).emitDouble(),
                                   this);
        } finally {
            connection.setTimeout(0, u);
        }
    }

    private Slaw matcher(Slaw... descrips) {
        threadChecker.check();
        return factory.list(descrips);
    }

    private Slaw timeSlaw(long timeout, TimeUnit unit) {
        threadChecker.check();
        double poolTimeout =
            timeout < 0 ? WAIT_FOREVER : ((double)unit.toNanos(timeout))/1e9;
        if (version() < FIRST_NEW_WAIT_V) {
            if (poolTimeout == WAIT_FOREVER) poolTimeout = OLD_WAIT;
            else if (poolTimeout == NO_WAIT) poolTimeout = OLD_NO_WAIT;
        }
        return factory.number(NumericIlk.FLOAT64, poolTimeout);
    }

    private Slaw indexSlaw(long idx) {
        threadChecker.check();
        return longSlaw(Math.max(0, idx));
    }

    private Slaw indexSlaw() {
        threadChecker.check();
        return indexSlaw(index);
    }

    private Slaw longSlaw(long v) {
        threadChecker.check();
        return factory.number(NumericIlk.INT64, v);
    }

    private long cleanIndex(long idx) {
        threadChecker.check();
        dirtyIndex = false; // TODO: Patrick and Karol: figure out what it is (it looks suspicious)
        return index = idx;
    }

    private void checkConnection() throws InOutException {
        threadChecker.check();
        if (!isConnected()) throw new InOutException("Connection closed");
    }

    private static final double WAIT_FOREVER = -1;
    private static final double NO_WAIT = 0;
    private static final double OLD_WAIT = NO_WAIT;
    private static final double OLD_NO_WAIT = WAIT_FOREVER;
    private static final int FIRST_NEW_WAIT_V = 2;

    private final SlawFactory factory;
    private final PoolAddress poolAddress;
    private volatile NetConnection connection;
    private volatile String name;
    private volatile long index;
    private volatile boolean dirtyIndex;

    @Override public void closeConnectionAbruptly() {
        // exempt from threadChecker - see super method javadoc
        connection.close();
    }

}
