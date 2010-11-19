// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServers;
import com.oblong.jelly.PoolServerAddress;

public abstract class PoolServerFactory {

    public abstract PoolServer getServer(PoolServerAddress address);
    public abstract Set<PoolServer> servers();
    public abstract boolean addListener(PoolServers.Listener listener);
    public abstract boolean isRemote();

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

    public static Set<PoolServer> servers(String scheme) {
        final PoolServerFactory f = getFactory(scheme);
        return f == null ? new HashSet<PoolServer>() : f.servers();
    }

    public static Set<PoolServer> remoteServers() {
        final Set<PoolServer> result = new HashSet<PoolServer>();
        for (PoolServerFactory f : factories.values()) {
            if (f.isRemote()) result.addAll(f.servers());
        }
        return result;
    }

    public static boolean addListener(String scheme,
                                      PoolServers.Listener listener) {
        if (scheme == null || listener == null) return false;
        final PoolServerFactory f = getFactory(scheme);
        return f != null && f.addListener(listener);
    }

    public static void addRemoteListener(PoolServers.Listener listener) {
        if (listener != null) {
            for (PoolServerFactory f : factories.values()) {
                if (f.isRemote()) f.addListener(listener);
            }
        }
    }

    public static PoolServerFactory getFactory(String scheme) {
        return factories.get(scheme);
    }

    public static boolean register(String scheme, PoolServerFactory factory) {
        if (scheme == null || factory == null) return false;
        return factories.put(scheme, factory) == null;
    }

    public static Set<PoolServer> cached(String scheme) {
        final Set<PoolServer> result = new HashSet<PoolServer>();
        for (PoolServer s : servers.values()) {
            if (s.address().scheme().equals(scheme)) result.add(s);
        }
        return result;
    }

    public static PoolServer cache(PoolServer server) {
        servers.put(server.address(), server);
        return server;
    }

    public static PoolServer remove(PoolServerAddress addr) {
        return servers.remove(addr);
    }

    private static final
    ConcurrentHashMap<String, PoolServerFactory> factories =
        new ConcurrentHashMap<String, PoolServerFactory>();

    private static final
    ConcurrentHashMap<PoolServerAddress, PoolServer> servers =
        new ConcurrentHashMap<PoolServerAddress, PoolServer>();

}