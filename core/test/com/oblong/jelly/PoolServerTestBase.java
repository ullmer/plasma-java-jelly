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

    @Before public void maybeDisable() {
        assumeTrue(server != null);
    }

    public static void clean(PoolServer server) throws PoolException {
        if (server != null) {
            final Set<String> pools = server.pools();
            for (String n : pools) server.dispose(n);
        }
    }

    public static PoolServer externalServer() {
        String uri = System.getProperty("com.oblong.jelly.externalServer");

        PoolServer s = null;
        if (uri != null && !uri.isEmpty()) {
	        System.out.println("uri is "+uri);
            try {
                s = PoolServers.get(PoolServerAddress.fromURI(uri));
            } catch (PoolException e) {
                fail(e.getMessage());
            }
            assertNotNull("URI: " + uri, s);
        } else {
	        System.err.println("uri is null or empty: unable to get property com.oblong.jelly.externalServer");
        }
        return s;
    }

    protected PoolServerTestBase(PoolServerAddress addr)
        throws PoolException {
        server = PoolServers.get(addr);
        assertNotNull(server);
        assertEquals(addr, server.address());
    }

    protected PoolAddress poolAddress(String name) throws PoolException {
        return new PoolAddress(server.address(), name);
    }

    protected final PoolServer server;
}
