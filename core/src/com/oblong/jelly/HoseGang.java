// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A multiplexed collection of input Hoses. Gangs maintain a set of
 * open connections to one or more pools, giving access to proteins
 * coming from any of them.
 *
 * <p>Each connection in the gang gets a unique name, which can be
 * used afterwards to remove the connection from the gang.
 *
 * <p>Unlike individual Hoses, HoseGang instances are thread-safe.
 *
 * @author jao
 */
public abstract class HoseGang {

    /**
     * Factory method creating a new, empty gang.
     */
    public static HoseGang newGang() {
        return new com.oblong.jelly.pool.gang.Gang();
    }

    /**
     * Number of connections in this gang. Always a non-negative
     * value.
     */
    public abstract int count();

    /**
     * Names for the connections added to this gang. It is always the
     * case that <code>names().count()</code> equals
     * <code>count()</code>; i.e., connection names are unique.
     */
    public abstract Set<String> names();

    /**
     * Adds a new connection to this gang, to the given pool. The name
     * of this connection will be <code>addr.toString()</code>, which
     * is passed as the first argument of a call to {@link
     * #add(String, PoolAddress)}.
     */
    public final boolean add(PoolAddress addr) throws PoolException {
        return add(addr.toString(), addr);
    }

    /**
     * Adds a new connection to this gang. Equivalent to calling
     * {@link #add(PoolAddress)} (q.v.) with
     * <code>PoolAddress.fromURI(uri)</code> as argument.
     */
    public final boolean add(String uri) throws PoolException {
        return add(uri, uri);
    }

    /**
     * Adds a new connection to this gang, stealing it from the given
     * hose. This method works like {@link #add(String, Hose)} using
     * <code>hose.name()</code> as the connection name.
     */
    public final boolean add(Hose hose) throws PoolException {
        return add(hose.name(), hose);
    }

    /**
     * Adds a new connection to this gang, to the given pool. The name
     * of this connection will be <code>name</code>. If a
     * connection with that name already exists in the gang, it will
     * be replaced with a freshly opened one, and <code>false</code>
     * returned. If this connection is a new one, this method returns
     * <code>true</code>.
     *
     * @throws PoolException if a connection to the given address
     * cannot be established.
     */
    public final boolean add(String name, PoolAddress addr)
        throws PoolException {
        return doAdd(name, Pool.participate(addr));
    }

    /**
     * Adds a new connection to this gang. Equivalent to calling
     * {@link #add(String, PoolAddress)} (q.v.) with
     * <code>PoolAddress.fromURI(uri)</code> as second argument.
     */
    public final boolean add(String name, String uri) throws PoolException {
        return doAdd(name, Pool.participate(uri));
    }

    /**
     * Adds a new connection to this gang, stealing it from
     * <code>hose</code>. The connection inherits the local index of
     * the given hose.
     *
     * <p>This method is useful when you want to set up the initial
     * index for reading, and also when you want to add to the gang
     * a {@link FilteredHose}.
     *
     * <p>After calling this method, your <code>hose</code> will be
     * disconnected.
     *
     * <p>As with the other addition methods, the return value
     * indicates whether this connection's name is new.
     */
    public final boolean add(String name, Hose hose) throws PoolException {
        return doAdd(name, hose.dupAndClose());
    }

    /**
     * Removes and closes the connection with the given name. Returns
     * <code>true</code> if <code>name</code> actually named a
     * connection in the band.
     */
    public abstract boolean remove(String name);

    /**
     * Closes all connections in this gang. It is important to call
     * this method once you're done with your gang, to avoid consuming
     * resuorces such as network collections unnecessarily.
     */
    public abstract void disband();

    /**
     * Waits for the next protein, from any connection.
     *
     * @throws InterruptedException if {@link #wakeUp} is invoked on
     * this hang from another thread.
     *
     * @throws GangException if an error occurs while trying to fetch
     * protein using any of the underlying hoses. Note that faulty
     * connections are not removed from the gang unless explicitly
     * requested (using {@link #remove}, and may keep generating
     * GangExceptions. A common idiom to avoid that is
     * <pre>
     *   try {
     *       gang.awaitNext();
     *   catch (GangException e) {
     *       gang.remove(e.name());
     *   } ...
     * </pre>
     *
     */
    public abstract Protein awaitNext()
        throws GangException, InterruptedException;

    /**
     * Waits for the next protein, from any connection, for the
     * specified amount of time. If <code>period</code> is negative,
     * the call is equivalent to {@link #awaitNext()} (i.e., we'll
     * wait for ever).
     *
     * @throws TimeoutException if the given period expires without
     * any new protein arriving.
     *
     * @throws InterruptedException if {@link #wakeUp} is invoked on
     * this hang from another thread.
     *
     * @throws GangException if an error occurs while trying to fetch
     * protein using any of the underlying hoses. Note that faulty
     * connections are not removed from the gang unless explicitly
     * requested (using {@link #remove}, and may keep generating
     * GangExceptions. A common idiom to avoid recurrent errors is
     * <pre>
     *   try {
     *       gang.awaitNext(period, unit);
     *   catch (GangException e) {
     *       gang.remove(e.name());
     *   } ...
     * </pre>
     *
     */
    public abstract Protein awaitNext(long period, TimeUnit unit)
        throws GangException, TimeoutException, InterruptedException;

    /**
     * Causes an InterruptedException to one active (or future) call
     * to {@link #awaitNext()} or {@link #awaitNext(long, TimeUnit)}.
     * If there are more than one threads blocked on any of those two
     * calls, only one of them will be awaken. If there is none, the
     * next call to any of the await methods will cause an
     * <code>InterruptedException</code>.
     */
    public abstract boolean wakeUp();

    protected abstract boolean doAdd(String name, Hose hose)
        throws PoolException;
}
