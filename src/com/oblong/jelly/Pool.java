// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

/**
 *
 * Created: Thu Jun  3 10:08:24 2010
 *
 * @author jao
 */
public abstract class Pool {

    public abstract PoolAddress address();
    public abstract PoolOptions options();

    public abstract Hose participate() throws PoolException;
    public abstract void dispose() throws PoolException;

    public static final Pool lookup(PoolAddress addr) throws PoolException {
        return factory.lookup(addr);
    }

    public static final Pool lookup(String addr) throws PoolException {
        return factory.lookup(new PoolAddress(addr));
    }

    public static final void dispose(PoolAddress addr) throws PoolException {
        lookup(addr).dispose();
    }

    public static final void dispose(String addr) throws PoolException {
        lookup(new PoolAddress(addr)).dispose();
    }

    public static final Pool create(PoolAddress addr) throws PoolException {
        return create(addr, null);
    }

    public static final Pool create(String addr) throws PoolException {
        return create(new PoolAddress(addr), null);
    }

    public static final Pool create(PoolAddress addr, PoolOptions opts)
        throws PoolException {
        if (addr == null) throw new PoolAddress.BadAddress("Null addr");
        return factory.create(addr, opts == null ? DEF_OPTS : opts);
    }

    public static final Pool create(String addr, PoolOptions opts)
        throws PoolException {
        return create(new PoolAddress(addr), opts);
    }

    public static final Hose participate(PoolAddress addr)
        throws PoolException {
        return factory.lookup(addr).participate();
    }

    public static final Hose participate(String addr) throws PoolException {
        return participate(new PoolAddress(addr));
    }

    public static final Hose participate(PoolAddress addr, PoolOptions opts)
        throws PoolException {
        if (addr == null) throw new PoolAddress.BadAddress("Null addr");
        return factory.lookup(addr, opts == null ? DEF_OPTS : opts)
                      .participate();
    }

    public static final Hose participate(String addr, PoolOptions opts)
        throws PoolException {
        return participate(new PoolAddress(addr), opts);
    }

    public static final void setFactory(com.oblong.jelly.pool.PoolFactory f) {
        factory = f;
    }

    private static volatile com.oblong.jelly.pool.PoolFactory factory;

    private static final PoolOptions DEF_OPTS = PoolOptions.MEDIUM;
}
