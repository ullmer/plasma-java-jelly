// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.mem;

import java.util.Set;

import net.jcip.annotations.ThreadSafe;

import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.PoolServers;
import com.oblong.jelly.pool.PoolServerCache;
import com.oblong.jelly.pool.PoolServerFactory;

@ThreadSafe
public final class MemServerFactory extends PoolServerFactory {

    public static final String SCM = "mem";

    @Override public boolean isRemote() { return false; }

    @Override
    public PoolServer getServer(PoolServerAddress a, String n, String st) {
        final String qname = MemPoolServer.qualifiedName(a, n, st);
        final PoolServer s = cache.get(qname);
        return s == null ? cache.add(new MemPoolServer(a, n, st)) : s;
    }

    @Override public Set<PoolServer> servers(PoolServerAddress address,
                                             String name,
                                             String subtype) {
        return cache.get(address, name, subtype);
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

    private static PoolServerCache cache = new PoolServerCache();
}
