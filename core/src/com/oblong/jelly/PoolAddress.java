// Copyright (c) 2010 Oblong Industries
// Created: Thu Jul  1 15:56:36 2010

package com.oblong.jelly;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.jcip.annotations.Immutable;

/**
 * A class representing and parsing pool URIs.
 *
 * <p> The location of a pool consists of two parts: the address of
 * its server and the pool name. One can specify the former as a
 * {@link PoolServerAddress} object, accompanying it by the pool name
 * as a string to create the full pool address.
 *
 * <p> As an alternative, one can also use a single string with the
 * full pool URI and rely on PoolAddress to handle its parsing (or on
 * any of the methods in {@link Pool} that take a single string as
 * their pool locator).
 *
 * <p> As an example, this code snippet:
 * <pre>
 *   PoolServerAddress = new PoolServerAddress("tcp", "hostname", 2323);
 *   PoolAddress a = new PoolAddress(sa, "a-pool");
 * </pre>
 *
 * creates a PoolAddress that is the same as the one created as:
 * <pre>
 *   PoolAddress a = PoolAddress.fromURI("tcp://hostname:2323/a-pool");
 * </pre>
 *
 * @author jao
 */
@Immutable
public final class PoolAddress {

    /**
     * Constructs a PoolAddress object from its string representation.
     * {@code uri} must be a full pool URI, including a scheme (e.g.
     * "tcp://"), optional hostname and port followed by a slash, and
     * a pool name (whose validity will be checked by the pool server
     * upon connection).
     */
    public static PoolAddress fromURI(String uri)
        throws BadAddressException {
        return new PoolAddress(null, uri);
    }

    /**
     * Combines a server address and a pool name to create a PoolAddress.
     *
     * <p> {@code name} will usually be a path relative to the server
     * address (e.g. {@code "pname"} or {@code "rel/path/name of a
     * pool"}), and this constructor will combine the two to yield the
     * final pool address.
     *
     * <p> But, if {@code name} happens to be a fully qualified pool
     * URI (e.g. {@code "tcp://there/a-pool"}, {@code addr} will be
     * ignored, and the new PoolAddress will be equal to one created
     * using {@code fromURI(name)}.
     *
     * <p> If {@code addr} is null, a default server address (as
     * constructed by {@code new PoolServerAddress(null)}, i.e.,
     * {@code "tcp://localhost:65456"} will be used.
     *
     * @throws BadAddressException if {@code name} is not a valid pool
     * name or URI. Note, however, that the lack of an error when
     * constructing an address on the client side doesn't guarantee
     * that the server will accept the pool name as a valid one.
     */
    public PoolAddress(PoolServerAddress addr, String name)
        throws BadAddressException {
        Matcher matcher = ADDR_PATT.matcher(name);
        if (!PoolServerAddress.isRelative(name) && matcher.matches()) {
            serverAddress = PoolServerAddress.fromURI(name);
            poolName = checkName(matcher.group(4));
        } else {
            serverAddress = addr == null ? new PoolServerAddress(null) : addr;
            poolName = checkName(name);
        }
        stringRep = serverAddress + "/" + poolName;
    }

    /** Accessor to the address of the server this pools belongs to. */
    public PoolServerAddress serverAddress() { return serverAddress; }
    /** Accesor to the part of the address relative to the server address. */
    public String poolName() { return poolName; }

    /**
     * Returns a valid URI representing this address.
     * <p> That means that it is always the case that:
     * <pre>
     *   address == PoolAddress.fromURI(address.toString());
     * </pre>
     */
    @Override public String toString() {
        return stringRep;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof PoolAddress)) return false;
        final PoolAddress oa = (PoolAddress)o;
        return stringRep.equals(oa.stringRep);
    }

    @Override public int hashCode() {
        return stringRep.hashCode();
    }

    private static final String ADDR_REGEX =
        PoolServerAddress.ADDR_REGEX + "(?:/(.+))";

    private static final Pattern ADDR_PATT = Pattern.compile(ADDR_REGEX);

    private static final String checkName(String name)
        throws BadAddressException {
        if (name == null || name.isEmpty())
            throw new BadAddressException("Empty pool name");
        // we leave any further checking to the pool server
        return name;
    }

    private final PoolServerAddress serverAddress;
    private final String poolName;
    private final String stringRep;
}
