// Copyright (c) 2010 Oblong Industries
// Created: Thu Jun  3 10:08:24 2010


package com.oblong.jelly;

import java.util.Set;

/**
 * A collection of functions to operate on pools. All these
 * functions are, strictly speaking, redundant, in the sense that you
 * can perform the same operations via {@link PoolServer} instances
 * obtained from the {@link PoolServers} factory. But the latter can
 * be a bit roundabout when you don't need to keep the PoolServer
 * around and all you want is a quick way to, say, create a pool or
 * obtain a {@link Hose} in one shot. The methods in this class allow
 * you to do precisely that, and to ignore the PoolServer API if you
 * wish to.
 * <p>
 * Half of these functions take a string URI to denote the pool they
 * act upon. Using them you can also be oblivious of PoolAddress and
 * PoolServerAddress if you're so inclined. On the other hand, using
 * instances of this classes instead of strings can help when the rest
 * of your application needs a more robust handling of pool addresses.
 * <p>
 * All the methods in this class are declared as throwing
 * PoolException, and, since talking with a remote server is involved
 * in all of them, you can expect any kind (meaning, any {@link
 * PoolException.Kind}) of error, with the exception of {@code
 * UNCLASSIFIED} and {@code USER}, and other obvious cases depending
 * on context (e.g., a call to participate won't throw a {@code
 * POOL_EXISTS} error).
 * <p>
 * This is a utility class: you're not expected to (and, actually,
 * cannot) create instances of this type.
 *
 * @author jao
 */
public final class Pool {

    /**
     * Asks a pool server to create the pool denoted by the given URI,
     * using the corresponding options. The latter can be null,
     * instructing the server to use default values for them. The URI
     * must include, of course, both the server and the pool names.
     *
     * @throws PoolException Possible error kinds include (but are not
     * limited to) {@link PoolException.Kind#POOL_EXISTS} (catchable
     * as a {@link PoolExistsException}), and {@link
     * PoolException.Kind#BAD_ADDRESS} ({@link BadAddressException}),
     * with the obvious meanings.
     */
    public static void create(String uri, PoolOptions opts)
        throws PoolException {
        create(PoolAddress.fromURI(uri), opts);
    }

    /**
     * Equivalent to {@code Pool.create(addr.toString(), opts)}.
     *
     * @see #create(String, PoolOptions)
     */
    public static void create(PoolAddress addr, PoolOptions opts)
        throws PoolException {
        PoolServers.get(addr.serverAddress()).create(addr.poolName(), opts);
    }

    /**
     * Asks a pool server to eliminate the pool denoted by the given
     * URI The URI must include, of course, both the server and the
     * pool names.
     *
     * @throws PoolException Possible error kinds include (but are not
     * limited to) {@link PoolException.Kind#NO_SUCH_POOL} (catchable
     * as a {@link NoSuchPoolException}), and {@link
     * PoolException.Kind#BAD_ADDRESS} ({@link BadAddressException}),
     * with the obvious meanings.
     */
    public static void dispose(String uri) throws PoolException {
        dispose(PoolAddress.fromURI(uri));
    }

    /**
     * Equivalent to {@code Pool.dispose(addr.toString())}.
     *
     * @see #dispose(String)
     */
    public static void dispose(PoolAddress addr) throws PoolException {
        PoolServers.get(addr.serverAddress()).dispose(addr.poolName());
    }

    /**
     * Establishes a connection with the pool denoted by the given URI.
     * If no exceptions are thrown, you can rely on the returned Hose
     * to be non-null and ready to use (although of course nothing
     * prevents the network connection to go awry in the meantime).
     *
     * @throws PoolException In particular, a PoolException of kind
     * {@link PoolException.Kind#NO_SUCH_POOL} (catchable as a {@link
     * NoSuchPoolException}) will be thrown if
     * the requested pool does not exist.
     */
    public static Hose participate(String uri) throws PoolException {
        return participate(PoolAddress.fromURI(uri));
    }

    /**
     * Equivalent to {@code Pool.participate(addr.toString())}.
     *
     * @see #participate(String)
     */
    public static Hose participate(PoolAddress addr) throws PoolException {
        return PoolServers.get(addr.serverAddress())
                          .participate(addr.poolName());
    }

    /**
     * Tries to establish a connection with the pool denoted by the
     * given URI. If the pool exists, {@code opts} is ignored;
     * otherwise, they'll be used to create a new pool with the given
     * options. Either way, you'll get back a Hose connected to the
     * pool, with the same guarantees as with
     * {@link #participate(String)}.
     *
     * @throws PoolException No errors of kind {@link
     * PoolException.Kind#POOL_EXISTS} or {@link
     * PoolException.Kind#NO_SUCH_POOL} will be thrown.
     */
    public static Hose participate(String uri, PoolOptions opts)
        throws PoolException {
        return participate(PoolAddress.fromURI(uri), opts);
    }

    /**
     * Equivalent to {@code Pool.participate(addr.toString(), opts)}.
     *
     * @see #participate(String, PoolOptions)
     */
    public static Hose participate(PoolAddress addr, PoolOptions opts)
        throws PoolException {
        return PoolServers.get(addr.serverAddress())
                          .participate(addr.poolName(), opts);
    }

    /**
     * Equivalent to {@code
     * Pool.pools(PoolServerAddress.fromURI(serverURI))}.
     *
     * @see #pools(PoolServerAddress)
     */
    public static Set<String> pools(String serverURI) throws PoolException {
        return pools(PoolServerAddress.fromURI(serverURI));
    }

    /**
     * Returns a list of the names of pools in the given server. Note
     * that the returned names are not full URIs: you need to add to
     * them the server address to operate on them using the rest of
     * methods in this class. For instance, the following code removes
     * all pools in a given server:
     *
     * <pre>
     *    PoolServerAddress sa =
     *        PoolServerAddress.fromURI("tcp://poolish-host.com:1020");
     *    for (String name : Pools.pools(sa))
     *      Pool.dispose(new PoolAddress(sa, name));
     * </pre>
     *
     */
    public static Set<String> pools(PoolServerAddress addr)
        throws PoolException {
        return PoolServers.get(addr).pools();
    }

    private Pool() {}
}
