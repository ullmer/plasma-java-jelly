// Copyright (c) 2010 Oblong Industries
// Created: Sat Jun 26 00:44:33 2010

package com.oblong.jelly;

import net.jcip.annotations.Immutable;

/**
 * A PoolException denoting a server-reported error accessing a pool.
 *
 * <p> The originator of this error condition will almost always be a
 * pool server, having trouble accessing a pool locally. There's
 * generally little one can do on the client side, except using {@link
 * #serverCode()} as additional log info.
 *
 * <p> It has kind <code>CORRUPT_POOL</code>.
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
