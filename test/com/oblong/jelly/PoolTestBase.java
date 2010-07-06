// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.*;

import com.oblong.jelly.pool.PoolExistsException;
import com.oblong.jelly.pool.NoSuchPoolException;

/**
 * Base class defining PoolServer and Pool-related tests.
 *
 * Created: Tue Jul  6 14:52:44 2010
 *
 * @author jao
 */
public class PoolTestBase extends PoolServerTestBase {

    public PoolTestBase() {}

    protected PoolTestBase(PoolServerAddress addr) throws PoolException {
        super(addr);
    }

    @Test public void registration() throws PoolException {
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
        final String[] names = {"a pool%", "da-pool"};
        for (String n : names) Pool.create(poolAddress(n), null);
        final Set<String> pools = Pool.pools(server.address().toString());
        assertEquals(names.length, pools.size());
        for (String n : names) assertTrue(pools.contains(n));
    }

    @Test(expected=NoSuchPoolException.class)
    public void nonExistent() throws PoolException {
        server.participate("non-existent-pool%%");
    }

    @Test public void duplicates() throws PoolException {
        final PoolAddress a = poolAddress("bar");
        final Hose h = Pool.participate(a, PoolOptions.SMALL);
        checkDup(a);
        h.withdraw();
        checkDup(a);
    }

    private void checkDup(PoolAddress a) throws PoolException {
        try {
            Pool.create(a, PoolOptions.LARGE);
        } catch (PoolExistsException e) {
            return;
        }
        fail("Duplicate pool creation");
    }
}
