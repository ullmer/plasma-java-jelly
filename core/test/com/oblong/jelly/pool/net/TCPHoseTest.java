// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.net;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import com.oblong.jelly.HoseTests;
import com.oblong.jelly.pool.mem.TCPMemProxy;

/**
 * Unit Test for hose operations on TCP pools, using a MemPool-base
 * TCPProxy as the target PoolServer.
 *
 * Created: Tue Jul  6 15:25:02 2010
 *
 * @author jao
 */
public class TCPHoseTest {

    @Test public void hoseName() throws Exception { tests.hoseName(); }

    @Test public void deposit() throws Exception { tests.deposit(); }

    @Test public void next() throws Exception { tests.next(); }
    @Test public void await() throws Exception { tests.await(); }
    @Test public void timeout() throws Exception { tests.awaitTimeout(); }
    @Test public void awaitNext() throws Exception { tests.awaitNext(); }

    @Test public void nth() throws Exception { tests.nth(); }
    @Test public void previous() throws Exception { tests.previous(); }
    @Test public void current() throws Exception { tests.current(); }

    @Test public void range() throws Exception { tests.range(); }

    @Test public void matchingAll() throws Exception { tests.matchingAll(); }
    @Test public void matchingOne() throws Exception { tests.matchingOne(); }
    @Test public void matchSome() throws Exception { tests.matchingSome(); }

    @Test public void poll() throws Exception { tests.poll(); }
    @Test public void cancelPoll() throws Exception { tests.cancelledPoll(); }
    @Test public void matchPollAll() throws Exception {
        tests.matchingPollAll();
    }
    @Test public void matchPollOne() throws Exception {
        tests.matchingPollOne();
    }
    @Test public void matchPollSome() throws Exception {
        tests.matchingPollSome();
    }

    @BeforeClass public static void openProxy() {
        try {
            proxy = new TCPMemProxy(0);
            proxyThread = new Thread(proxy);
            proxyThread.start();
            tests = new HoseTests(proxy.tcpAddress());
        } catch (Exception e) {
            fail("Initialization error: " + e);
        }
    }

    @AfterClass public static void closeProxy() {
        tests.cleanUp();
        proxy.exit();
        try { proxyThread.join(10); } catch (Exception e) {}
    }

    private static TCPMemProxy proxy;
    private static Thread proxyThread;
    private static HoseTests tests;
}
