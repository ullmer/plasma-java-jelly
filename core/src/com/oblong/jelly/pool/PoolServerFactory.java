// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool;

import java.util.concurrent.ConcurrentHashMap;

import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServerAddress;

public abstract class PoolServerFactory {

    public abstract PoolServer getServer(PoolServerAddress address);

    public static PoolServer get(PoolServerAddress address) {
        PoolServer server = servers.get(address);
        if (server == null) {
            final String scheme = address.scheme();
            final PoolServerFactory f = getFactory(scheme);
            if (f != null) {
                server = f.getServer(address);
                final PoolServer old = servers.putIfAbsent(address, server);
                if (old != null) server = old;
            }
        }
        return server;
    }

    public static PoolServerFactory getFactory(String scheme) {
        return factories.get(scheme);
    }

    public static boolean register(String scheme, PoolServerFactory factory) {
        if (scheme == null || factory == null) return false;
        return factories.put(scheme, factory) == null;
    }

    private static ConcurrentHashMap<String, PoolServerFactory> factories =
        new ConcurrentHashMap<String, PoolServerFactory>();

    private static ConcurrentHashMap<PoolServerAddress, PoolServer> servers =
        new ConcurrentHashMap<PoolServerAddress, PoolServer>();
}