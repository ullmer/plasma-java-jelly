// Copyright (c) 2010 Oblong Industries
// Created: Mon Jun 28 16:08:10 2010

package com.oblong.jelly;

import net.jcip.annotations.Immutable;

/**
 * A PoolException denoting the failure to access a pool.
 *
 * <p> This problem will arise when asking for connection to a
 * non-existing pool.
 *
 * <p> It has kind {@code NO_SUCH_POOL}.
 *
 * @author jao
 */
@Immutable
public class NoSuchPoolException extends PoolException {

    public NoSuchPoolException(long sc) {
        super(Kind.NO_SUCH_POOL, sc, "Server code was " + sc);
    }

    private static final long serialVersionUID = 1648502586498969925L;
}
