// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.net;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assume.*;

import com.oblong.jelly.HoseGangTests;
import com.oblong.jelly.PoolAddress;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.PoolServerTestBase;

/**
 * Unit tests for HoseGang using an external server.
 *
 * @author jao
 */
public class ExternalHoseGangTest {

    @Test public void emptyTCP() throws Exception {
        HoseGangTests.emptyTest(new PoolAddress(TCP_ADDR, "p1"),
                                new PoolAddress(TCP_ADDR, "p2"));
    }

    @Test public void emptyMix() throws Exception {
        HoseGangTests.emptyTest(new PoolAddress(MEM_ADDR, "p1"),
                                new PoolAddress(TCP_ADDR, "p2"));
    }

    @Test public void tcpSeq() throws Exception {
        HoseGangTests.seqTest(new PoolAddress(TCP_ADDR, "a"),
                              new PoolAddress(TCP_ADDR, "b"),
                              new PoolAddress(TCP_ADDR, "c"));
    }

    @Test public void mixSeq() throws Exception {
        HoseGangTests.seqTest(new PoolAddress(MEM_ADDR, "a"),
                              new PoolAddress(TCP_ADDR, "b"),
                              new PoolAddress(TCP_ADDR, "c"));
    }

    @Test public void async() throws Exception {
        HoseGangTests.asyncTest(new PoolAddress(TCP_ADDR, "a"),
                                new PoolAddress(TCP_ADDR, "b"));
    }

    @Test public void await() throws Exception {
        HoseGangTests.waitTest(new PoolAddress(MEM_ADDR, "a"),
                               new PoolAddress(TCP_ADDR, "b"),
                               new PoolAddress(TCP_ADDR, "c"));
    }

    @Test public void wakeUp() throws Exception {
        HoseGangTests.wakeUpTest(new PoolAddress(TCP_ADDR, "b"),
                                 new PoolAddress(TCP_ADDR, "c"));
    }

    @Before public void makeEclipseHappy() {
        assumeTrue(SERVER != null);
    }

    @AfterClass public static void clean() throws PoolException {
        PoolServerTestBase.clean(SERVER);
    }

    @BeforeClass public static void init() throws PoolException {
        MEM_ADDR = PoolServerAddress.fromURI("mem://localhost");
        SERVER = PoolServerTestBase.externalServer();
        // assertNotNull(SERVER);
        TCP_ADDR = SERVER == null ? null : SERVER.address();
        clean();
    }

    @Ignore("unrelated test for bug hunting")
    @Test public void foo() throws Exception {
        final PoolAddress addr = new PoolAddress(TCP_ADDR, "foo");
        try { com.oblong.jelly.Pool.dispose(addr); } catch (Exception e) {}
        com.oblong.jelly.Pool.create(addr, null);
        final com.oblong.jelly.Protein[] ps = HoseGangTests.deposit(addr, 2);
        final com.oblong.jelly.Hose h =
            com.oblong.jelly.Pool.participate(addr);
        h.rewind();
        org.junit.Assert.assertTrue(h.poll());
        // org.junit.Assert.assertEquals(ps[0], h.peek());
        org.junit.Assert.assertEquals(ps[0], h.next());
        org.junit.Assert.assertTrue(h.poll());
        // org.junit.Assert.assertEquals(ps[1], h.peek());
        org.junit.Assert.assertEquals(ps[1], h.next());
    }

    private static PoolServer SERVER;
    private static PoolServerAddress TCP_ADDR;
    private static PoolServerAddress MEM_ADDR;
}
