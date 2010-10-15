// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.net;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import net.jcip.annotations.NotThreadSafe;

import com.oblong.jelly.Hose;
import com.oblong.jelly.NoSuchProteinException;
import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.PoolAddress;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.pool.PoolProtein;
import com.oblong.jelly.slaw.SlawFactory;

@NotThreadSafe
final class NetHose implements Hose {

    NetHose(NetConnection conn, String pn) throws PoolException {
        connection = conn;
        factory = conn.factory();
        poolAddress = new PoolAddress(connection.address(), pn);
        setName(null);
        cleanIndex(newestIndex());
    }

    @Override public int version() {
        return connection.version();
    }

    @Override public Slaw info() {
        try {
            final Slaw res = Request.INFO.send(connection, indexSlaw(-1));
            return res.nth(1).toProtein().ingests();
        } catch (Throwable e) {
            return factory.map();
        }
    }

    @Override public String name() {
        return name;
    }

    @Override public void setName(String n) {
        name = n == null ? poolAddress.toString() : n;
    }

    @Override public PoolAddress poolAddress() {
        return poolAddress;
    }

    @Override public boolean isConnected() {
        return connection.isOpen();
    }

    @Override public void withdraw() {
        try {
            if (isConnected()) Request.WITHDRAW.sendAndClose(connection);
        } catch (PoolException e) {
            final Logger log = Logger.getLogger(Hose.class.getName());
            log.warning("Error withdrawing Hose '" + name() + "'");
            log.warning("Exception was: " + e);
        }
    }

    @Override public long index() {
        return index;
    }

    @Override public long newestIndex() throws PoolException {
        try {
            final Slaw res = Request.NEWEST_INDEX.send(connection).nth(0);
            return res.emitLong();
        } catch (NoSuchProteinException e) {
            return Protein.NO_INDEX;
        }
    }

    @Override public long oldestIndex() throws PoolException {
        try {
            final Slaw res = Request.OLDEST_INDEX.send(connection).nth(0);
            return res.emitLong();
        } catch (NoSuchProteinException e) {
            return Protein.NO_INDEX;
        }
    }

    @Override public void seekTo(long idx) {
        index = idx;
        dirtyIndex = true;
    }

    @Override public void seekBy(long offset) {
        seekTo(index + offset);
    }

    @Override public void toLast() throws PoolException {
        seekTo(newestIndex());
    }

    @Override public void runOut() throws PoolException {
        seekTo(1 + newestIndex());
    }

    @Override public void rewind() throws PoolException {
        seekTo(oldestIndex());
    }

    @Override public Protein deposit(Protein p) throws PoolException {
        final Slaw res = Request.DEPOSIT.send(connection, p);
        final long index = res.nth(0).emitLong();
        final double stamp = res.nth(2).emitDouble();
        return new PoolProtein(p, index, stamp, this);
    }

    @Override public Protein current() throws PoolException {
        return nth(index);
    }

    @Override public Protein next() throws PoolException {
        final Slaw res = Request.NEXT.send(connection, indexSlaw(index));
        return new PoolProtein(res.nth(0).toProtein(),
                               cleanIndex(res.nth(2).emitLong() + 1) - 1,
                               res.nth(1).emitDouble(),
                               this);
    }

    @Override public Protein next(Slaw descrip) throws PoolException {
        if (descrip == null) throw new NoSuchProteinException(0L);
        final Slaw res =
            Request.PROBE_FWD.send(connection, indexSlaw(index), descrip);
        return new PoolProtein(res.nth(0).toProtein(),
                               cleanIndex(res.nth(2).emitLong() + 1) - 1,
                               res.nth(1).emitDouble(),
                               this);
    }

    @Override public Protein awaitNext(long t, TimeUnit unit)
        throws PoolException {
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
        return awaitNext(-1, TimeUnit.SECONDS);
    }

    @Override public Protein previous() throws PoolException {
        final Slaw res = Request.PREV.send(connection, indexSlaw(index));
        return new PoolProtein(res.nth(0).toProtein(),
                               cleanIndex(res.nth(2).emitLong()),
                               res.nth(1).emitDouble(),
                               this);
    }

    @Override public Protein previous(Slaw descrip) throws PoolException {
        if (descrip == null) throw new NoSuchProteinException(0L);
        final Slaw res =
            Request.PROBE_BACK.send(connection, indexSlaw(index), descrip);
        return new PoolProtein(res.nth(0).toProtein(),
                               cleanIndex(res.nth(2).emitLong()),
                               res.nth(1).emitDouble(),
                               this);
    }

    @Override public Protein nth(long idx) throws PoolException {
        final Slaw sidx = indexSlaw(idx);
        final Slaw res = Request.NTH_PROTEIN.send(connection, sidx);
        return new PoolProtein(res.nth(0).toProtein(),
                               sidx.emitLong(),
                               res.nth(1).emitDouble(),
                               this);
    }

    private Protein await(long t, TimeUnit u) throws PoolException {
        connection.setTimeout(t, u);
        try {
            final Slaw res =
                Request.AWAIT_NEXT.send(connection, timeSlaw(t, u));
            return new PoolProtein(res.nth(1).toProtein(),
                                   cleanIndex(res.nth(3).emitLong() + 1) - 1,
                                   res.nth(2).emitDouble(),
                                   this);
        } finally {
            connection.setTimeout(0, u);
        }
    }

    private Slaw timeSlaw(long timeout, TimeUnit unit) {
        double poolTimeout =
            timeout < 0 ? WAIT_FOREVER : ((double)unit.toNanos(timeout))/1e9;
        if (version() < FIRST_NEW_WAIT_V) {
            if (poolTimeout == WAIT_FOREVER) poolTimeout = OLD_WAIT;
            else if (poolTimeout == NO_WAIT) poolTimeout = OLD_NO_WAIT;
        }
        return factory.number(NumericIlk.FLOAT64, poolTimeout);
    }

    private Slaw indexSlaw(long idx) {
        return factory.number(NumericIlk.INT64, idx);
    }

    private long cleanIndex(long idx) {
        dirtyIndex = false;
        return index = idx;
    }

    private static final double WAIT_FOREVER = -1;
    private static final double NO_WAIT = 0;
    private static final double OLD_WAIT = NO_WAIT;
    private static final double OLD_NO_WAIT = WAIT_FOREVER;
    private static final int FIRST_NEW_WAIT_V = 2;

    private final NetConnection connection;
    private final SlawFactory factory;
    private final PoolAddress poolAddress;
    private String name;
    long index;
    boolean dirtyIndex;
}
