// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created: Mon Jun 14 13:22:13 2010
 *
 * @author jao
 */
public abstract class PoolServer {

    public static final short DEFAULT_PORT = -1;

    public static final PoolServer connect(String scheme,
                                           String host,
                                           short port)
        throws PoolException {
        Factory f = null;
        synchronized (factories) {
            f = factories.get(scheme);
        }
        return (f == null) ? null : f.get(scheme, host, port);
    }

    public static final PoolServer connect(String host, short port)
        throws PoolException {
        return connect(TCP_SCM, host, port);
    }

    public static final PoolServer connect(String host)
        throws PoolException {
        return connect(TCP_SCM, host, DEFAULT_PORT);
    }

    public abstract Pool create(String name, PoolOptions opts)
        throws PoolException;

    public final void dispose(String name) throws PoolException {
        find(name).dispose();
    }

    public abstract Pool find(String name);

    public abstract Pool find(String name, PoolOptions opts)
        throws PoolException;

    public abstract List<Pool> list();

    public final Hose participate(String name) throws PoolException {
        return find(name).participate();
    }

    public final Hose participate(String name, PoolOptions opts)
        throws PoolException {
        return find(name, opts).participate();
    }


    public interface Factory {
        PoolServer get(String scheme, String host, short port)
            throws PoolException;
    }

    public static final boolean register(String scheme, Factory factory) {
        if (scheme == null || factory == null) return false;
        synchronized (factories) {
            factories.put(scheme, factory);
        }
        return true;
    }

    private static final String TCP_SCM = "tcp";

    private static final Map<String, Factory> factories;

    static {
        factories = new HashMap<String, Factory>();
        factories.put(TCP_SCM, new com.oblong.jelly.pool.tcp.Factory());
    }

}
