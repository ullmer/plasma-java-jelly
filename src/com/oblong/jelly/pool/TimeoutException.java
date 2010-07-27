package com.oblong.jelly.pool;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.PoolException;

@Immutable
public class TimeoutException extends PoolException {

    public TimeoutException(long sc) {
        super(Kind.TIMEOUT, sc, "Server code was " + sc);
    }

    private static final long serialVersionUID = 3597941266546542983L;
}
