// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.pool.PoolConnection;
import com.oblong.jelly.pool.PoolConnectionFactory;


/**
 *
 * Created: Fri Jul  2 14:42:16 2010
 *
 * @author jao
 */
public final class TCPProxy implements Runnable {

    public TCPProxy(PoolConnectionFactory factory, PoolServerAddress addr)
        throws IOException {
        this(factory, addr, PoolServerAddress.DEFAULT_PORT);
    }

    public TCPProxy(
        PoolConnectionFactory factory, PoolServerAddress address, int port)
        throws IOException {
        this.factory = factory;
        this.address = address;
        this.socket = new ServerSocket(port);
        this.exit = false;
        this.handlers = new ArrayList<TCPProxyHandler>();
        this.threads = new ArrayList<Thread>();
    }

    @Override public void run() {
        exit = false;
        while (!exit) {
            try {
                launchHandler(socket.accept(), factory.get(address));
            } catch (Exception e) {
                if (!socket.isClosed())
                    log.severe("Exception launching handler: " + e.getMessage());
            }
        }
        closeHandlers();
        waitChildren();
    }

    public PoolServerAddress tcpAddress() {
        try {
            return new PoolServerAddress("localhost", socket.getLocalPort());
        } catch (PoolException e) {
            assert false : "We know this address is well-formed";
            return null;
        }
    }

    public void exit() {
        exit = true;
        try {
            socket.close();
        } catch (IOException e) {
            log.warning("Exception closing socket: " + e.getMessage());
        }
    }

    private void launchHandler(Socket sock, PoolConnection pc)
        throws IOException {
        final TCPProxyHandler handler = new TCPProxyHandler(sock, pc);
        final Thread th = new Thread(handler);
        th.start();
        handlers.add(handler);
        threads.add(th);
    }

    private void closeHandlers() {
        for (TCPProxyHandler h : handlers) h.close();
    }

    private void waitChildren() {
        for (Thread t : threads)
            try { t.join(10); } catch (Exception e) {}
    }

    private final ServerSocket socket;
    private final PoolConnectionFactory factory;
    private final PoolServerAddress address;
    private final List<TCPProxyHandler> handlers;
    private final List<Thread> threads;
    private volatile boolean exit;

    private final Logger log = Logger.getLogger(getClass().toString());
}
