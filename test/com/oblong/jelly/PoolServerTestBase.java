// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.Set;

import org.junit.After;
import org.junit.Before;

import static org.junit.Assume.*;
import static org.junit.Assert.*;


/**
 * Base class for tests needing a PoolServerInstance.
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

    @Before public void maybeDisable() {
        assumeTrue(server != null);
    }
    
    @After public void clean() throws PoolException {
        if (server != null) {
            final Set<String> pools = server.pools();
            for (String n : pools) server.dispose(n);
        }
    }

    protected PoolAddress poolAddress(String name) throws PoolException {
        return new PoolAddress(server.address(), name);
    }

    protected final PoolServer server;
}
