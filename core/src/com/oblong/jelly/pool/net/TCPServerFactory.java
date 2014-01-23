// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.net;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import com.oblong.util.ExceptionHandler;
import net.jcip.annotations.ThreadSafe;

import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServers.Listener;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.pool.PoolServerCache;
import com.oblong.jelly.pool.PoolServerFactory;

@ThreadSafe
public final class TCPServerFactory
    extends PoolServerFactory implements ServiceListener {

    @Override public boolean isRemote() { return true; }

    @Override
    public PoolServer getServer(PoolServerAddress address,
                                String name,
                                String subtype) {
        final String qname = Server.qualifiedName(address,
                                                  name,
                                                  subtype,
                                                  factory);
        final PoolServer s = cache.get(qname);
        return s == null ? new Server(factory, address, name, subtype) : s;
    }

    @Override synchronized
    public Set<PoolServer> servers(PoolServerAddress a, String n, String s) {
        if (jmDNS == null) {
            final JmDNS d = jmDNS();
            if (d != null) {
                for (ServiceInfo inf : d.list(SCM_SRV))
                    cache.add(fromInfo(inf));
            }
        }
        return cache.get(a, n, s);
    }

    @Override synchronized public boolean addListener(Listener listener) {
        if (jmDNS == null) {
            final JmDNS dns = jmDNS();
            if (dns == null) return false;
        }
        listeners.add(listener);
        return true;
    }

    @Override public void serviceAdded(ServiceEvent e) {
    }

    @Override synchronized public void serviceResolved(ServiceEvent e) {
        logger.info("Service resolved event: " + e);
        final String name = e.getName();
        final ServiceInfo info = e.getInfo();
        if (name != null && name.equals(info.getName()))
            // The test above can fail, probably due to jmDNS bugs
            serverAdded(cache.add(fromInfo(info)));
    }

    @Override synchronized public void serviceRemoved(ServiceEvent e) {
        logger.info("Service removed event: " + e);
        final PoolServer server = fromInfo(e.getInfo());
        if (server != null) {
            for (PoolServer s : cache.remove(null, server.name(), null))
                serverRemoved(s);
        }
    }

    public static void register() {
        register(SCM, new TCPServerFactory());
    }

    public static void unregister() {
        final TCPServerFactory f =
            (TCPServerFactory)PoolServerFactory.unregister(SCM);
        if (f != null) f.closeJmDNS();
        cache.clear();
    }

    public static void reset() {
        unregister();
        register();
    }

    private PoolServer serverAdded(PoolServer s) {
        if (s != null) for (Listener l : listeners) l.serverAdded(s);
        return s;
    }

    private PoolServer serverRemoved(PoolServer s) {
        if (s != null) for (Listener l : listeners) l.serverRemoved(s);
        return s;
    }

    private Server fromInfo(ServiceInfo inf) {
        try {
            final String url = inf.getURL(SCM);
            final String name = inf.getName();
            final PoolServerAddress addr = PoolServerAddress.fromURI(url);
            final String subtype = inf.getSubtype();
            return new Server(factory, addr, name, subtype);
        } catch (Throwable e) {
            ExceptionHandler.handleException(e);
            logger.warning("Unable to extract server from info " + inf
                           + ", error: " + e.getMessage());
            return null;
        }
    }

    private synchronized void closeJmDNS() {
        if (jmDNS != null) {
            try {
                jmDNS.close();
            } catch (IOException e) {
                logger.severe("Error closing jmDNS: " + e);
            } finally {
                jmDNS = null;
            }
        }
    }

    private JmDNS jmDNS() {
        if (jmDNS == null) {
            try {
                jmDNS = JmDNS.create();
                jmDNS.addServiceListener(SCM_SRV, this);
            } catch (IOException e) {
                logger.severe("Error opening zeroconf: " + e.getMessage());
            }
        }
        return jmDNS;
    }

    private final NetConnectionFactory factory = new TCPConnection.Factory();
    private final List<Listener> listeners = new ArrayList<Listener>();
    private volatile JmDNS jmDNS = null;

    public static final String SCM = "tcp";
    private static final String SCM_SRV = "_pool-server._tcp.local.";
    private static final Logger logger =
        Logger.getLogger(TCPServerFactory.class.getName());

    private static final PoolServerCache cache = new PoolServerCache();
}
