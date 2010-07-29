package com.oblong.jelly;

import net.jcip.annotations.Immutable;


@Immutable
public class NoSuchProteinException extends PoolException {

    public NoSuchProteinException(long sc) {
        super(Kind.NO_SUCH_PROTEIN, sc, "Server code was " + sc);
    }

    private static final long serialVersionUID = -7648502586498969925L;
}
