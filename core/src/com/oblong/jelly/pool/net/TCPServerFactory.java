// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import net.jcip.annotations.ThreadSafe;

import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServers.Listener;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.pool.PoolServerFactory;

@ThreadSafe
public final class TCPServerFactory
    extends PoolServerFactory implements ServiceListener {

    @Override public boolean isRemote() { return true; }

    @Override
    public PoolServer getServer(PoolServerAddress address, String subtype) {
        final String name = address.toString();
        final String qname = Server.qualifiedName(name, subtype, factory);
        PoolServer srv = cache.get(qname);
        if (srv == null) {
            srv = new Server(factory, address, name, subtype);
            final PoolServer old = cache.putIfAbsent(qname, srv);
            if (old != null) srv = old;
        }
        return srv;
    }

    @Override synchronized public Set<PoolServer> servers() {
        final Set<PoolServer> result =
            new HashSet<PoolServer>(cache.values());
        if (jmDNS == null) {
            final JmDNS dns = jmDNS();
            if (dns != null) {
                for (ServiceInfo inf : dns.list(SCM_SRV)) {
                    final Server server = fromInfo(inf);
                    if (server != null) {
                        cache.put(server.qualifiedName(), server);
                        result.add(server);
                    }
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
        cache.clear();
        final TCPServerFactory f =
            (TCPServerFactory)PoolServerFactory.unregister(SCM);
        if (f != null && f.jmDNS != null) {
            try {
                f.jmDNS.close();
            } catch (IOException e) {
                logger.severe("Error closing jmDNS: " + e);
            }
        }
    }

    public static void reset() {
        unregister();
        register();
    }

    @Override public void serviceAdded(ServiceEvent e) {
    }

    @Override synchronized public void serviceRemoved(ServiceEvent e) {
        logger.info("Service removed event: " + e);
        final Server s = fromInfo(e.getInfo());
        if (s != null) {
            cache.remove(s.qualifiedName());
            for (Listener l : listeners) l.serverRemoved(s);
        }
    }

    @Override synchronized public void serviceResolved(ServiceEvent e) {
        logger.info("Service resolved event: " + e);
        final Server s = fromInfo(e.getInfo());
        if (s != null) {
            cache.put(s.qualifiedName(), s);
            for (Listener l : listeners) l.serverAdded(s);
        }
    }

    private Server fromInfo(ServiceInfo inf) {
        try {
            final String url = inf.getURL(SCM);
            final String name = inf.getName();
            final PoolServerAddress addr = PoolServerAddress.fromURI(url);
            final String subtype = inf.getSubtype();
            return new Server(factory, addr, name, subtype);
        } catch (Throwable e) {
            logger.warning("Unable to extract server from info " + inf
                           + ", error: " + e.getMessage());
            return null;
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

    private static final ConcurrentHashMap<String, PoolServer> cache =
        new ConcurrentHashMap<String, PoolServer>();
}
