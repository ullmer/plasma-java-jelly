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
            if (!runnable) return;
            runnable = false;
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
            synchronized (this) { runnable = hose.isConnected(); }
        }
    }

    Fetcher(Hose h, FetchQueue q, boolean e) {
        hose = h;
        queue = q;
        errors = e;
        runnable = true;
    }

    synchronized boolean isRunnable() { return runnable; }

    void withdraw() {
        synchronized (this) { runnable = false; }
        errors = false;
        hose.withdraw();
    }

    private final Hose hose;
    private final FetchQueue queue;
    private volatile boolean errors;
    @GuardedBy("this") private boolean runnable;
}
