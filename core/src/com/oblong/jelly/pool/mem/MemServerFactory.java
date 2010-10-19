// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.mem;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.pool.PoolServerFactory;

@Immutable
public final class MemServerFactory extends PoolServerFactory {

    public static final String SCM = "mem";

    @Override public PoolServer getServer(PoolServerAddress address) {
        return new MemPoolServer(address);
    }

    public static boolean register() {
        return register(SCM);
    }

    public static boolean register(String scheme) {
        return register(scheme, new MemServerFactory());
    }

}
