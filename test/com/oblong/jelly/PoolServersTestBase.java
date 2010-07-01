// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *  Base unit test for class PoolServers
 *
 *
 * Created: Thu Jul  1 17:26:54 2010
 *
 * @author jao
 */
public class PoolServersTestBase {

    public PoolServersTestBase(String scheme) {
        this.scheme = scheme;
    }

    @Test public void addresses() throws PoolException {
        final String[] as = {"local", "foo:2349", ""};
        for (String a : as) {
            final String uri = scheme + "://" + a;
            final PoolServerAddress addr = PoolServerAddress.fromURI(uri);
            assertEquals(scheme, addr.scheme());
            final PoolServer s = PoolServers.get(addr);
            assertTrue(uri, s != null);
            assertEquals(addr, s.address());
        }
    }

    @Test public void unregistered() throws PoolException {
        final PoolServerAddress addr =
            new PoolServerAddress(scheme + "aaaa", "localhost", 22);
        assertTrue(null == PoolServers.get(addr));
    }

    private final String scheme;
}
