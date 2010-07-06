// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assume.*;
import static org.junit.Assert.*;

import com.oblong.jelly.pool.NoSuchPoolException;
import com.oblong.jelly.pool.PoolExistsException;

/**
 *
 * Created: Mon Jul  5 22:13:18 2010
 *
 * @author jao
 */
public class PoolServerTestBase {

    public PoolServerTestBase() {
        server = null;
    }

    protected PoolServerTestBase(PoolServerAddress addr)
        throws PoolException {
        server = PoolServers.get(addr);
        assertNotNull(server);
        assertEquals(addr, server.address());
    }

    @Before public void clean() throws PoolException {
        assumeTrue(server != null);
        final Set<String> pools = server.pools();
        for (String n : pools) server.dispose(n);
    }

    @Test public void registration() throws PoolException {
        assumeTrue(server != null);
        final PoolAddress fa = poolAddress("foo");
        Pool.create(fa, null);
        Hose fh = Pool.participate(fa);
        Hose fhd = Pool.participate(fa, PoolOptions.SMALL);
        fhd = Pool.participate(fa);
        assertEquals(fa, fh.poolAddress());
        assertEquals(fa, fhd.poolAddress());
        fh.withdraw();
        assertTrue(fhd.isConnected());
        assertFalse(fh.isConnected());
        assertTrue(server.pools().contains(fa.poolName()));
    }

    @Test public void funnyNames() throws PoolException {
        assumeTrue(server != null);
        final String[] names = {"a pool%", "da-pool"};
        for (String n : names) Pool.create(poolAddress(n), null);
        final Set<String> pools = Pool.pools(server.address().toString());
        assertEquals(names.length, pools.size());
        for (String n : names) assertTrue(pools.contains(n));
    }

    @Test(expected=NoSuchPoolException.class)
    public void nonExistent() throws PoolException {
        assumeTrue(server != null);
        server.participate("non-existent-pool%%");
    }

    @Test public void duplicates() throws PoolException {
        assumeTrue(server != null);
        final PoolAddress a = poolAddress("bar");
        final Hose h = Pool.participate(a, PoolOptions.SMALL);
        checkDup(a);
        h.withdraw();
        checkDup(a);
    }

    protected PoolAddress poolAddress(String name) throws PoolException {
        return new PoolAddress(server.address(), name);
    }

    private void checkDup(PoolAddress a) throws PoolException {
        try {
            Pool.create(a, PoolOptions.LARGE);
        } catch (PoolExistsException e) {
            return;
        }
        fail("Duplicate pool creation");
    }

    private final PoolServer server;
}
