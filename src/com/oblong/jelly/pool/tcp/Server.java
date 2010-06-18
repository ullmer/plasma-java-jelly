// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.tcp;

import java.util.Set;

import com.oblong.jelly.Pool;
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

    public Server(PoolAddress address) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public Pool create(String name, PoolOptions opts) throws PoolException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void dispose(String name) throws PoolException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Pool find(String name) throws PoolException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<String> pools() throws PoolException {
        // TODO Auto-generated method stub
        return null;
    }

 
}
