// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.Set;

import org.junit.Before;

import static org.junit.Assume.*;
import static org.junit.Assert.*;

import com.oblong.jelly.PoolException;

/**
 * Base class for tests needing a PoolServer instance.
 *
 * Created: Mon Jul  5 22:13:18 2010
 *
 * @author jao
 */
public class PoolServerTestBase {

    public PoolServerTestBase() {
        server = null;
    }

    public PoolServerTestBase(PoolServer s) throws PoolException {
        server = s;
        // clean();
    }

    protected PoolServerTestBase(PoolServerAddress addr)
        throws PoolException {
        server = Pool.getServer(addr);
        assertNotNull(server);
        assertEquals(addr, server.address());
    }

    @Before public void maybeDisable() {
        assumeTrue(server != null);
    }

    public static void clean(PoolServer server) throws PoolException {
        if (server != null) {
            final Set<String> pools = server.pools();
            for (String n : pools) server.dispose(n);
        }
    }

    protected PoolAddress poolAddress(String name) throws PoolException {
        return new PoolAddress(server.address(), name);
    }

    protected static PoolServer externalServer() {
        String uri = System.getProperty("com.oblong.jelly.externalServer");
        PoolServer s = null;
        if (uri != null && !uri.isEmpty()) {
            try {
                s = Pool.getServer(PoolServerAddress.fromURI(uri));
            } catch (PoolException e) {
                fail(e.getMessage());
            }
            assertNotNull("URI: " + uri, s);
        }
        return s;
    }

    protected final PoolServer server;
}
