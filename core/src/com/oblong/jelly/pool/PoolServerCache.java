// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.jcip.annotations.ThreadSafe;

import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServerAddress;

@ThreadSafe
public class PoolServerCache {

    public PoolServer add(PoolServer s) {
        if (s == null) return null;
        final PoolServer old = servers.putIfAbsent(s.qualifiedName(), s);
        return old == null ? s : old;
    }

    public PoolServer get(String qname) {
        return servers.get(qname);
    }

    public boolean contains(PoolServer s) {
        return servers.containsValue(s);
    }

    public boolean contains(PoolServerAddress a, String n, String s) {
        for (PoolServer srv : servers.values())
            if (check(srv, a, n, s)) return true;
        return false;
    }

    public PoolServer get(String n, String s) {
        for (PoolServer srv : servers.values())
            if (check(srv, null, n, s)) return srv;
        return null;
    }

    public Set<PoolServer> get(PoolServerAddress a, String n, String s) {
        Set<PoolServer> result = new HashSet<PoolServer>();
        for (PoolServer srv : servers.values())
            if (check(srv, a, n, s)) result.add(srv);
        return result;
    }

    public Set<PoolServer> get(PoolServerAddress addr) {
        Set<PoolServer> result = new HashSet<PoolServer>();
        for (PoolServer s : servers.values())
            if (s.address().equals(addr)) result.add(s);
        return result;
    }

    public PoolServer remove(String qname) {
        return servers.remove(qname);
    }

    public PoolServer remove(PoolServer s) {
        return remove(s.qualifiedName());
    }

    public Set<PoolServer> remove(PoolServerAddress a, String n, String s) {
        Set<PoolServer> result = new HashSet<PoolServer>();
        for (PoolServer srv : servers.values())
            if (check(srv, a, n, s))
                result.add(servers.remove(srv.qualifiedName()));
        return result;
    }

    public void clear() { servers.clear(); }

    public int size() { return servers.size(); }

    public String dump() {
        StringBuilder b = new StringBuilder();
        b.append(servers.size());
        b.append(" servers");
        for (String s : servers.keySet()) b.append("\n - ").append(s);
        return b.toString();
    }

    public Set<PoolServer> servers() {
        return new HashSet<PoolServer>(servers.values());
    }

    private static boolean check(PoolServer s,
                                 PoolServerAddress a,
                                 String n,
                                 String t) {
        return ((n == null || n.equals(s.name())) &&
                (t == null || t.equals(s.subtype())) &&
                (a == null || a.equals(s.address())));
    }

    private ConcurrentHashMap<String, PoolServer> servers =
        new ConcurrentHashMap<String, PoolServer>();
}
