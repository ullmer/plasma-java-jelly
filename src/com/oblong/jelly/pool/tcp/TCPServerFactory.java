// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.tcp;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.jcip.annotations.ThreadSafe;

import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServers;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.pool.Server;
import com.oblong.jelly.pool.ServerConnectionFactory;

/**
 *
 * Created: Mon Jun 14 14:42:48 2010
 *
 * @author jao
 */
@ThreadSafe
public final class TCPServerFactory implements PoolServers.Factory {

    @Override public PoolServer get(PoolServerAddress address)
        throws PoolException {
        PoolServer server = servers.get(address);
        if (server == null) {
            server = new Server(factory, address);
            servers.put(address, server);
        }
        return server;
    }

    private static Map<PoolServerAddress, PoolServer> servers =
        new ConcurrentHashMap<PoolServerAddress, PoolServer>();
    
    private static final ServerConnectionFactory factory =
        new TCPServerConnection.Factory();
}
