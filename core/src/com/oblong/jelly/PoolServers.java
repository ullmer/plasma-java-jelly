// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.Set;

/**
 *
 * @author jao
 */
public final class PoolServers {

    public static PoolServer get(PoolServerAddress address) {
        return com.oblong.jelly.pool.PoolServerFactory.get(address);
    }

    public static Set<PoolServer> servers(String scheme) {
        return com.oblong.jelly.pool.PoolServerFactory.servers(scheme);
    }

    public interface Listener {
        void serverAdded(PoolServer server, Set<PoolServer> current);
        void serverRemoved(PoolServer server, Set<PoolServer> current);
    }

    public static boolean registerListener(String scheme, Listener handler) {
        return com.oblong.jelly.pool.PoolServerFactory.registerListener(
            scheme, handler);
    }

    private PoolServers() {}
}
