// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *  Unit Test for class PoolServerAddress
 *
 *
 * Created: Thu Jul  1 03:58:29 2010
 *
 * @author jao
 */
public class PoolServerAddressTest {

    @Test public void simple() throws PoolException {
        final PoolServerAddress addr = checkSelf("tcp", "foo", 22);
        assertEquals("tcp", addr.scheme());
        assertEquals("foo", addr.host());
        assertEquals(22, addr.port());
    }

    @Test public void emptyHost() throws PoolException {
        final PoolServerAddress addr = checkSelf("mem", null, -1);
        assertEquals("mem", addr.scheme());
        assertEquals(PoolServerAddress.DEFAULT_HOST, addr.host());
        assertEquals(PoolServerAddress.DEFAULT_PORT, addr.port());
    }

    @Test public void onlyHost() throws PoolException {
        final PoolServerAddress addr = new PoolServerAddress("host");
        assertEquals(PoolServerAddress.DEFAULT_SCHEME, addr.scheme());
        assertEquals("host", addr.host());
        assertEquals(PoolServerAddress.DEFAULT_PORT, addr.port());
        assertEquals(addr, new PoolServerAddress("", "host", -23));
    }

    @Test public void fromUri() throws PoolException {
        PoolServerAddress addr = PoolServerAddress.fromURI("host");
        assertEquals("tcp://host", addr.toString());
        addr = PoolServerAddress.fromURI("mem://foo");
        assertEquals("mem://foo", addr.toString());
        addr = PoolServerAddress.fromURI("mem://");
        assertEquals("mem://localhost", addr.toString());
    }

    @Test public void relative() {
        assertTrue(PoolServerAddress.isRelative("foo"));
        assertTrue(PoolServerAddress.isRelative("foo/bar"));
        assertTrue(PoolServerAddress.isRelative("/foo/bar"));
        assertFalse(PoolServerAddress.isRelative("mem:///foo"));
        assertFalse(PoolServerAddress.isRelative("tcp://foo/bar"));
    }

    private PoolServerAddress checkSelf(String s, String h, int p)
        throws PoolException {
        PoolServerAddress addr = new PoolServerAddress(s, h, p);
        assertEquals(new PoolServerAddress(addr.scheme(),
                                           addr.host(),
                                           addr.port()),
                     addr);
        assertEquals(addr, PoolServerAddress.fromURI(addr.toString()));
        return addr;
    }
}
