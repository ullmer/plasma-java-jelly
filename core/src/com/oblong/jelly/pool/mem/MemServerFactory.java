// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.mem;

import java.util.Set;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServers;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.pool.PoolServerFactory;

@Immutable
public final class MemServerFactory extends PoolServerFactory {

    public static final String SCM = "mem";

    @Override public PoolServer getServer(PoolServerAddress address) {
        return new MemPoolServer(address);
    }

    @Override public Set<PoolServer> servers() {
        return PoolServerFactory.cachedServers(SCM);
    }

    @Override public boolean registerListener(PoolServers.Listener listener) {
        return false;
    }

    public static boolean register() {
        return register(SCM);
    }

    public static boolean register(String scheme) {
        return register(scheme, new MemServerFactory());
    }

}
