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
                synchronized (this) {
                    if (!hoses.isEmpty()
                        && h != null
                        && hoses.containsKey(h.name())) {
                        try {
                            if (h.poll()) queue.put(h);
                            else queue.available(h);
                        } catch (PoolException e) {
                            if (errors) queue.put(h, e);
                        }
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
        hose.setName(name);
        final Hose old = hoses.put(name, hose);
        try {
            queue.available(hose);
        } catch (InterruptedException e) {
            throw new PoolException("Thread interrupted: " + e.getMessage());
        }
        if (old != null) old.withdraw();
        return old == null;
    }

    boolean remove(String name) {
        final Hose h = hoses.remove(name);
        if (h != null) h.withdraw();
        return h != null;
    }

    void removeAll() {
        synchronized (this) {
            errors = false;
            for (Hose h : hoses.values()) h.withdraw();
            hoses.clear();
            queue.clear();
        }
        queue.wakeUpHoseQueue();
    }

    Set<String> names() { return hoses.keySet(); }

    int count() { return hoses.size(); }

    private final ConcurrentHashMap<String, Hose> hoses;

    private final FetchQueue queue;
    private volatile boolean errors;
}
