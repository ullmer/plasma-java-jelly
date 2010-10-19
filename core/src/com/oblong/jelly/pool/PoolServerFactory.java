// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool;

import java.util.concurrent.ConcurrentHashMap;

import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServerAddress;

public abstract class PoolServerFactory {
 
    public abstract PoolServer get(PoolServerAddress address);

    public static PoolServerFactory get(String scheme) {
        return factories.get(scheme);
    }
    
    public static boolean register(String scheme, PoolServerFactory factory) {
        if (scheme == null || factory == null) return false;
        return factories.put(scheme, factory) == null;
    }

    private static ConcurrentHashMap<String, PoolServerFactory> factories =
        new ConcurrentHashMap<String, PoolServerFactory>();

}