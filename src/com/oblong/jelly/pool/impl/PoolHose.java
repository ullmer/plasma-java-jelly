package com.oblong.jelly.pool.impl;

import net.jcip.annotations.NotThreadSafe;

import com.oblong.jelly.Hose;
import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.pool.ProtocolException;
import com.oblong.jelly.slaw.SlawFactory;

@NotThreadSafe
final class PoolHose implements Hose {

    PoolHose(ServerConnection conn, String pn) throws PoolException {
        connection = conn;
        factory = conn.factory();
        poolName = pn;
        setName(null);
        index = newestIndex();
    }

    @Override public int version() {
        return connection.version();
    }

    @Override public String name() {
        return name;
    }

    @Override public void setName(String n) {
        name = n == null ? connection.address() + "/" + poolName : n;
    }

    @Override public String poolName() {
        return poolName;
    }

    @Override public boolean isConnected() {
        return connection.isOpen();
    }

    @Override public void withdraw() throws PoolException {
        Request.WITHDRAW.sendAndClose(connection, factory.string(poolName));
    }

    @Override public long index() {
        return index;
    }

    @Override public long newestIndex() throws PoolException {
        final Slaw res = Request.NEWEST_INDEX.send(connection).nth(0);
        return res.emitLong();
    }

    @Override public long oldestIndex() throws PoolException {
        final Slaw res = Request.OLDEST_INDEX.send(connection).nth(0);
        try {
            return res.emitLong();
        } catch (UnsupportedOperationException e) {
            throw new ProtocolException(res, "Invalid argument 0");
        }
    }

    @Override public void seekTo(long index) {
        this.index = index;
    }

    @Override public void seekBy(long offset) {
        index += offset;
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
        final Protein recProt = res.nth(1).toProtein();
        return new PoolProtein(recProt, index, stamp, this);
    }

    @Override public Protein next() throws PoolException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public Protein next(double timeout) throws PoolException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public Protein previous() throws PoolException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override public Protein nth(long index) throws PoolException {
        // TODO Auto-generated method stub
        return null;
    }

    private Slaw timeoutToSlaw(double timeout) {
        if (timeout < 0) timeout = WAIT;
        if (version() < FIRST_NEW_WAIT_V) {
            if (timeout == WAIT) timeout = OLD_WAIT;
            else if (timeout == NO_WAIT) timeout = OLD_NO_WAIT;
        }
        return factory.number(NumericIlk.FLOAT64, timeout);
    }

    private static final double OLD_WAIT = NO_WAIT;
    private static final double OLD_NO_WAIT = WAIT;
    private static final int FIRST_NEW_WAIT_V = 2;

    private final ServerConnection connection;
    private final SlawFactory factory;
    private final String poolName;
    private String name;
    long index;
}
