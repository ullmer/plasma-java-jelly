// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import net.jcip.annotations.ThreadSafe;

import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServers;
import com.oblong.jelly.PoolServers.Listener;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.pool.PoolServerFactory;

@ThreadSafe
public final class TCPServerFactory
    extends PoolServerFactory implements ServiceListener {

    @Override public boolean isRemote() { return true; }

    @Override public PoolServer getServer(PoolServerAddress address) {
        return new Server(factory, address);
    }

    @Override synchronized public Set<PoolServer> servers() {
        final Set<PoolServer> result = PoolServerFactory.cached(SCM);
        if (jmDNS == null) {
            final JmDNS dns = jmDNS();
            if (dns != null) {
                for (ServiceInfo inf : dns.list(SCM_SRV)) {
                    final PoolServer server = fromInfo(inf, true);
                    if (server != null)
                        result.add(PoolServerFactory.cache(server));
                }
                jmDNS.addServiceListener(SCM_SRV, this);
            }
        }
        return result;
    }

    @Override synchronized public boolean addListener(Listener listener) {
        if (jmDNS == null) {
            final JmDNS dns = jmDNS();
            if (dns == null) return false;
            dns.addServiceListener(SCM_SRV, this);
        }
        listeners.add(listener);
        return true;
    }

    public static void register() {
        register(SCM, new TCPServerFactory());
    }

    public static void unregister() {
        final TCPServerFactory f =
            (TCPServerFactory)PoolServerFactory.unregister(SCM);
        if (f != null && f.jmDNS != null) f.jmDNS.close();
    }

    public static void reset() {
        unregister();
        register();
    }

    private PoolServer fromInfo(ServiceInfo inf, boolean adding) {
        try {
            final String url = inf.getURL(SCM);
            final String name = inf.getName();
            final PoolServerAddress addr = PoolServerAddress.fromURI(url);
            final PoolServer cached =
                adding ? PoolServerFactory.cached(addr) : null;
            final Set<String> subtypes = cached == null ?
                new HashSet<String>() : cached.subtypes();
            final String subtype = inf.getSubtype();
            if (subtype != null && subtype.length() > 0)
                subtypes.add(subtype);
            final PoolServer srv = new Server(factory, addr, name, subtypes);
            return srv;
        } catch (PoolException e) {
            logger.warning("Unable to extract server from info " + inf
                           + ", error: " + e.getMessage());
            return null;
        }
    }

    @Override public void serviceAdded(ServiceEvent e) {
        logger.info("Service added event: " + e);
    }

    @Override synchronized public void serviceRemoved(ServiceEvent e) {
        logger.info("Service removed event: " + e);
        final PoolServer s = fromInfo(e.getInfo(), false);
        if (s != null) {
            PoolServerFactory.remove(s.name());
            for (Listener l : listeners) l.serverRemoved(s);
        }
    }

    @Override synchronized public void serviceResolved(ServiceEvent e) {
        logger.info("Service resolved event: " + e);
        final PoolServer s = fromInfo(e.getInfo(), true);
        if (s != null) {
            PoolServerFactory.cache(s);
            for (Listener l : listeners) l.serverAdded(s);
        }
    }

    private JmDNS jmDNS() {
        if (jmDNS == null) {
            try {
                jmDNS = JmDNS.create();
            } catch (IOException e) {
                logger.severe("Error opening zeroconf: " + e.getMessage());
            }
        }
        return jmDNS;
    }

    private final NetConnectionFactory factory = new TCPConnection.Factory();
    private final List<Listener> listeners = new ArrayList<Listener>();
    private volatile JmDNS jmDNS = null;

    private static final String SCM = "tcp";
    private static final String SCM_SRV = "_pool-server._tcp.local.";
    private static final Logger logger =
        Logger.getLogger(TCPServerFactory.class.getName());
}
