// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.net.URI;
import java.net.URISyntaxException;

import net.jcip.annotations.Immutable;

/**
 *
 * Created: Wed Jun  9 16:20:42 2010
 *
 * @author jao
 */
@Immutable
public final class PoolServerAddress {

    public static class BadAddress extends PoolException {

        BadAddress(String i) { super(Code.BAD_ADDRESS, i); }
        BadAddress(Throwable e) { super(Code.BAD_ADDRESS, e); }

        private static final long serialVersionUID = -8010793100844536131L;
    }

     public PoolServerAddress(String scheme, String host, int port)
        throws BadAddress {
        uri = makeURI(scheme, host, port);
    }

    public PoolServerAddress(String uri) throws BadAddress {
        try {
            final URI u = new URI(uri);
            this.uri = makeURI(u.getScheme(), u.getHost(), u.getPort());
        } catch (URISyntaxException e) {
            throw new BadAddress(e);
        }
    }

    public String scheme() { return uri.getScheme(); }

    public String host() { return uri.getHost(); }

    public int port() { return uri.getPort(); }

    @Override public boolean equals(Object o) {
        if (!(o instanceof PoolServerAddress)) return false;
        return uri.equals(((PoolServerAddress)o).uri);
    }

    @Override public int hashCode() {
        return uri.hashCode();
    }

    @Override public String toString() {
        return uri.toString();
    }

    private static final int DEFAULT_PORT = 65455;

    private static URI makeURI(String scheme, String host, int port)
        throws BadAddress {
        if (port == PoolServers.DEFAULT_PORT) port = DEFAULT_PORT;
        try {
            return new URI(scheme, "", host, port, "", "", "");
        } catch (URISyntaxException e) {
            throw new BadAddress(e);
        }
    }

    private final URI uri;
}
