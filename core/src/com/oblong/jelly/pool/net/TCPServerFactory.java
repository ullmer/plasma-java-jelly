// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.net;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServers;
import com.oblong.jelly.PoolServerAddress;

/**
 *
 * Created: Mon Jun 14 14:42:48 2010
 *
 * @author jao
 */
@Immutable
public final class TCPServerFactory implements PoolServers.Factory {

    @Override public PoolServer get(PoolServerAddress address) {
        return new Server(factory, address);
    }

    public static void register() {
        PoolServers.register("tcp", new TCPServerFactory());
    }

    private static final NetConnectionFactory factory =
        new TCPPoolConnection.Factory();
}
