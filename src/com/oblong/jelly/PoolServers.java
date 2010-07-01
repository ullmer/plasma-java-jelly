// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.concurrent.ConcurrentHashMap;

import net.jcip.annotations.ThreadSafe;

import com.oblong.jelly.PoolServerAddress.BadAddress;
import com.oblong.jelly.pool.tcp.TCPServerFactory;

/**
 *
 * Created: Sat Jun 19 00:33:27 2010
 *
 * @author jao
 */
@ThreadSafe
public class PoolServers {

    public static final PoolServer get(PoolServerAddress address) {
        PoolServer server = servers.get(address);
        if (server == null) {
            final String scheme = address.scheme();
            final Factory f = factories.get(scheme);
            if (f != null) {
                try {
                    server = f.get(address);
                    final PoolServer oldServer =
                        servers.putIfAbsent(address, server);
                    if (oldServer != null) server = oldServer;
                } catch (PoolException e) {
                    // TODO: proper logging
                    e.printStackTrace();
                }
            }
        }
        return server;
    }

    public interface Factory {
        PoolServer get(PoolServerAddress address) throws PoolException;
    }

    public static final boolean register(String scheme, Factory factory) {
        if (scheme == null || factory == null) return false;
        return factories.put(scheme, factory) == null;
    }

    private static final ConcurrentHashMap<String, Factory> factories =
        new ConcurrentHashMap<String, Factory>();

    static {
        register("tcp", new com.oblong.jelly.pool.tcp.TCPServerFactory());
    }

    private static ConcurrentHashMap<PoolServerAddress, PoolServer> servers =
        new ConcurrentHashMap<PoolServerAddress, PoolServer>();

    private PoolServers() {}
}
