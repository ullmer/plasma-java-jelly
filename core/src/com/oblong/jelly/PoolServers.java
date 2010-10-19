// Copyright (c) 2010 Oblong Industries
// Created: Sat Jun 19 00:33:27 2010

package com.oblong.jelly;

import java.util.concurrent.ConcurrentHashMap;

import com.oblong.jelly.pool.PoolServerFactory;

import net.jcip.annotations.ThreadSafe;

/**
 * A factory of PoolServer instances.
 *
 * <p> This is a factory for PoolServer instances, obtainable given
 * the corresponding pool address.
 *
 * <p> Creation of the actual PoolServer instances is delegated, on a
 * per-scheme basis, to instances of the embedded PoolServers.Factory
 * interface, and the class offers implementors of new pool schema the
 * possibility of registering those new factories.
 *
 * <p> If you're not implementing a new pool server accessor, you can
 * safely ignore all methods and interfaces in this class, except for
 * {@link #get}, the factory method giving you access to new
 * PoolServer instances.
 *
 * <p> If you're happy with the interface provided by {@link Pool},
 * you can even forget about PoolServer instances at all.
 *
 * @author jao
 */
@ThreadSafe
public final class PoolServers {
    /**
     * Provides an object implementing PoolServer given its address.
     *
     * <p> Pool servers are uniquely identified by their address,
     * which acts in this respect as a URI. This method actually
     * returns the same object when called repeatedly with the same
     * argument.
     *
     * <p> If there's no PoolServer with the given address (for
     * instance, because its protocol is not registered), this method
     * returns null.
     */
    public static PoolServer get(PoolServerAddress address) {
        PoolServer server = servers.get(address);
        if (server == null) {
            final String scheme = address.scheme();
            final PoolServerFactory f = PoolServerFactory.get(scheme);
            if (f != null) {
                server = f.get(address);
                final PoolServer old = servers.putIfAbsent(address, server);
                if (old != null) server = old;
            }
        }
        return server;
    }

     static {
        com.oblong.jelly.pool.net.TCPServerFactory.register();
        com.oblong.jelly.pool.mem.MemServerFactory.register();
    }

    private static ConcurrentHashMap<PoolServerAddress, PoolServer> servers =
        new ConcurrentHashMap<PoolServerAddress, PoolServer>();

    private PoolServers() {}
}
