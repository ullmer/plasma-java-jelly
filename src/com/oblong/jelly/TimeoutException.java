package com.oblong.jelly;

import net.jcip.annotations.Immutable;


@Immutable
public class TimeoutException extends PoolException {

    public TimeoutException(long sc) {
        super(Kind.TIMEOUT, sc, "Server code was " + sc);
    }

    private static final long serialVersionUID = 3597941266546542983L;
}
