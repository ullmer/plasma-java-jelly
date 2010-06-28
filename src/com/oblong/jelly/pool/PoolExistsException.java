// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.PoolException;

/**
 *
 * Created: Mon Jun 28 16:10:28 2010
 *
 * @author jao
 */
@Immutable
public class PoolExistsException extends PoolException {

    public PoolExistsException(long sc) {
        super(Code.POOL_EXISTS, sc, "Server code was " + sc);
    }

    private static final long serialVersionUID = -949502586422969925L;
}
