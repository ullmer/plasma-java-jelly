// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 *  Base unit test for class PoolServers
 *
 *
 * Created: Thu Jul  1 17:26:54 2010
 *
 * @author jao
 */
public class PoolServersTestBase {

    public PoolServersTestBase () { scheme = null; }

    protected PoolServersTestBase(String scheme) {
        this.scheme = scheme;
        assertNotNull(this.scheme);
    }

    @Test public void addresses() throws PoolException {
        assumeTrue(scheme != null);
        final String[] as = {"local", "foo:2349", ""};
        for (String a : as) {
            final String uri = scheme + "://" + a;
            final PoolServerAddress addr = PoolServerAddress.fromURI(uri);
            assertEquals(scheme, addr.scheme());
            final PoolServer s = PoolServers.get(addr);
            assertNotNull(uri, s);
            assertEquals(addr, s.address());
        }
    }

    @Test public void unregistered() throws PoolException {
        assumeTrue(scheme != null);
        final PoolServerAddress addr =
            new PoolServerAddress(scheme + "aaaa", "localhost", 22);
        assertNull(PoolServers.get(addr));
    }

    private final String scheme;
}
