// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import com.oblong.jelly.PoolAddress.BadAddress;

/**
 *
 * Created: Thu Jun  3 10:08:24 2010
 *
 * @author jao
 */
public final class Pool {

    public static void create(String uri, PoolOptions opts)
        throws PoolException {
        create(new PoolAddress(uri), poolName(uri), opts);
    }

    public static void create(PoolAddress addr,
                              String name,
                              PoolOptions opts) throws PoolException {
        PoolServers.get(addr).create(name, opts);
    }

    public static void dispose(String uri) throws PoolException {
        dispose(new PoolAddress(uri), poolName(uri));
    }

    public static void dispose(PoolAddress addr, String name)
        throws PoolException {
        PoolServers.get(addr).dispose(name);
    }

    public static Hose participate(String uri)
        throws PoolException {
        return participate(new PoolAddress(uri), poolName(uri));
    }

    public static Hose participate(PoolAddress addr, String name)
        throws PoolException {
        return PoolServers.get(addr).participate(name);
    }

    public static Hose participate(String uri, PoolOptions opts)
        throws PoolException {
        return participate(new PoolAddress(uri), poolName(uri), opts);
    }

    public static Hose participate(PoolAddress addr,
                                   String name,
                                   PoolOptions opts)
        throws PoolException {
        return PoolServers.get(addr).participate(name, opts);
    }

    private static String poolName(String uri) throws PoolException {
        final int i = uri.lastIndexOf('/');
        if (i < 0 || i == uri.length() - 1)
            throw new BadAddress("Empty pool name");
        return uri.substring(i);
    }

    private Pool() {}
}
