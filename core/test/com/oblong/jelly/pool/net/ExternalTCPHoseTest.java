// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.net;

import org.junit.BeforeClass;

import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServerTestBase;
import com.oblong.jelly.HoseTests;

/**
 * Tests for Hose operations against an external TCP pool server.
 *
 * Created: Wed Jul 21 12:29:22 2010
 *
 * @author jao
 */
public class ExternalTCPHoseTest extends HoseTests.Tests<TCPConnection> {

    @BeforeClass public static void setUp() throws Exception {
        final PoolServer s = PoolServerTestBase.externalServer();
        if (s != null) initTests(s.address());
    }
}
