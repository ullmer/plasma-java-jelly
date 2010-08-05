// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.mem;

import java.io.IOException;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.pool.tcp.TCPProxy;

/**
 *
 * Created: Mon Jul  5 21:25:38 2010
 *
 * @author jao
 */
public final class TCPMemProxy implements Runnable {

    public TCPMemProxy(int port) throws PoolException, IOException {
        proxy = new TCPProxy(new MemPoolConnection.Factory(),
                             new PoolServerAddress("mem", "localhost", -1),
                             port);
    }


    @Override public void run() { proxy.run(); }

    public void exit() { proxy.exit(); }

    public PoolServerAddress tcpAddress() { return proxy.tcpAddress(); }

    private final TCPProxy proxy;

    static {
        MemServerFactory.register();
    }
}
