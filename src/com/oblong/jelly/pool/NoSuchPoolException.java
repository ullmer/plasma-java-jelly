// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.PoolException;

/**
 *
 * Created: Mon Jun 28 16:08:10 2010
 *
 * @author jao
 */
@Immutable
public class NoSuchPoolException extends PoolException {

    public NoSuchPoolException(long sc) {
        super(Code.NO_SUCH_POOL, sc, "Server code was " + sc);
    }

    private static final long serialVersionUID = 1648502586498969925L;
}
