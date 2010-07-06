// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.Set;

/**
 *
 * Created: Thu Jun  3 10:08:24 2010
 *
 * @author jao
 */
public final class Pool {

    public static void create(String uri, PoolOptions opts)
        throws PoolException {
        create(PoolAddress.fromURI(uri), opts);
    }

    public static void create(PoolAddress addr, PoolOptions opts)
        throws PoolException {
        PoolServers.get(addr.serverAddress()).create(addr.poolName(), opts);
    }

    public static void dispose(String uri) throws PoolException {
        dispose(PoolAddress.fromURI(uri));
    }

    public static void dispose(PoolAddress addr) throws PoolException {
        PoolServers.get(addr.serverAddress()).dispose(addr.poolName());
    }

    public static Hose participate(String uri) throws PoolException {
        return participate(PoolAddress.fromURI(uri));
    }

    public static Hose participate(PoolAddress addr) throws PoolException {
        return PoolServers.get(addr.serverAddress())
                          .participate(addr.poolName());
    }

    public static Hose participate(String uri, PoolOptions opts)
        throws PoolException {
        return participate(PoolAddress.fromURI(uri), opts);
    }

    public static Hose participate(PoolAddress addr, PoolOptions opts)
        throws PoolException {
        return PoolServers.get(addr.serverAddress())
                          .participate(addr.poolName(), opts);
    }

    public static Set<String> pools(String serverURI) throws PoolException {
        return pools(PoolServerAddress.fromURI(serverURI));
    }

    public static Set<String> pools(PoolServerAddress addr)
        throws PoolException {
        return PoolServers.get(addr).pools();
    }

    private Pool() {}
}
