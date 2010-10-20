// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.gang;

import com.oblong.jelly.Hose;
import com.oblong.jelly.PoolException;

import net.jcip.annotations.ThreadSafe;
import net.jcip.annotations.GuardedBy;

@ThreadSafe
final class Fetcher implements Runnable {

    public final void run() {
        synchronized (this) {
            if (!idle) return;
            idle = false;
        }
        try {
            try {
                queue.put(hose.awaitNext());
            } catch (PoolException e) {
                if (errors) queue.put(hose, e);
            }
        } catch (InterruptedException e) {
            // let the thread die
        } finally {
            synchronized (this) { idle = true; }
        }
    }

    Fetcher(Hose h, FetchQueue q, boolean e) {
        hose = h;
        queue = q;
        errors = e;
        idle = true;
    }

    synchronized boolean isIdle() { return idle; }

    synchronized void withdraw() {
        try {
            errors = false;
            hose.withdraw();
        } catch (Exception e) {
        }
    }

    private final Hose hose;
    private final FetchQueue queue;
    private volatile boolean errors;
    @GuardedBy("this") private boolean idle;
}
