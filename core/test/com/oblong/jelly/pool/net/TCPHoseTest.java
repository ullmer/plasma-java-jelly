// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.net;

import com.oblong.jelly.util.ExceptionHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
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
public class TCPHoseTest extends HoseTests.Tests<TCPConnection> {

    @BeforeClass public static void openProxy() {
        try {
            proxy = new TCPMemProxy(0);
            proxyThread = new Thread(proxy);
            proxyThread.start();
            initTests(proxy.tcpAddress());
        } catch (Exception e) {
            ExceptionHandler.handleException(e);
            fail("Initialization error: " + e);
        }
    }

    @AfterClass public static void closeProxy() {
        cleanUpTests();
        proxy.exit();
        try { proxyThread.join(10); } catch (Exception e) {
            ExceptionHandler.handleException(e);
        }
    }

    private static TCPMemProxy proxy;
    private static Thread proxyThread;
}
