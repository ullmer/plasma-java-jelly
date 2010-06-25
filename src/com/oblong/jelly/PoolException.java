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

    public static enum Code {
        BAD_ADDRESS("Malformed pool address"),
        IO_ERROR("I/O error"),
        UNSUPPORTED_OP("Unsupported operation"),
        SERVER_ERROR("Server-side error"),
        PROTOCOL_ERROR("Protocol error"),
        TIMEOUT("A timeout expired waiting for a protein"),
        NO_SUCH_PROTEIN("Requested protein does not exist"),
        UNCLASSIFIED("Unclassified error"),
        USER("User-defined exception");

        private Code(String desc) {
            description = desc;
        }

        private final String description;
    }

    public final Code code() { return code; }

    public final int serverCode() { return serverCode; }

    @Override public String getMessage() {
        return code + "(" + code.description + "): " + info;
    }

    public PoolException(String msg) {
        this(Code.UNCLASSIFIED, msg);
    }

    protected PoolException(Code code, String info) {
        this(code, 0, info);
    }

    protected PoolException(Code code, Throwable cause) {
        this(code, 0, cause);
    }

    protected PoolException(int serverCode, String info) {
        this(Code.SERVER_ERROR, serverCode, info);
    }

    protected PoolException(int serverCode, Throwable cause) {
        this(Code.SERVER_ERROR, serverCode, cause);
    }

    protected PoolException(Code code, int sc, String info) {
        this.code = code;
        this.info = info;
        this.serverCode = sc;
    }

    protected PoolException(Code code, int sc, Throwable cause) {
        super(cause);
        this.code = code;
        info = cause.getMessage();
        this.serverCode = sc;
    }

    private final Code code;
    private final String info;
    private final int serverCode;

    private static final long serialVersionUID = -3964934204273865061L;
}
