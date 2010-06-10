// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.net.URI;
import java.net.URISyntaxException;

import net.jcip.annotations.Immutable;

import static com.oblong.jelly.PoolException.Code.BAD_ADDRESS;

/**
 *
 * Created: Wed Jun  9 16:20:42 2010
 *
 * @author jao
 */
@Immutable
public final class PoolAddress {

    public PoolAddress(String address) throws PoolException {
        final int sep = address.lastIndexOf('/');
        if (sep == -1 || sep > address.length() - 2)
            throw new PoolException(
                BAD_ADDRESS, "Missing pool name in address: " + address);
        name = checkName(address.substring(sep + 1));
        try {
            uri = new URI(address.substring(0, sep));
        } catch (URISyntaxException e) {
            throw new PoolException(BAD_ADDRESS, e);
        }
    }

    public PoolAddress(String host, int port, String name)
        throws PoolException {
        try {
            uri = new URI(TCP_SCM, "", host, port, "", "", "");
        } catch (URISyntaxException e) {
            throw new PoolException(BAD_ADDRESS, e);
        }
        this.name = checkName(name);
    }

    public String scheme() { return TCP_SCM; }
    public String host() { return uri.getHost(); }
    public int port() { return uri.getPort(); }
    public String name() { return name; }

    @Override public boolean equals(Object o) {
        if (!(o instanceof PoolAddress)) return false;
        return toString().equals(o.toString());
    }

    @Override public int hashCode() {
        return toString().hashCode();
    }

    @Override public String toString() {
        return uri + "/" + name;
    }

    private static String checkName(String name) throws PoolException {
        String error = null;
        if (name == null || name.isEmpty())
            error = "empty name";
        else if (name.startsWith("/") || name.startsWith("."))
            error = "invalid start character (" + name.charAt(0) + ")";
        else if (name.endsWith("/"))
            error = "trailing slash";
        else for (String s : FORBIDDEN)
                 if (name.contains(s)) {
                     error = "contains `" + s + "'";
                     break;
                 }
        if (error != null)
            throw new PoolException(BAD_ADDRESS,
                                    "Invalid name (" + name + "): " + error);
        return name;
    }

    private static final String[] FORBIDDEN = {"\\", ":", "..", "//"};
    private static final String TCP_SCM = "tcp";

    private final URI uri;
    private final String name;
}
