// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author jao
 */
public abstract class HoseGang {

    public static HoseGang newGang() { return newGang(10); }
    public static HoseGang newGang(int bufferSize) {
        return new com.oblong.jelly.pool.gang.Gang(bufferSize);
    }

    public abstract int count();

    public final boolean add(PoolAddress addr) throws PoolException {
        return add(addr.toString(), addr);
    }

    public final boolean add(String uri) throws PoolException {
        return add(uri, uri);
    }

    public final boolean add(Hose hose) throws PoolException {
        return add(hose.name(), hose);
    }

    public final boolean add(String name, String uri) throws PoolException {
        return doAdd(name, Pool.participate(uri));
    }

    public final boolean add(String name, PoolAddress addr)
        throws PoolException {
        return doAdd(name, Pool.participate(addr));
    }

    public final boolean add(String name, Hose hose) throws PoolException {
        return doAdd(name, hose.dupAndClose());
    }

    public abstract boolean remove(String name);
    public abstract void disband();

    public abstract Protein next() throws GangException, InterruptedException;
    public abstract Protein awaitNext(long period, TimeUnit unit)
        throws GangException, TimeoutException, InterruptedException;

    public abstract boolean wakeUp();

    protected abstract boolean doAdd(String name, Hose hose)
        throws PoolException;
}
