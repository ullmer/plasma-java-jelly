// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import com.oblong.jelly.pool.mem.TCPMemProxy;
import com.oblong.util.Pair;

/**
 * Unit tests for class HoseGang.
 *
 *
 * @author jao
 */
public class HoseGangTest {

    @Test public void emptyMem() throws Exception {
        HoseGangTests.emptyTest(new PoolAddress(MEM_ADDR, "p1"),
                                new PoolAddress(MEM_ADDR, "p2"));
    }

    @Test public void emptyTCP() throws Exception {
        HoseGangTests.emptyTest(new PoolAddress(TCP_ADDR, "p1"),
                                new PoolAddress(TCP_ADDR2, "p2"));
    }

    @Test public void memSeq() throws Exception {
        HoseGangTests.seqTest(new PoolAddress(MEM_ADDR, "a"),
                              new PoolAddress(MEM_ADDR, "b"),
                              new PoolAddress(MEM_ADDR, "c"));
    }

    @Test public void tcpSeq() throws Exception {
        HoseGangTests.seqTest(new PoolAddress(TCP_ADDR, "a"),
                              new PoolAddress(TCP_ADDR2, "b"),
                              new PoolAddress(TCP_ADDR, "c"));
    }

    @Test public void mixSeq() throws Exception {
        HoseGangTests.seqTest(new PoolAddress(MEM_ADDR, "a"),
                              new PoolAddress(TCP_ADDR2, "b"),
                              new PoolAddress(TCP_ADDR, "c"));
    }

    @Test public void await() throws Exception {
        HoseGangTests.waitTest(new PoolAddress(MEM_ADDR, "a"),
                               new PoolAddress(TCP_ADDR2, "b"),
                               new PoolAddress(TCP_ADDR, "c"));
    }

    @Test public void wakeUp() throws Exception {
        HoseGangTests.wakeUpTest(new PoolAddress(MEM_ADDR, "a"),
                                 new PoolAddress(TCP_ADDR2, "b"),
                                 new PoolAddress(TCP_ADDR, "c"));
    }


    @BeforeClass public static void openProxy() {
        try {
            MEM_ADDR = PoolServerAddress.fromURI("mem://localhost");
            Pair<TCPMemProxy, Thread> p = startProxy();
            proxy = p.first();
            proxyThread = p.second();
            TCP_ADDR = proxy.tcpAddress();
            p = startProxy();
            proxy2 = p.first();
            proxyThread2 = p.second();
            TCP_ADDR2 = proxy2.tcpAddress();
        } catch (Exception e) {
            fail("Initialization error: " + e);
        }
    }

    @AfterClass public static void closeProxy() {
        proxy.exit();
        proxy2.exit();
        try {
            proxyThread.join(100);
            proxyThread2.join(100);
        } catch (Exception e) {
        }
    }

    private static Pair<TCPMemProxy,Thread> startProxy() {
        try {
            final TCPMemProxy proxy = new TCPMemProxy(0);
            final Thread thread = new Thread(proxy);
            thread.start();
            return Pair.create(proxy, thread);
        } catch (Exception e) {
            fail("Initialization error: " + e);
            return null;
        }
    }

    private static Thread proxyThread;
    private static Thread proxyThread2;
    private static TCPMemProxy proxy;
    private static TCPMemProxy proxy2;
    private static PoolServerAddress TCP_ADDR;
    private static PoolServerAddress TCP_ADDR2;
    private static PoolServerAddress MEM_ADDR;

}
