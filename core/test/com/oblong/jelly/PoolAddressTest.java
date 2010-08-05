// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *  Unit Test for class PoolAddress
 *
 *
 * Created: Thu Jul  1 16:25:07 2010
 *
 * @author jao
 */
public class PoolAddressTest {

    @Test public void uris() throws PoolException {
        PoolAddress a = PoolAddress.fromURI("mem:///foo");
        checkSelf(a);
        assertEquals("foo", a.poolName());
        assertEquals("mem", a.serverAddress().scheme());
        assertEquals("localhost", a.serverAddress().host());
        a = PoolAddress.fromURI("mem://host/foo/p ddfd`");
        checkSelf(a);
        assertEquals("foo/p ddfd`", a.poolName());
        assertEquals("mem://host", a.serverAddress().toString());
    }

    @Test public void noServer() throws PoolException {
        PoolAddress a = PoolAddress.fromURI("pool");
        checkSelf(a);
        assertEquals("pool", a.poolName());
        assertEquals("tcp://localhost", a.serverAddress().toString());
        a = PoolAddress.fromURI("pool/bar/baz");
        checkSelf(a);
        assertEquals("pool/bar/baz", a.poolName());
        assertEquals("tcp://localhost", a.serverAddress().toString());
    }

    private void checkSelf(PoolAddress a) throws PoolException {
        assertEquals(a, new PoolAddress(a.serverAddress(), a.poolName()));
        assertEquals(a, PoolAddress.fromURI(a.toString()));
    }
}
