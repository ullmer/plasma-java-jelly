// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

/**
 *
 * Created: Thu Jun  3 10:08:24 2010
 *
 * @author jao
 */
public final class Pool {

    public static void create(String uri, PoolOptions opts)
        throws PoolException {
        create(new PoolServerAddress(uri), poolName(uri), opts);
    }

    public static void create(PoolServerAddress addr,
                              String name,
                              PoolOptions opts) throws PoolException {
        PoolServers.get(addr).create(name, opts);
    }

    public static void dispose(String uri) throws PoolException {
        dispose(new PoolServerAddress(uri), poolName(uri));
    }

    public static void dispose(PoolServerAddress addr, String name)
        throws PoolException {
        PoolServers.get(addr).dispose(name);
    }

    public static Hose participate(String uri)
        throws PoolException {
        return participate(new PoolServerAddress(uri), poolName(uri));
    }

    public static Hose participate(PoolServerAddress addr, String name)
        throws PoolException {
        return PoolServers.get(addr).participate(name);
    }

    public static Hose participate(String uri, PoolOptions opts)
        throws PoolException {
        return participate(new PoolServerAddress(uri), poolName(uri), opts);
    }

    public static Hose participate(PoolServerAddress addr,
                                   String name,
                                   PoolOptions opts)
        throws PoolException {
        return PoolServers.get(addr).participate(name, opts);
    }

    private static String poolName(String uri) throws PoolException {
        final int i = uri.lastIndexOf('/');
        if (i < 0 || i == uri.length() - 1)
            throw new PoolServerAddress.BadAddress("Empty pool name");
        return uri.substring(i);
    }

    private Pool() {}
}
