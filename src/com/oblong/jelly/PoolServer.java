// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * Created: Mon Jun 14 13:22:13 2010
 *
 * @author jao
 */
public abstract class PoolServer {

    public static final int DEFAULT_PORT = -1;

    public static final PoolServer connect(String scheme,
                                           String host,
                                           int port)
        throws PoolException {
        final Factory f =
            TCP_SCM.equals(scheme) ? tcpFactory : factories.get(scheme);
        return (f == null) ? null : f.get(scheme, host, port);
    }

    public static final PoolServer connect(String host, int port)
        throws PoolException {
        return tcpFactory.get(TCP_SCM, host, port);
    }

    public static final PoolServer connect(String host)
        throws PoolException {
        return connect(TCP_SCM, host, DEFAULT_PORT);
    }

    public abstract Pool create(String name, PoolOptions opts)
        throws PoolException;

    public abstract void dispose(String name) throws PoolException;

    public abstract Pool find(String name);

    public abstract Pool find(String name, PoolOptions opts)
        throws PoolException;

    public abstract Set<String> pools();

    public abstract Hose participate(String name) throws PoolException;

    public abstract Hose participate(String name, PoolOptions opts)
        throws PoolException;

    public interface Factory {
        PoolServer get(String scheme, String host, int port)
            throws PoolException;
    }

    public static final boolean register(String scheme, Factory factory) {
        if (scheme == null || factory == null) return false;
        factories.put(scheme, factory);
        return true;
    }

    private static final String TCP_SCM = "tcp";

    private static final Map<String, Factory> factories =
        new ConcurrentHashMap<String, Factory>();
    private static final Factory tcpFactory =
        new com.oblong.jelly.pool.tcp.Factory();
}
