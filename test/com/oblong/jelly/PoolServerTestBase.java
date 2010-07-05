// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.Set;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.oblong.jelly.pool.NoSuchPoolException;

/**
 *
 * Created: Mon Jul  5 22:13:18 2010
 *
 * @author jao
 */
public class PoolServerTestBase {

    public PoolServerTestBase(PoolServerAddress addr) throws PoolException {
        server = PoolServers.get(addr);
        assertTrue(server != null);
        assertEquals(addr, server.address());
    }

    @Before public void clean() throws PoolException {
        Set<String> pools = server.pools();
        for (String n : pools) server.dispose(n);
    }

    @Test public void nonExistent() throws PoolException {
        try {
            server.participate("non-existent-pool%%");
        } catch (NoSuchPoolException e) {
            return;
        }
        fail("Non existent pool apparently found");
    }

    @Test public void registration() throws PoolException {
        PoolAddress fa = new PoolAddress(server.address(), "foo");
        Pool.create(fa, null);
        Hose fh = Pool.participate(fa);
    }

    private final PoolServer server;
}
