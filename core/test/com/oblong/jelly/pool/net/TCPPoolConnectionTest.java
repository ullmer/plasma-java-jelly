// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.net;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolTestBase;

import static com.oblong.jelly.pool.net.Request.*;

import com.oblong.jelly.pool.mem.TCPMemProxy;
import com.oblong.jelly.pool.net.TCPConnection;

/**
 * Unit Test for TCP pool servers, using a MemPool-base TCPProxy
 * as the target PoolServer.
 *
 *
 * Created: Mon Jul  5 21:15:43 2010
 *
 * @author jao
 */
public class TCPPoolConnectionTest extends PoolTestBase {

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

    @Test public void supportedSets() throws PoolException, IOException {
        checkSupported(TCPConnection.defaultSupported);
        for (Request r : Request.values()) checkSupported(r);
        checkSupported(CREATE, DISPOSE, PARTICIPATE, PARTICIPATE_C, WITHDRAW,
                       OLDEST_INDEX, NEWEST_INDEX, DEPOSIT, NTH_PROTEIN, NEXT,
                       PROBE_FWD, PREV, PROBE_BACK, AWAIT_NEXT, LIST);
    }

    private static void checkSupported(Request req, Request... rest)
        throws PoolException, IOException {
        checkSupported(EnumSet.of(req, rest));
    }

    private static void checkSupported(Set<Request> supp)
        throws PoolException, IOException {
        final byte[] data = TCPConnection.supportedToData(supp);
        final ByteArrayInputStream is = new ByteArrayInputStream(data);
        assertEquals(supp, TCPConnection.readSupported(is, 3));
    }


    private static TCPMemProxy proxy;
    private static Thread proxyThread;
}
