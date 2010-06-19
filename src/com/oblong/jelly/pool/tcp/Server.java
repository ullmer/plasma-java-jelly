// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.tcp;

import java.util.Set;

import com.oblong.jelly.Hose;
import com.oblong.jelly.PoolAddress;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolOptions;
import com.oblong.jelly.PoolServer;

/**
 *
 * Created: Mon Jun 14 16:22:00 2010
 *
 * @author jao
 */
final class Server implements PoolServer {

    Server(PoolAddress address) throws PoolException {
        assert address.scheme().equals("tcp");
        this.address = address;
    }

    @Override public void create(String name, PoolOptions opts)
        throws PoolException {
    }

    @Override public void dispose(String name) throws PoolException {
    }

    @Override public Hose participate(String name) throws PoolException {
        return null;
    }

    @Override public Hose participate(String name, PoolOptions opts)
        throws PoolException {
        return null;
    }

    @Override public Set<String> pools() throws PoolException {
        return null;
    }

    private final PoolAddress address;
}
