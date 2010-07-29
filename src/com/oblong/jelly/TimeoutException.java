// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import net.jcip.annotations.Immutable;

/**
 * A PoolException denoting the expiration of a timeout.
 *
 * <p> It has kind {@code TIMEOUT}.
 *
 * <p> This error will be thrown when waiting for a protein does not
 * succeed before the specified time.
 *
 * @author jao
 */
@Immutable
public class TimeoutException extends PoolException {

    public TimeoutException(long sc) {
        super(Kind.TIMEOUT, sc, "Server code was " + sc);
    }

    private static final long serialVersionUID = 3597941266546542983L;
}
