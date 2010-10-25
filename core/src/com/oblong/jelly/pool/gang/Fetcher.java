// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.gang;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.jcip.annotations.ThreadSafe;

import com.oblong.jelly.Hose;
import com.oblong.jelly.PoolException;

@ThreadSafe
final class Fetcher implements Runnable {

    public final void run() {
        try {
            while (!hoses.isEmpty()) {
                final Hose h = queue.available();
                if (h != null && hoses.containsKey(h.name())) {
                    try {
                        if (h.poll()) queue.put(h);
                        else queue.available(h);
                    } catch (PoolException e) {
                        if (errors) queue.put(h, e);
                        queue.available(h);
                    }
                }
            }
        } catch (InterruptedException e) {
            // let the thread die
        }
    }

    Fetcher(FetchQueue q) {
        hoses = new ConcurrentHashMap<String, Hose>();
        queue = q;
        errors = true;
    }

    boolean add(String name, Hose hose) throws PoolException {
        final Hose old = hoses.put(name, hose);
        if (old != null) old.withdraw();
        hose.setName(name);
        try {
            queue.available(hose);
        } catch (InterruptedException e) {
            throw new PoolException("Thread was interrupted: "
                                    + e.getMessage());
        }
        return old == null;
    }

    boolean remove(String name) {
        final Hose h = hoses.remove(name);
        if (h != null) h.withdraw();
        return h != null;
    }

    void removeAll() {
        errors = false;
        for (Hose h : hoses.values()) h.withdraw();
        hoses.clear();
        queue.wakeUpHoseQueue();
    }

    Set<String> names() { return hoses.keySet(); }

    int count() { return hoses.size(); }

    private final ConcurrentHashMap<String, Hose> hoses;

    private final FetchQueue queue;
    private volatile boolean errors;
}
