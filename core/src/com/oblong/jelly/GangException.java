// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

/**
 *
 * @author jao
 */
public final class GangException extends Exception {

    public GangException(String name, PoolAddress addr, PoolException e) {
        super(e);
        origin = name;
        address = addr;
    }

    public String origin() { return origin; }
    public PoolAddress originAddress() { return address; }
    public PoolException cause() { return (PoolException)getCause(); }

    @Override public String getMessage() {
        return origin + ": " + super.getMessage();
    }

    private final String origin;
    private final PoolAddress address;

    private static final long serialVersionUID = 4735712786960038267L;
}
