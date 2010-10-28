// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.net;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

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

    @Test public void await() throws Exception {
        HoseGangTests.waitTest(new PoolAddress(MEM_ADDR, "a"),
                               new PoolAddress(TCP_ADDR, "b"),
                               new PoolAddress(TCP_ADDR, "c"));
    }

    @Test public void wakeUp() throws Exception {
        HoseGangTests.wakeUpTest(new PoolAddress(TCP_ADDR, "b"),
                                 new PoolAddress(TCP_ADDR, "c"));
    }

    @AfterClass public static void clean() throws PoolException {
        PoolServerTestBase.clean(SERVER);
    }

    @BeforeClass public static void init() throws PoolException {
        MEM_ADDR = PoolServerAddress.fromURI("mem://localhost");
        SERVER = PoolServerTestBase.externalServer();
        assertNotNull(SERVER);
        TCP_ADDR = SERVER.address();
        clean();
    }

    private static PoolServer SERVER;
    private static PoolServerAddress TCP_ADDR;
    private static PoolServerAddress MEM_ADDR;
}
