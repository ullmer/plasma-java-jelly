// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.PoolException;

/**
 *
 * Created: Sat Jun 26 00:44:33 2010
 *
 * @author jao
 */
@Immutable
public class PoolCorruptException extends PoolException {

    public PoolCorruptException(long sc) {
        super(Code.CORRUPT_POOL, sc, "Server code was " + sc);
    }

    private static final long serialVersionUID = 3597921219546342983L;
}
