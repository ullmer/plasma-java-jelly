// Copyright (c) 2010 Oblong Industries
// Created: Mon Jun 14 13:22:13 2010

package com.oblong.jelly;

import java.util.Set;

/**
 * Interface defining pool operations against a specific server.
 *
 * <p> This interface abstracts over pool servers, which are entities
 * giving you access to a set of pools, allowing also disposal and the
 * creation of new ones.
 *
 * <p> Usually, a pool server is actually an external process to which
 * the client connects using a network protocol, but nothing in the
 * jelly interfaces precludes other kinds of servers. From the client
 * side, one can thus treat servers as modifiable collections of
 * pools. That said, the most common case is by far that of a server
 * accessed using the TCP scheme.
 *
 * <p> The concrete nature of the pool server is described by the
 * scheme of its {@link PoolServerAddress}, which also serves as the
 * universal resource identifier for the server. To obtain an instance
 * of this class associated to a given address, use the factory method
 * {@link Pool#getServer}.
 *
 * <p> All the operations defined in this interface can also be
 * performed by means of the static methods in {@link Pool}, that will
 * spare you the trouble (in case that's a trouble) of having to deal
 * with PoolServer instances at all.
 *
 * @see Pool
 *
 * @author jao
 */
public interface PoolServer {

    /** The address and URI of this server. */
    PoolServerAddress address();

    /**
     * Name of the server, for display purposes. It's guaranteed to be
     * unique within a given scheme, but servers with different names
     * can have the same address.
     *
     * <p> For remote servers discovered via zeroconf, this name
     * corresponds to the service name.
     */
    String name();

    /**
     * Server subtype.
     *
     * The server "type" is given by the scheme of its pool address
     * (e.g. "tcp"). Some servers may provide additional subtypes,
     * specifying the purpose of the pools they serve, or any other
     * application-specific information.
     *
     * <p> For remote servers discovered via zeroconf, these subtypes
     * correspond to the server's service subtypes.
     */
    String subtype();

    /**
     * A name taking into account the server's subtype and guaranteed
     * to be unique.
     */
    String qualifiedName();

    /**
     * Creates a new pool, with the given name and options, in this
     * server.
     *
     * <p> Note that <code>name</code> should refer to the pool name
     * <i>sans</i> its server part, i.e., be a relative, rather than
     * absolute URI.
     *
     * @throws PoolException if the creation failed. Almost any kind
     * of PoolException can be expected here. Besides network protocol
     * errors, {@link PoolException.Kind#BAD_ADDRESS} (thrown as a
     * {@link BadAddressException}) and {@link
     * PoolException.Kind#POOL_EXISTS} ({@link NoSuchPoolException})
     * will perhaps be the most common error types.
     *
     * @see Pool#create(String, PoolOptions)
     */
    void create(String name, PoolOptions opts) throws PoolException;

    /**
     * Removes the given pool from this server.
     *
     * <p> Note that <code>name</code> should refer to the pool name
     * <i>sans</i> its server part, i.e., be a relative, rather than
     * absolute URI.
     *
     * @throws PoolException Almost any kind of PoolException can be
     * expected here. Besides network errors, {@link
     * PoolException.Kind#BAD_ADDRESS} (thrown as a {@link
     * BadAddressException}) and {@link
     * PoolException.Kind#NO_SUCH_POOL} ({@link NoSuchPoolException})
     * will perhaps be the most common error types.
     *
     * @see Pool#dispose(String)
     */
    void dispose(String name) throws PoolException;

    /**
     * Establishes a connection with the pool denoted by the given name.
     *
     * <p> Note that <code>name</code> should refer to the pool name
     * <i>sans</i> its server part, i.e., be a relative, rather than
     * absolute URI.
     *
     * If no exceptions are thrown, you can rely on the returned Hose
     * to be non-null and ready to use (although of course nothing
     * prevents the network connection to go awry in the meantime).
     *
     * @throws PoolException In particular, a PoolException of kind
     * {@link PoolException.Kind#NO_SUCH_POOL} (catchable as a {@link
     * NoSuchPoolException}) will be thrown if
     * the requested pool does not exist.
     *
     * @see Pool#participate(String)
     */
    Hose participate(String name) throws PoolException;

    /**
     * Tries to establish a connection with the pool denoted by the
     * given name. If the pool exists, <code>opts</code> is ignored;
     * otherwise, they'll be used to create a new pool with the given
     * options. Either way, you'll get back a Hose connected to the
     * pool, with the same guarantees as with {@link
     * #participate(String)}.
     *
     * <p> Note that <code>name</code> should refer to the pool name
     * <i>sans</i> its server part, i.e., be a relative, rather than
     * absolute URI.
     *
     * @throws PoolException No errors of kind {@link
     * PoolException.Kind#POOL_EXISTS} or {@link
     * PoolException.Kind#NO_SUCH_POOL} will be thrown.
     *
     * @see Pool#participate(String, PoolOptions)
     */
    Hose participate(String name, PoolOptions opts) throws PoolException;

    /**
     * Returns a list of the names of pools in this server. See {@link
     * Pool#pools} for details.
     */
    Set<String> pools() throws PoolException;
}
