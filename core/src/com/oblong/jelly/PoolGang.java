// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author jao
 */
public abstract class PoolGang {

    public static PoolGang newGang() { return newGang(10); }
    public static PoolGang newGang(int bufferSize) { return null; }

    public abstract int count();

    public final boolean add(String name, String uri) throws PoolException {
        return add(name, PoolAddress.fromURI(uri), false);
    }

    public final boolean add(String name, String uri, boolean ignoreErrors)
        throws PoolException {
        return add(name, PoolAddress.fromURI(uri), ignoreErrors);
    }

    public final boolean add(String name, PoolAddress addr)
        throws PoolException {
        return add(name, addr, false);
    }

    public final String add(PoolAddress addr) throws PoolException {
        return add(addr, false);
    }

    public final String add(PoolAddress addr, boolean ignoreErrors)
        throws PoolException {
        final String name = addr.toString();
        return add(name, addr, ignoreErrors) ? name : null;
    }

    public final String add(String uri) throws PoolException {
        return add(uri, false);
    }

    public final String add(String uri, boolean ignoreErrors)
        throws PoolException {
        return add(PoolAddress.fromURI(uri), ignoreErrors);
    }

    public abstract boolean add(String name,
                                PoolAddress addr,
                                boolean ignoreErrors) throws PoolException;


    public abstract boolean remove(String name);
    public abstract void disband();

    public abstract Protein next() throws GangException, InterruptedException;
    public abstract Protein awaitNext(long period, TimeUnit unit)
        throws GangException, TimeoutException, InterruptedException;

    public abstract boolean wakeUp();
}
