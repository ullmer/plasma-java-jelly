// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import java.util.Set;
import java.util.logging.Logger;

import com.oblong.jelly.PoolServer;

final class ServerInfo {

    ServerInfo(PoolServer s) {
        this(s, s.name());
    }

    ServerInfo(PoolServer s, String n) {
        server = s;
        name = n;
        poolNumber = UNINITIALIZED;
        pools = null;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof ServerInfo)) return false;
        return name.equals(((ServerInfo)o).name);
    }

    void updatePools () {
        try {
            pools = server.pools();
            poolNumber = pools.size();
        } catch (Exception e) {
            Logger.getLogger("Ponder").info(
                "Error connection to server: " + e.getMessage());
            poolNumber = CON_ERR;
        }
    }

    String name() { return name; }

    Set<String> pools() { return pools; }

    int poolNumber() { return poolNumber; }

    String poolNumberStr() {
        String txt;
        switch (poolNumber) {
        case UNINITIALIZED: txt = "scanning"; break;
        case 1: txt = "1 pool"; break;
        default: txt = poolNumber + " pools"; break;
        }
        return txt;
    }

    PoolServer server() { return server; }

    boolean connectionError() { return poolNumber == CON_ERR; }

    void clearPools() { poolNumber = UNINITIALIZED; }

    void nextName(int n) { name = name + " #" + n; }

    private static final int CON_ERR = -2;
    private static final int UNINITIALIZED = -1;

    private final PoolServer server;
    private volatile Set<String> pools;
    private volatile int poolNumber;
    private volatile String name;
}

