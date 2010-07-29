// Copyright (c) 2010 Oblong Industries
// Created: Wed Jun  9 16:27:19 2010

package com.oblong.jelly;

import net.jcip.annotations.Immutable;

/**
 * Base class for exceptions arising during operations on Pools.
 *
 * There are several ways operations on pools can fail. As is
 * customary in Java APIs, jelly uses exceptions to signal those
 * errors. It offers, however, to possibilities when it comes to
 * handling them, viz. using the root PoolException type and
 * distinguish between different error kinds via its
 * PoolException.Kind field, or, alternatively, using the separate
 * exception classes defined in com.oblong.jelly.pool and
 * discriminating between different error types by means of separate
 * catch clauses.
 *
 * Thus, you'll observe that all pool operations throwing exceptions
 * use PoolException in their throws clause (so that a single catch
 * clause will do), but also that all exception types in
 * com.oblong.jelly.pool derive from PoolException, so that you can
 * also write catch clauses separately for them.
 *
 * For example, this snippet uses the first error handling strategy:
 *
 * <pre>
 *   try {
 *     Hose h = Pool.participate("a_pool");
 *     // ...
 *   } catch (PoolException e) {
 *     if (PoolException.NO_SUCH_POOL == e.kind()) f();
 *     else if (PoolException.CORRUPT_POOL == e.kind()) g();
 *     else h();
 *   }
 * </pre>
 *
 * which could be re-written, if you want to separate different error kinds
 * by exception clause as:
 *
 * <pre>
 *   try {
 *     Hose h = Pool.participate("a_pool");
 *     // ...
 *   } catch (NoSuchPoolException e) {
 *     f();
 *   } catch (CorruptPoolException e) {
 *     g();
 *   } catch (PoolException e) {
 *     h();
 *   }
 * </pre>
 *
 * Sometimes a derived exception type provides an interface for
 * accessing some piece of information you're interested in, and
 * you'll probably favour the second style in those cases.
 *
 * @author jao
 */
@Immutable
public class PoolException extends Exception {

    /**
     * Each PoolException has a kind, which describes the type of the
     * error. The kind also relates to the instance's actual Java
     * type, to be found in the {@link com.oblong.jelly.pool} package.
     */
    public static enum Kind {
        /**
         * Malformed pool or server address.
         * @see com.oblong.jelly.pool.BadAddressException
         */
        BAD_ADDRESS("Malformed pool address or name"),
        /**
         * Input/output error when exchanging info with server.
         * @see com.oblong.jelly.pool.InOutException
         */
        IO_ERROR("I/O error"),
        /**
         * The server does not support the requested operation.
         * @see com.oblong.jelly.pool.InvalidOperationException
         */
        UNSUPPORTED_OP("Unsupported operation"),
        /**
         * The server reported a generic error.
         * @see com.oblong.jelly.pool.ServerException
         */
        SERVER_ERROR("Server-side error"),
        /**
         * Server and client didn't understand each other.
         * @see com.oblong.jelly.pool.ProtocolException
         */
        PROTOCOL_ERROR("Protocol error"),
        /**
         * Timeout while waiting for server response.
         * @see com.oblong.jelly.pool.TimeoutException
         */
        TIMEOUT("A timeout expired waiting for a protein"),
        /**
         * The requested protein does not exist in pool.
         * @see com.oblong.jelly.pool.NoSuchProteinException
         */
        NO_SUCH_PROTEIN("Requested protein does not exist"),
        /**
         * The requested pool does not exist.
         * @see com.oblong.jelly.pool.NoSuchPoolException
         */
        NO_SUCH_POOL("Requested pool does not exist"),
        /**
         * The requested pool already exists.
         * @see com.oblong.jelly.pool.PoolExistsException
         */
        POOL_EXISTS("Pool could not be created: it already exists"),
        /**
         * A pool was found in a bad state in a server.
         * @see com.oblong.jelly.pool.CorruptPoolException
         */
        CORRUPT_POOL("Pool couldn't be accessed on server"),
        /** None of the above, but still an error. */
        UNCLASSIFIED("Unclassified error"),
        /** If you need your own error type, use this kind. */
        USER("User-defined exception");

        public String description() { return description; }

        private Kind(String desc) { description = desc; }

        private final String description;
    }

    /** Yes, you guessed it. */
    public final Kind kind() { return kind; }

    /**
     * It is often the case that PoolExceptions originate in errors
     * reported by a pool server. This method returns the integer code
     * that the server used to label the error.
     */
    public final long serverCode() { return serverCode; }

    @Override public String getMessage() {
        return kind + "(" + kind.description + "): " + info;
    }

    protected PoolException(String msg) {
        this(Kind.UNCLASSIFIED, msg);
    }

    protected PoolException(Kind kind, String info) {
        this(kind, 0, info);
    }

    protected PoolException(Kind kind, Throwable cause) {
        this(kind, 0, cause);
    }

    protected PoolException(long serverCode, String info) {
        this(Kind.SERVER_ERROR, serverCode, info);
    }

    protected PoolException(long serverCode, Throwable cause) {
        this(Kind.SERVER_ERROR, serverCode, cause);
    }

    protected PoolException(Kind kind, long sc, String info) {
        this.kind = kind;
        this.info = info;
        this.serverCode = sc;
    }

    protected PoolException(Kind kind, long sc, Throwable cause) {
        super(cause);
        this.kind = kind;
        info = cause.getMessage();
        this.serverCode = sc;
    }

    private final Kind kind;
    private final String info;
    private final long serverCode;

    private static final long serialVersionUID = -3964934204273865061L;
}
