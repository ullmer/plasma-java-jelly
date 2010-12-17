// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.mem;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.jcip.annotations.ThreadSafe;

import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.PoolServers;
import com.oblong.jelly.pool.PoolServerFactory;

@ThreadSafe
public final class MemServerFactory extends PoolServerFactory {

    public static final String SCM = "mem";

    @Override public boolean isRemote() { return false; }

    @Override
    public PoolServer getServer(PoolServerAddress address, String st) {
        PoolServer srv = servers.get(address);
        if (srv == null) {
            srv = new MemPoolServer(address);
            final PoolServer old = servers.putIfAbsent(address, srv);
            if (old != null) srv = old;
        }
        return srv;
    }

    @Override public Set<PoolServer> servers() {
        return new HashSet<PoolServer>(servers.values());
    }

    @Override public boolean addListener(PoolServers.Listener listener) {
        return false;
    }

    public static boolean register() {
        return register(SCM);
    }

    public static boolean register(String scheme) {
        return register(scheme, new MemServerFactory());
    }

    private static ConcurrentHashMap<PoolServerAddress, PoolServer> servers =
        new ConcurrentHashMap<PoolServerAddress, PoolServer>();
}
