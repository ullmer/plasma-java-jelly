// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.tcp;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServers;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.pool.impl.Server;
import com.oblong.jelly.pool.impl.PoolConnectionFactory;

/**
 *
 * Created: Mon Jun 14 14:42:48 2010
 *
 * @author jao
 */
@Immutable
public final class TCPServerFactory implements PoolServers.Factory {

    @Override public PoolServer get(PoolServerAddress address)
        throws PoolException {
        return new Server(factory, address);
    }

    private static final PoolConnectionFactory factory =
        new TCPPoolConnection.Factory();
}
