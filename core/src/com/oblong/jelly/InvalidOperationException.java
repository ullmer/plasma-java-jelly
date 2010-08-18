// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import net.jcip.annotations.Immutable;

/**
 * A PoolException denoting an operation not accepted by a server.
 *
 * <p> This error will arise when you try to perform an operation that
 * the pool server does not support.
 *
 * <p> It has kind <code>UNSUPPORTED_OP</code>.
 *
 * @author jao
 */
@Immutable
public class InvalidOperationException extends PoolException {

    public InvalidOperationException(String info) {
        super(Kind.UNSUPPORTED_OP, info);
    }

    public InvalidOperationException(long code) {
        super(Kind.UNSUPPORTED_OP, code, "Server rejected op");
    }

    private static final long serialVersionUID = -8852204604279246564L;
}
