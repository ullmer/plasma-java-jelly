// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

/**
 * Errors reported by {@link HoseGang} operations.
 *
 * @author jao
 */
public final class GangException extends Exception {

    /**
     * A GangException encapsulates an error <code>e</code> accessing
     * a gang connection named <code>name</code> with address
     * <code>addr</code>.
     */
    public GangException(String name, PoolAddress addr, PoolException e) {
        super(e);
        origin = name;
        address = addr;
    }

    /**
     * The name of the connection in the gang causing this error.
     */
    public String name() { return origin; }

    /**
     * The name of the connection in the gang causing this error.
     */
    public PoolAddress address() { return address; }

    /**
     * The actual cause of this exception.
     */
    public PoolException cause() { return (PoolException)getCause(); }

    @Override public String getMessage() {
        return origin + ": " + super.getMessage();
    }

    private final String origin;
    private final PoolAddress address;

    private static final long serialVersionUID = 4735712786960038267L;

}
