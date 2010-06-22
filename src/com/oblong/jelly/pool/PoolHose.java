package com.oblong.jelly.pool;

import com.oblong.jelly.Hose;

final class PoolHose implements Hose {

    PoolHose(ServerConnection conn, String pn) {
        connection = conn;
        poolName = pn;
        name = connection.address() + "/" + poolName;
    }
    
    @Override public String name() {
        return name;
    }

    @Override public void setName(String n) {
        name = n;
    }
    
    @Override public String poolName() {
        return poolName;
    }
    
    private final ServerConnection connection;
    private final String poolName;
    private String name;
}
