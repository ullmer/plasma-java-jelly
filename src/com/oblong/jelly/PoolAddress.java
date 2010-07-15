// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.jcip.annotations.Immutable;

/**
 *
 * Created: Thu Jul  1 15:56:36 2010
 *
 * @author jao
 */
@Immutable
public final class PoolAddress {

    public static PoolAddress fromURI(String uri)
        throws PoolException {
        return new PoolAddress(null, uri);
    }

    public PoolAddress(PoolServerAddress addr, String name)
        throws PoolException {
        Matcher matcher = ADDR_PATT.matcher(name);
        if (!PoolServerAddress.isRelative(name)
            && matcher.matches() && matcher.group(1) != null) {
            serverAddress = PoolServerAddress.fromURI(name);
            poolName = checkName(matcher.group(4));
        } else {
            serverAddress = addr == null ? new PoolServerAddress(null) : addr;
            poolName = checkName(name);
        }
        stringRep = serverAddress + "/" + poolName;
    }

    public PoolServerAddress serverAddress() { return serverAddress; }
    public String poolName() { return poolName; }

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

    private static final String checkName(String name) throws PoolException {
        if (name == null || name.isEmpty())
            throw new PoolServerAddress.BadAddress("Empty pool name");
        // we leave any further checking to the pool server
        return name;
    }

    private final PoolServerAddress serverAddress;
    private final String poolName;
    private final String stringRep;
}
