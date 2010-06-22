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
        SERVER_ERROR("Server error"),
        UNCLASSIFIED("Unclassified error"),
        USER("User-defined exception");

        private Code(String desc) {
            description = desc;
        }

        private final String description;
    }

    public final Code code() { return code; }

    @Override public String getMessage() {
        return code + "(" + code.description + "): " + info;
    }

    public PoolException(String msg) {
        this(Code.UNCLASSIFIED, msg);
    }

    protected PoolException(Code code, String info) {
        this.code = code;
        this.info = info;
    }

    protected PoolException(Code code, Throwable cause) {
        super(cause);
        this.code = code;
        info = cause.getMessage();
    }

    private final Code code;
    private final String info;

    private static final long serialVersionUID = -3964934204273865061L;
}
