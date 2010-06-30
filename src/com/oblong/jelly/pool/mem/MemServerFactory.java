// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.mem;

import java.util.concurrent.ConcurrentHashMap;

import net.jcip.annotations.ThreadSafe;

import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.PoolServers;
import com.oblong.jelly.pool.impl.Server;
import com.oblong.jelly.pool.impl.PoolConnectionFactory;

/**
 *
 *
 * Created: Tue Jun 29 19:37:16 2010
 *
 * @author jao
 */
@ThreadSafe
public final class MemServerFactory implements PoolServers.Factory {

    @Override public PoolServer get(PoolServerAddress address)
        throws PoolException {
        PoolServer server = servers.get(address);
        if (server == null) {
            server = 
                servers.putIfAbsent(address, new Server(factory, address));
        }
        return server;
    }

    private static ConcurrentHashMap<PoolServerAddress, PoolServer> servers =
        new ConcurrentHashMap<PoolServerAddress, PoolServer>();

    private static final PoolConnectionFactory factory =
        new MemPoolConnection.Factory();
}
