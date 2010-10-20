// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.gang;

import com.oblong.jelly.Hose;
import com.oblong.jelly.PoolException;

import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
final class Fetcher implements Runnable {

    public final void run() {
        done = false;
        try {
            try {
                queue.put(hose.awaitNext());
            } catch (PoolException e) {
                if (errors) queue.put(hose, e);
            }
        } catch (InterruptedException e) {
            // let the thread die
        }
        done = true;
    }

    Fetcher(Hose h, FetchQueue q, boolean ie) {
        hose = h;
        queue = q;
        errors = !ie;
        done = true;
    }

    boolean isDone() { return done; }

    void withdraw() {
        try {
            errors = false;
            hose.withdraw();
        } catch (Exception e) {
        }
    }

    private final Hose hose;
    private final FetchQueue queue;
    private volatile boolean errors;
    private volatile boolean done;
}
