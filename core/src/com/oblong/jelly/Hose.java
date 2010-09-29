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
 * a explicitly position in the stream.
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
 * instances use no built-in synchronization mechanisms, makes them
 * non-thread-safe.
 *
 * <p> It is also important to keep in mind that the network resources
 * consumed by a Hose should be explicitly released (using {@link
 * #withdraw}), to ensure their proper and timely management.
 *
 * <p> Regarding error reporting, this interfaces follows the usual
 * policy with pool errors in jelly: methods have a throws
 * PoolException clause and will throw all the appropriate subtypes,
 * that can be caught specifically if you so desire (see discussion
 * {@link PoolException here}).
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
     * pool. Keys are (Slaw) strings and include size, size-used,
     * index-capacity, index-count, mmap-pool-version, type, and
     * terminal. Additional information may be available, depending on
     * the target server's version.
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
     * InOutException}.
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
     *       hose = Pool.participate("tcp://imladris/brandywine");
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
     * <p> The index sequence increases monotonically, so
     * <code>oldestIndex()</code> will always be less than or equal to
     * <code>newestIndex()</code>.
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

    /**
     * Tries to deposit the give protein at the end of the pool.
     *
     * <p> If the operation succeeds, the returned protein will be the
     * same as <code>p</code> <i>cum</i> Slaw, but its timestamp and
     * index will correspond to those reported by the server. Remember
     * that Protein instances are immutable, so that <code>p</code>
     * won't (can't) be modified.
     *
     * <p> Since any error during the deposit will be reported by
     * throwing a PoolException, you're guaranteed that the returned
     * Protein will always be non-null. If you're not interested in
     * the index or timestamp of the deposited protein, you can just
     * ignore that return value.
     *
     * <pre>
     *    try {
     *      Protein dep = hose.deposit(p);
     *      System.out.println("Deposited with index " + dep.index()
     *                          + " and stamp " + dep.timestamp());
     *      letsMoveOn();
     *   } catch (PoolException e) {
     *      System.out.println("Ooops: " + e.getMessage());
     *   }
     * </pre>
     *
     * <p> Note that the index and timestamp of <code>p</code> are
     * ignored, as is the local index of the Hose.
     */
    Protein deposit(Protein p) throws PoolException;

    /**
     * Fetches the protein located at the given index value.
     *
     * <p> Calling this method does not modify the local index and, if
     * the invocation succeeds, the return value is guaranteed to be
     * non-null.
     *
     * @throws PoolException If no protein with the requested
     * <code>index</code> exists, the PoolException will be of type
     * {@link NoSuchProteinException}. Other kinds, possibly related
     * to communication problems or conditions encountered by the pool
     * server, may also arise.
     *
     */
    Protein nth(long index) throws PoolException;

    /**
     * Retrieves the Protein located at the current value of the index.
     *
     * <p> Calling this method does not modify the local index and, if
     * the invocation succeeds, the return value is guaranteed to be
     * non-null. Successive calls to <code>current</code> will return
     * the same protein over and over unless and until the pool fills
     * up and wraps around, but note that each one will imply a
     * connection to the server and could still fail.
     *
     * @throws PoolException Since the Hose's index can be set to
     * arbitrary values on the client side (and, moreover, proteins in
     * the pool can be overwritten when no space is left in it), a
     * possible outcome of this method is a {@link
     * NoSuchProteinException}. All other kinds of PoolException
     * describing problems when talking with a pool server can also
     * occur.
     */
    Protein current() throws PoolException;

    /**
     * Retrieves the next Protein in the pool with index equal or greater
     * than this Hose's local index, and advances the latter.
     *
     * <p> Thus, successive calls to <code>next</code> will
     * sequentially traverse the sequence of proteins in the pool.
     * Since the absence of a next protein (when reaching the end of
     * the pool) is signaled by means of a <code>PoolException</code>
     * (of type <code>NoSuchProteinException</code>), the return value
     * of this method is always non-null. Moreover, after each call,
     * the value returned by <code>index()</code> will be equal to the
     * index of the returned protein plus one.
     *
     * <p> If no protein is available at call time, this method will
     * fail immediately. Use {@link #awaitNext()} or {@link
     * #awaitNext(long, TimeUnit)} if you're willing to wait.
     */
    Protein next() throws PoolException;

    /**
     * Looks for the next protein in the pool whose descrips match
     * the given one.
     *
     * <p> The search will start at the current index, and proceed
     * forward until a protein matching (see below) the given descrip
     * is found, or no more proteins are available, in which case a
     * <code>PoolException</code> with kind {@link
     * PoolException.Kind#NO_SUCH_PROTEIN} (and, therefore, type
     * {@link NoSuchProteinException}) will be thrown.
     *
     * <p> If <code>descrip</code> is not a Slaw list, any protein
     * containing it among its descrips will match. If
     * <code>descrip</code> is a list, all of its elements must be
     * present in the protein's descrip list, possibly with
     * intervening gaps. As you can see, matching can only happen for
     * proteins whose descrips are a Slaw list.
     */
    Protein next(Slaw descrip) throws PoolException;

    /**
     * Like {@link #next()}, but blocking (with a timeout) if no
     * proteins are available.
     *
     * <p> Whe a next protein is already available, this method
     * behaves just like next. Otherwise, it will wait for a new
     * protein to appear in the pool (with an index equal or greater
     * than the current one) for the specified amount of time.
     *
     * <p> When <code>period</code> is 0, this method behaves like
     * {@link #next()} (i.e., it doesn't wait), while if
     * <code>period</code> is negative, it behaves like {@link
     * #awaitNext()} (i.e., it waits forever).
     *
     * <p> Note that, if you have moved forward the local index past
     * the end of the pool (i.e., to a value greater than
     * <code>newestIndex() + 1</code>, it may be the case that more
     * than one protein must be added to the pool by other
     * participants before this call returns.
     *
     * @throws PoolException In particular, failure to retrieve the
     * next protein is reported as a timeout ({@link
     * PoolException.Kind#TIMEOUT} or {@link TimeoutException}) rather
     * than as <code>NoSuchProteinException</code>, as was the case
     * for {@link #next}.
     */
    Protein awaitNext(long period, TimeUnit unit) throws PoolException;

    /**
     * Like {@link #next()}, but blocking indefinitely if no proteins
     * are available.
     *
     * <p> This method will return only when a protein is available,
     * or an error accessing the server occurs. In the latter case, no
     * attempts to regain communication with the server is made, and
     * the error is reporting by throwing a PoolException of the
     * appropriate type.
     */
    Protein awaitNext() throws PoolException;

    /**
     * Retrieves the next Protein in the pool with index strictly less
     * than this Hose's local index, and decreases the latter.
     *
     * <p> If there are no proteins with an index lesser than the
     * current local index, a {@link NoSuchProteinException} is
     * thrown, and the local index is not modified.
     *
     * <p> Since indexes in a pool are assigned in a strictly
     * increasing sequence, there's no possibility of waiting for a
     * previous protein.
     *
     * <p> Successive calls to <code>previous</code> will allow you to
     * move sequentially through the protein stream, back to its
     * beginning. After each successful call to <code>previous</code>,
     * the local hose index will be equal to that of the returned
     * protein.
     */
    Protein previous() throws PoolException;

    /**
     * Looks for the next protein in the pool whose descrips match
     * the given one.
     *
     * <p> This method works like {@link #next(Slaw)}, but searching
     * backwards instead of forwards. The local index is modified as
     * in {@link #previous()}.
     */
    Protein previous(Slaw descrip) throws PoolException;
}
