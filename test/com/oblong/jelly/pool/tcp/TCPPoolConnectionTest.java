// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.tcp;

import java.io.IOException;

import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolServerTestBase;
import com.oblong.jelly.pool.mem.TCPMemProxy;

/**
 * Unit Test for class TCPPoolConnection
 *
 *
 * Created: Mon Jul  5 21:15:43 2010
 *
 * @author jao
 */
public class TCPPoolConnectionTest extends PoolServerTestBase {

    @BeforeClass public static void startUp()
        throws IOException, PoolException {
        proxy = new TCPMemProxy(60000);
        proxyThread = new Thread(proxy);
        proxyThread.start();
    }

    @AfterClass public static void shutDown() throws InterruptedException {
        proxy.exit();
        proxyThread.join(100);
    }

    public TCPPoolConnectionTest() throws PoolException {
        super(proxy.tcpAddress());
    }

    private static TCPMemProxy proxy;
    private static Thread proxyThread;
}
