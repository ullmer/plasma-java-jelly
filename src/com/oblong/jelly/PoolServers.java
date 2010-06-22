// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.jcip.annotations.ThreadSafe;

/**
 *
 * Created: Sat Jun 19 00:33:27 2010
 *
 * @author jao
 */
@ThreadSafe
public class PoolServers {

    public static final int DEFAULT_PORT = -1;

    public static final PoolServer get(PoolServerAddress address)
        throws PoolException {
        PoolServer server = servers.get(address);
        if (server == null) {
            String scheme = address.scheme();
            final Factory f = (scheme == null || TCP_SCM.equals(scheme))
                ? tcpFactory : factories.get(scheme);
            if (f == null) 
                throw new PoolServerAddress.BadAddress("Bad scheme: " + scheme);
            server = f.get(address);
            servers.put(address, server);
        }
        return server;
    }

    public interface Factory {
        PoolServer get(PoolServerAddress address) throws PoolException;
    }

    public static final boolean register(String scheme, Factory factory) {
        if (scheme == null || factory == null) return false;
        factories.put(scheme, factory);
        return true;
    }

    private static final String TCP_SCM = "tcp";

    private static final Map<String, Factory> factories =
        new ConcurrentHashMap<String, Factory>();
    private static final Factory tcpFactory =
        new com.oblong.jelly.pool.tcp.TCPServerFactory();

    private static final Map<PoolServerAddress, PoolServer> servers =
        new ConcurrentHashMap<PoolServerAddress, PoolServer>();

    private PoolServers() {}
}
