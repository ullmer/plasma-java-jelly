// Copyright (c) 2010 Oblong Industries
// Created: Mon Jun 28 16:10:28 2010

package com.oblong.jelly;

import net.jcip.annotations.Immutable;

/**
 * A PoolException denoting the failure to create a pool that already
 * exists.
 *
 * <p> It has kind <code>POOL_EXISTS</code>.
 *
 * @author jao
 */
@Immutable
public class PoolExistsException extends PoolException {

    public PoolExistsException(long sc) {
        super(Kind.POOL_EXISTS, sc, "Server code was " + sc);
    }

    private static final long serialVersionUID = -949502586422969925L;
}
