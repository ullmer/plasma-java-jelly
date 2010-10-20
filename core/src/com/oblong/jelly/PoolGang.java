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

    public static PoolGang newGang(PoolAddress... addrs)
        throws GangException {
        return newGang(false, addrs);
    }

    public static PoolGang newGang(String... uris) throws GangException {
        return newGang(false, uris);
    }

    public static PoolGang newGang(boolean ignoreErrors, PoolAddress... addrs)
        throws GangException {
        final PoolGang gang = newGang(addrs.length);
        for (PoolAddress addr : addrs) {
            try {
                gang.add(addr, ignoreErrors);
            } catch (PoolException e) {
                throw new GangException(addr.toString(), addr, e);
            }
        }
        return gang;
    }

    public static PoolGang newGang(boolean ignoreErrors, String... uris)
        throws GangException {
        final PoolGang gang = newGang(uris.length);
        for (String uri : uris) {
            try {
                gang.add(uri, ignoreErrors);
            } catch (PoolException e) {
                throw new GangException(uri, null, e);
            }
        }
        return gang;
    }

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

    public abstract Protein next() throws GangException;
    public abstract Protein awaitNext(long period, TimeUnit unit)
        throws GangException, TimeoutException;

    public abstract boolean wakeUp();
}
