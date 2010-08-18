// Copyright (c) 2010 Oblong Industries
package com.oblong.jelly;

import net.jcip.annotations.Immutable;

/**
 * A PoolException signaling the failure to retrieve a protein.
 *
 * <p> Errors of this kind (<code>NO_SUCH_PROTEIN)</code> will occur
 * when asking for proteins either because their number is exhausted
 * or because the remaining ones do not match the specified criterium.
 *
 * @author jao
 */
@Immutable
public class NoSuchProteinException extends PoolException {

    public NoSuchProteinException(long sc) {
        super(Kind.NO_SUCH_PROTEIN, sc, "Server code was " + sc);
    }

    private static final long serialVersionUID = -7648502586498969925L;
}
