// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.Set;

import com.oblong.jelly.pool.PoolServerFactory;

/**
 * A collection of functions creating and listing {@link PoolServer}
 * instances.
 *
 * <p>Besides a PoolServer factory method, {@link #get}, this
 * non-instantiable class provides static methods to list available
 * servers, and register callbacks to be invoked when new ones become
 * available. The asynchronous interface accommodates those servers
 * that are able to announce themselves when they join the local
 * network using protocols such as Bonjour or Avahi.
 *
 * @author jao
 */
public final class PoolServers {
    /**
     * Provides an object implementing PoolServer given its address.
     *
     * <p> Pool servers are uniquely identified by their address,
     * which acts in this respect as a URI. This method actually
     * returns the same object when called repeatedly with the same
     * argument.
     *
     * <p> If there's no PoolServer with the given address (for
     * instance, because its protocol is not registered), this method
     * returns null.
     */
    public static PoolServer get(PoolServerAddress address) {
        return PoolServerFactory.get(address);
    }

    /**
     * Convenience method that construct a PoolServerAddress and calls
     * {@link #get(PoolServerAddress) get} on it, returning null if
     * the URI is malformed.
     */
    public static PoolServer get(String uri) {
        try {
            return get(PoolServerAddress.fromURI(uri));
        } catch (BadAddressException e) {
            return null;
        }
    }

    /**
     * Lists the currently known servers for the given scheme.
     *
     * Of particular interest are those schemes, such as "tcp",
     * corresponding to remote servers that can announce themselves
     * using protocols such as zeroconf. {@link #remoteServers}
     * retrieves a list of discovered servers using any of the remote
     * schemes.
     */
    public static Set<PoolServer> servers(String scheme) {
        return PoolServerFactory.servers(scheme);
    }

    /**
     * Lists the currently discovered remote servers.
     *
     * Remote pool servers may be able to annonunce themselves via
     * zeroconf. This method will use that protocol to list servers
     * discovered in the local network.
     */
    public static Set<PoolServer> remoteServers() {
        return PoolServerFactory.remoteServers();
    }

    /**
     * Listener for modifications on the list of current servers.
     *
     * @see #addListener
     */
    public interface Listener {
        /**
         * Callback for server additions. This method is called when a
         * new server is discovered, or whether it's connected to for
         * the first time.
         *
         * <p>When this callback is invoked, @c server will already be
         * present in the corresponding set returned by {@link
         * PoolServers#servers}.
         */
        void serverAdded(PoolServer server);
        /**
         * Callback for server removals. This method is called when a
         * previously discovered server disappears from the network.
         *
         * <p>When this callback is invoked, @c server will already be
         * absent in the corresponding set returned by {@link
         * PoolServers#servers}.
         */
        void serverRemoved(PoolServer server);
    }

    /**
     * Adds a listener for server addition or removal. Using listeners
     * allows treating server announcements (e.g., via zeroconf) in an
     * asynchronous manner, and lets you keep up to date the list of
     * extant servers using a push model (as is typical, for instance,
     * in zeroconf service browsers.
     *
     * <p>The listener will only react to additions or removals of
     * servers with the given scheme. See also {@link
     * addRemoteListener}.
     */
    public static boolean addListener(String scheme, Listener listener) {
        return PoolServerFactory.addListener(scheme, listener);
    }

    /**
     * Registers the given listener for all remote schemes.
     *
     * @see #addListener
     */
    public static void addRemoteListener(Listener listener) {
        PoolServerFactory.addRemoteListener(listener);
    }

    public static void main(String[] args) throws InterruptedException {
        addRemoteListener(new Listener() {
                public void serverAdded(PoolServer s) {
                    System.out.println("added: " + s);
                }
                public void serverRemoved(PoolServer s) {
                    System.out.println("removed: " + s);
                }
            });
        while (true) {
            Thread.sleep(5000);
            Set<PoolServer> servers = servers("tcp");
            System.out.println("Found " + servers.size() + " servers:");
            for (PoolServer s : servers)
                System.out.println("\t- " + s);
        }
    }

    private PoolServers() {}

    static {
        com.oblong.jelly.pool.net.TCPServerFactory.register();
        com.oblong.jelly.pool.mem.MemServerFactory.register();
    }
}
