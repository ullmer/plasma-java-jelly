// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import net.jcip.annotations.Immutable;


/**
 *
 * Created: Sat Jun 26 00:44:33 2010
 *
 * @author jao
 */
@Immutable
public class CorruptPoolException extends PoolException {

    public CorruptPoolException(long sc) {
        super(Kind.CORRUPT_POOL, sc, "Server code was " + sc);
    }

    private static final long serialVersionUID = 3597921219546342983L;
}
