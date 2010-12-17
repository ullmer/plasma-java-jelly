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
                                         String name,
                                         String subtype);
    public abstract Set<PoolServer> servers(PoolServerAddress address,
                                            String name,
                                            String subtype);
    public abstract boolean addListener(PoolServers.Listener listener);
    public abstract boolean isRemote();

    public static PoolServer get(PoolServerAddress address,
                                 String name,
                                 String subtype) {
        final String scheme = address.scheme();
        final PoolServerFactory f = getFactory(scheme);
        return f != null ? f.getServer(address, name, subtype) : null;
    }

    public static Set<PoolServer> servers(String scheme,
                                          PoolServerAddress address,
                                          String name,
                                          String subtype) {
        final PoolServerFactory f = getFactory(scheme);
        return f == null
            ? new HashSet<PoolServer>()
            : f.servers(address, name, subtype);
    }

    public static Set<PoolServer> remoteServers(PoolServerAddress address,
                                                String name,
                                                String subtype) {
        Set<PoolServer> result = null;
        for (PoolServerFactory f : factories.values()) {
            if (f.isRemote()) {
                if (result == null)
                    result = f.servers(address, name, subtype);
                else
                    result.addAll(f.servers(address, name, subtype));
            }
        }
        return result == null ? new HashSet<PoolServer>() : result;
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
