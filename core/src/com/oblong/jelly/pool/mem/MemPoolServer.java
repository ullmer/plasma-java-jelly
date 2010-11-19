// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.mem;

import java.util.HashSet;
import java.util.Set;

import com.oblong.jelly.Hose;
import com.oblong.jelly.NoSuchPoolException;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolExistsException;
import com.oblong.jelly.PoolOptions;
import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServerAddress;

final class MemPoolServer implements PoolServer {

    @Override public PoolServerAddress address() { return address; }

    @Override public String name() { return address.toString(); }

    @Override public Set<String> subtypes() { return new HashSet<String>(); }

    @Override public void create(String name, PoolOptions opts)
        throws PoolException {
        if (MemPool.create(name) == null) throw new PoolExistsException(0);
    }

    @Override public void dispose(String name) throws PoolException {
        if (!MemPool.dispose(name)) throw new NoSuchPoolException(0);
    }

    @Override public Hose participate(String name) throws PoolException {
        final MemPool pool = MemPool.get(name);
        if (pool == null) throw new NoSuchPoolException(0);
        return new MemHose(pool, address);
    }

    @Override public Hose participate(String name, PoolOptions opts)
        throws PoolException {
        MemPool.create(name);
        return participate(name);
    }

    @Override public Set<String> pools() throws PoolException {
        return MemPool.names();
    }

    MemPoolServer(PoolServerAddress address) {
    	this.address = address;
    }

    private final PoolServerAddress address;
}
