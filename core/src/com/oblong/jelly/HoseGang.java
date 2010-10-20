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

    public final boolean add(String name, String uri) throws PoolException {
        return add(name, PoolAddress.fromURI(uri), -1);
    }

    public final boolean add(String name, String uri, long index)
        throws PoolException {
        return add(name, PoolAddress.fromURI(uri), index);
    }

    public final boolean add(String name, PoolAddress addr)
        throws PoolException {
        return add(name, addr, -1);
    }

    public final String add(PoolAddress addr) throws PoolException {
        return add(addr, -1);
    }

    public final String add(PoolAddress addr, long index)
        throws PoolException {
        final String name = addr.toString();
        return add(name, addr, index) ? name : null;
    }

    public final String add(String uri) throws PoolException {
        return add(uri, -1);
    }

    public final String add(String uri, long index) throws PoolException {
        return add(PoolAddress.fromURI(uri), index);
    }

    public abstract boolean add(String name, PoolAddress addr, long index)
        throws PoolException;


    public abstract boolean remove(String name);
    public abstract void disband();

    public abstract Protein next() throws GangException, InterruptedException;
    public abstract Protein awaitNext(long period, TimeUnit unit)
        throws GangException, TimeoutException, InterruptedException;

    public abstract boolean wakeUp();
}
