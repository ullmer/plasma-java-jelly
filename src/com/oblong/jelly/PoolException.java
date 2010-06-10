// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import net.jcip.annotations.Immutable;

/**
 *
 * Created: Wed Jun  9 16:27:19 2010
 *
 * @author jao
 */
@Immutable public class PoolException extends Exception {

    public static enum Code {
        BAD_ADDRESS("Malformed pool address", -1);

        public final String description;
        public final int code;

        Code(String desc, int c) {
            description = desc;
            code = c;
        }
    }

    public PoolException(Code code, String info) {
        this.code = code;
        this.info = info;
    }

    public PoolException(Code code, Throwable cause) {
        this.code = code;
        info = cause.toString();
    }

    public Code code() { return code; }

    @Override public String toString() {
        return code + "(" + code.description + "): " + info;
    }

    private final Code code;
    private final String info;
}
