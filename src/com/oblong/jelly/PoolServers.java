// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.concurrent.ConcurrentHashMap;

import net.jcip.annotations.ThreadSafe;

/**
 *
 * Created: Sat Jun 19 00:33:27 2010
 *
 * @author jao
 */
@ThreadSafe
public final class PoolServers {

    public static PoolServer get(PoolServerAddress address)
        throws PoolException {
        PoolServer server = servers.get(address);
        if (server == null) {
            final String scheme = address.scheme();
            final Factory f = factories.get(scheme);
            if (f != null) {
                server = f.get(address);
                final PoolServer old = servers.putIfAbsent(address, server);
                if (old != null) server = old;
            }
        }
        return server;
    }

    public interface Factory {
        PoolServer get(PoolServerAddress address) throws PoolException;
    }

    public static boolean register(String scheme, Factory factory) {
        if (scheme == null || factory == null) return false;
        return factories.put(scheme, factory) == null;
    }

    private static ConcurrentHashMap<String, Factory> factories =
        new ConcurrentHashMap<String, Factory>();

    static {
        register("tcp", new com.oblong.jelly.pool.tcp.TCPServerFactory());
    }

    private static ConcurrentHashMap<PoolServerAddress, PoolServer> servers =
        new ConcurrentHashMap<PoolServerAddress, PoolServer>();

    private PoolServers() {}
}
