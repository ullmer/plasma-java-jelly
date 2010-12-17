// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServers;
import com.oblong.jelly.PoolServerAddress;

public abstract class PoolServerFactory {

    public abstract PoolServer getServer(PoolServerAddress address,
                                         String subtype);
    public abstract Set<PoolServer> servers();
    public abstract boolean addListener(PoolServers.Listener listener);
    public abstract boolean isRemote();

    public static PoolServer get(PoolServerAddress address, String st) {
        final String scheme = address.scheme();
        final PoolServerFactory f = getFactory(scheme);
        return f != null ? f.getServer(address, st) : null;
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

    public static PoolServerFactory unregister(String scheme) {
        return factories.remove(scheme);
    }

    public static boolean register(String scheme, PoolServerFactory factory) {
        if (scheme == null || factory == null) return false;
        return factories.put(scheme, factory) == null;
    }

    private static final
    ConcurrentHashMap<String, PoolServerFactory> factories =
        new ConcurrentHashMap<String, PoolServerFactory>();
}
