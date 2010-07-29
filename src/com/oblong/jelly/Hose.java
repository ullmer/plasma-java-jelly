// Copyright (c) 2010 Oblong Industries
// Created: Thu Jun 10 22:55:16 2010

package com.oblong.jelly;

import java.util.concurrent.TimeUnit;

import net.jcip.annotations.NotThreadSafe;

/**
 * Interface for bidirectional communication with a pool.
 *
 * <p> Using a {@link PoolServer} as a factory, or directly via {@link
 * Pool#participate(String)} (or any of its overloads), you can obtain
 * an instance of this class, which provides you with a bidirectional
 * protein exchange channel with a concrete pool, as well as with some
 * other methods to manipulate the pool's state and obtain associated
 * metadata.
 *
 * <p> A Hose maintains some local state, relevant to the client using
 * it. Most notable, and index which denotes the position on the
 * pool's protein stream. This position (returned by {@link #index})
 * is used implicitly by protein retrieval methods that do not specify
 * a explicty position in the stream.
 *
 * <p> A second piece of local state is a free form name that client
 * code can assign, and mutate at any time. This name can be used in
 * the client side in any way you see fit, without affecting in any
 * way the communications with the server.
 *
 * <p> And, finally, there's of course the data structures involved in
 * the network connection.
 *
 * <p> The presence of mutable state, coupled with the fact that Hose
 * instances provide no built-in synchronization, makes them
 * non-thread-safe.
 *
 * <p> It is also important to keep in mind that the network resources
 * consumed by a Hose should be explicitly released (using {@link
 * #withdraw}), to ensure their proper and timely management.
 *
 * @author jao
 */
@NotThreadSafe
public interface Hose {

    /**
     * The network protocol used to communicate with the pool server
     * is versioned. This method gives you access to the version of
     * the network protocol used by this hose.
     */
    int version();

    /**
     * Returns a map with keyed pieces of information on the backing
     * pool. Keys are (Slaw) strings and include: size, size-used,
     * index-capacity, index-count, index-count, mmap-pool-version,
     * type, and terminal.
     */
    Slaw info();

    /**
     * This Hose's name. This is a local name that bears no relation
     * to the pool URI, except for the fact that it's initialized to
     * that value as a default. You can however modify the hose's name
     * without affecting in any way the pool it's connected to.
     */
    String name();

    /**
     * Modifies the local name of this Hose. Changing this name in no
     * way modifies the underlying connection.
     */
    void setName(String n);

    /**
     * The address of the pool this Hose is (or was) connected to.
     */
    PoolAddress poolAddress();

    /**
     * Whether the connection with the target pool is still alive.
     *
     * <p> This will return false if, for instance, you've called {@link
     * #withdraw} before.
     */
    boolean isConnected();

    /**
     * Closes the connection with the target pool, releasing all
     * associated resources. This method is idempotent.
     *
     * <p> After calling this method, any other operation involving
     * the remote end of the connection will fail with a {@link
     * ProtocolException}.
     *
     * <p> As stressed before, explicitly calling this method in
     * client code is of the essence for correct resource management
     * (you can think of Hose instances as, say, Socket instances in
     * that regard). A usage pattern ensuring proper resource
     * management could be:
     *
     * <pre>
     *    Hose hose = null;
     *    try {
     *       hose = Pool.participate("tcp://mithrandir/brandiwine");
     *       playWithThisHose(hose);
     *    } catch (PoolException e) {
     *       // ...
     *    } finally {
     *       if (hose != null) {
     *         hose.withdraw();
     *         hose = null;
     *       }
     *    }
     * </pre>
     *
     * and variants thereof.
     */
    void withdraw();

    /**
     * Every Hose maintains, locally, a pointer or index into the pool's
     * protein stream. This pointer works as an implicit parameter for
     * protein retrieval methods that don't specify an index among its
     * parameters.
     */
    long index();

    /**
     * Asks the server for the index of the last deposited protein so
     * far.
     *
     * <p> Calling this method does not modify this Hose's index.
     */
    long newestIndex() throws PoolException;

    /**
     * Asks the server for the index of the first available protein.
     *
     * <p> The index sequence increases monotonically, so {@code
     * oldestIndex()} will always be less than or equal to {@code
     * newestIndex()}.
     *
     * <p> Calling this method does not modify this Hose's index.
     */
    long oldestIndex() throws PoolException;

    /**
     * Sets the local value of the current index to the given value.
     *
     * <p> There's no communication with the server, and the value of
     * the current index is set unconditionally to the provided one.
     */
    void seekTo(long index);

    /**
     * Shifts the local value of the current index by the given offset.
     *
     * <p> There's no communication with the server, and the value of
     * the current index is modified unconditionally.
     */
    void seekBy(long offset);

    /**
     * Sets the local index to that of the very newest protein in the
     * pool. That is, calling {@link #current} immediately after this
     * method will return the latest deposited protein (barring
     * intervening deposits by this or other processes).
     */
    void toLast() throws PoolException;

    /**
     * Sets the local index pointing just past the end of the pool, so
     * that a call to {@link #next()} will wait, returning the next
     * protein deposited by another participant.
     */
    void runOut() throws PoolException;

    /**
     * Sets the local index to that of the first protein in the pool.
     * That is, calling {@link #current} immediately after this method
     * will return the first available protein in the pool.
     */
    void rewind() throws PoolException;

    Protein deposit(Protein p) throws PoolException;

    Protein current() throws PoolException;
    Protein next() throws PoolException;
    Protein next(Slaw descrip) throws PoolException;
    Protein awaitNext(long period, TimeUnit unit) throws PoolException;
    Protein awaitNext() throws PoolException;
    Protein previous() throws PoolException;
    Protein previous(Slaw descrip) throws PoolException;
    Protein nth(long index) throws PoolException;
}
