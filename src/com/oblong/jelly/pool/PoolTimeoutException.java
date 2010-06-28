package com.oblong.jelly.pool;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.PoolException;

@Immutable
public class PoolTimeoutException extends PoolException {

    public PoolTimeoutException(long sc) {
        super(Code.TIMEOUT, sc, "Server code was " + sc);
    }

    private static final long serialVersionUID = 3597941266546542983L;
}
