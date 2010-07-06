// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.tcp;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;

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

    @BeforeClass public static void startUp()
        throws IOException, PoolException {
        proxy = new TCPMemProxy(60005);
        proxyThread = new Thread(proxy);
        proxyThread.start();
    }

    @AfterClass public static void shutDown() throws InterruptedException {
        proxy.exit();
        proxyThread.join(100);
    }

    public TCPHoseTest() throws PoolException {
        super(proxy.tcpAddress());
    }

    private static TCPMemProxy proxy;
    private static Thread proxyThread;
}
