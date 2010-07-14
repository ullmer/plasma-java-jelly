// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.jcip.annotations.Immutable;

/**
 *
 * Created: Wed Jun  9 16:20:42 2010
 *
 * @author jao
 */
@Immutable
public final class PoolServerAddress {

    public static final int DEFAULT_PORT = 65456;
    public static final String DEFAULT_SCHEME = "tcp";
    public static final String DEFAULT_HOST = "localhost";

    public static class BadAddress extends PoolException {

        public BadAddress(String i) { super(Code.BAD_ADDRESS, i); }
        public BadAddress(Throwable e) { super(Code.BAD_ADDRESS, e); }

        public BadAddress(long sc) {
            super(Code.BAD_ADDRESS, sc, "Server rejected address");
        }

        private static final long serialVersionUID = -8010793100844536131L;
    }

    public static PoolServerAddress fromURI(String uri)
        throws BadAddress {
        Matcher matcher = ADDR_PATT.matcher(uri);
        if (!matcher.lookingAt()) return null;
        final String scheme = matcher.group(1);
        final String host = matcher.group(2);
        final String port = matcher.group(3);
        final int p = port == null ? -1 : Integer.parseInt(port);
        return new PoolServerAddress(scheme, host, p);
    }

    public PoolServerAddress(String scheme, String host, int port)
        throws BadAddress {
         this.scheme = checkScheme(scheme);
         this.host = checkHost(host);
         this.port = port < 0 ? DEFAULT_PORT : port;
         stringRep = this.scheme
             + (this.host.isEmpty() ? "" : ("://" + this.host))
             + (this.port == DEFAULT_PORT ? "" : ":" + this.port);
    }

    public PoolServerAddress(String host, int port) throws BadAddress {
        this("", host, port);
    }

    public PoolServerAddress(String host) throws BadAddress {
        this("", host, -1);
    }

    public String scheme() { return scheme; }

    public String host() { return host; }

    public int port() { return port; }

    @Override public boolean equals(Object o) {
        if (!(o instanceof PoolServerAddress)) return false;
        final PoolServerAddress oa = (PoolServerAddress)o;
        return stringRep.equals(oa.stringRep);
    }

    @Override public int hashCode() {
        return toString().hashCode();
    }

    @Override public String toString() {
        return stringRep;
    }

    static final String ADDR_REGEX =
        "(?:([\\p{Alpha}]+)://)?(?:([\\p{Alpha}]+)(?::(\\d+))?)?";

    private static final Pattern ADDR_PATT = Pattern.compile(ADDR_REGEX);

    private static String checkScheme(String scm) throws BadAddress {
        if (scm == null || scm.isEmpty()) return DEFAULT_SCHEME;
        if (scm.indexOf(':') > 0 || scm.indexOf('/') > 0)
            throw new BadAddress("Scheme cannot contain ' or /");
        return scm;
    }

    private static String checkHost(String host) throws BadAddress {
        if (host == null || host.isEmpty()) return DEFAULT_HOST;
        if (host.indexOf(':') > 0 || host.indexOf('/') > 0)
            throw new BadAddress("Host cannot contain `:' or `/'");
        return host;
    }

    private final String scheme;
    private final String host;
    private final int port;
    private final String stringRep;
}
