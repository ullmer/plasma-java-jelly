// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import net.jcip.annotations.Immutable;

/**
 *
 * Created: Wed Jun  9 16:27:19 2010
 *
 * @author jao
 */
@Immutable
public class PoolException extends Exception {

    public static enum Kind {
        BAD_ADDRESS("Malformed pool address or name"),
        IO_ERROR("I/O error"),
        UNSUPPORTED_OP("Unsupported operation"),
        SERVER_ERROR("Server-side error"),
        PROTOCOL_ERROR("Protocol error"),
        TIMEOUT("A timeout expired waiting for a protein"),
        NO_SUCH_PROTEIN("Requested protein does not exist"),
        NO_SUCH_POOL("Requested pool does not exist"),
        POOL_EXISTS("Pool could not be created: it already exists"),
        CORRUPT_POOL("Pool couldn't be accessed on server"),
        UNCLASSIFIED("Unclassified error"),
        USER("User-defined exception");

        private Kind(String desc) {
            description = desc;
        }

        private final String description;
    }

    public final Kind kind() { return kind; }

    public final long serverCode() { return serverCode; }

    @Override public String getMessage() {
        return kind + "(" + kind.description + "): " + info;
    }

    public PoolException(String msg) {
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
