// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.net;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

import com.oblong.jelly.HoseTestBase;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.pool.mem.TCPMemProxy;

/**
 * Unit Test for hose operations on TCP pools, using a MemPool-base
 * TCPProxy as the target PoolServer.
 *
 * Created: Tue Jul  6 15:25:02 2010
 *
 * @author jao
 */
public class TCPHoseTest extends HoseTestBase {

    public TCPHoseTest() throws PoolException {
        super(proxy.tcpAddress());
    }

    @BeforeClass public static void openProxy() {
        try {
            proxy = new TCPMemProxy(0);
            proxyThread = new Thread(proxy);
            proxyThread.start();
        } catch (Exception e) {
            fail("Initialization error: " + e);
        }
    }

    @AfterClass public static void closeProxy() {
        proxy.exit();
        try { proxyThread.join(10); } catch (Exception e) {}
    }

    private static TCPMemProxy proxy;
    private static Thread proxyThread;

}
