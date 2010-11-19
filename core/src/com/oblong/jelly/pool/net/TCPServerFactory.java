// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.net;

import java.io.IOException;
import java.util.HashSet;
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
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.pool.PoolServerFactory;

@ThreadSafe
public final class TCPServerFactory extends PoolServerFactory {

    @Override public boolean isRemote() { return true; }

    @Override public PoolServer getServer(PoolServerAddress address) {
        return new Server(factory, address);
    }

    @Override public synchronized Set<PoolServer> servers() {
        final Set<PoolServer> result = PoolServerFactory.cached(SCM);
        final JmDNS dns = jmDNS();
        if (dns != null) {
            for (ServiceInfo inf : dns.list(SCM_SRV)) {
                final PoolServer server = fromInfo (inf);
                if (server != null) result.add(server);
            }
        }
        return result;
    }

    @Override synchronized
    public boolean addListener(PoolServers.Listener listener) {
        final JmDNS dns = jmDNS();
        if (dns == null) return false;
        dns.addServiceListener(SCM_SRV, new Listener(listener));
        return true;
    }

    public static void register() {
        register(SCM, new TCPServerFactory());
    }

    private static PoolServer fromInfo(ServiceInfo inf) {
        if (!inf.hasData()) return null;
        try {
            final String url = inf.getURL(SCM);
            final String name = inf.getName();
            final PoolServerAddress addr = PoolServerAddress.fromURI(url);
            final PoolServer cached = PoolServerFactory.remove(addr);
            final Set<String> subtypes = cached == null ?
                new HashSet<String>() : cached.subtypes();
            final String subtype = inf.getSubtype();
            if (subtype != null && subtype.length() > 0)
                subtypes.add(subtype);
            final PoolServer srv = new Server(factory, addr, name, subtypes);
            return PoolServerFactory.cache(srv);
        } catch (PoolException e) {
            return null;
        }
    }

    private static final class Listener implements ServiceListener {
        Listener(PoolServers.Listener lsn) { listener = lsn; }

        @Override public void serviceAdded(ServiceEvent e) {}

        @Override public void serviceRemoved(ServiceEvent e) {
            final PoolServer s = fromInfo(e.getInfo());
            if (s != null)
                listener.serverRemoved(PoolServerFactory.remove(s.address()));
        }

        @Override public void serviceResolved(ServiceEvent e) {
            final PoolServer s = fromInfo(e.getInfo());
            if (s != null) listener.serverAdded(PoolServerFactory.cache(s));
        }

        final PoolServers.Listener listener;
    }

    private static final JmDNS jmDNS() {
        if (jmDNS == null) {
            try {
                jmDNS = JmDNS.create();
            } catch (IOException e) {
                logger.severe("Error opening zeroconf: " + e.getMessage());
            }
        }
        return jmDNS;
    }

    private static final NetConnectionFactory factory =
        new TCPConnection.Factory();

    private static final String SCM = "tcp";
    private static final String SCM_SRV = "_pool-server._tcp";
    private static JmDNS jmDNS = null;
    private static final Logger logger =
        Logger.getLogger(TCPServerFactory.class.getName());
}
