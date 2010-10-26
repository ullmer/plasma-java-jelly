// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.gang;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.oblong.jelly.Hose;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.Protein;
import com.oblong.jelly.GangException;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
final class FetchQueue {

    FetchQueue() {
        queue = new LinkedBlockingQueue<Elem>();
        disposed = new LinkedBlockingQueue<Elem>();
    }

    void put(Hose h) throws InterruptedException {
        queue.put(new Elem(h));
    }

    void put(GangException e) throws InterruptedException {
        queue.put(new Elem(e));
    }

    void put(Hose hose, PoolException e) throws InterruptedException {
        put(new GangException(hose.name(), hose.poolAddress(), e));
    }

    boolean wakeUp() {
        return queue.offer(WAKE_TOKEN);
    }

    Protein next(long t, TimeUnit u)
        throws GangException, InterruptedException {
        return (t < 0 || u == null) ? null : protein(queue.poll(t, u));
    }

    Protein take() throws GangException, InterruptedException {
        return protein(queue.take());
    }

    void available(Hose h) throws InterruptedException {
        disposed.put(new Elem(h));
    }

    Hose available() throws InterruptedException {
        final Elem e = disposed.take();
        if (e == WAKE_TOKEN) throw new InterruptedException();
        return e.hose;
    }

    void wakeUpHoseQueue() {
        try { disposed.put(WAKE_TOKEN); } catch (InterruptedException e) {}
    }

    void clear() {
        queue.clear();
        disposed.clear();
    }

    private Protein protein(Elem e)
        throws GangException, InterruptedException {
        if (e == null) return null;
        if (e == WAKE_TOKEN) throw new InterruptedException();
        if (e.error != null) throw e.error;
        try {
            return  e.hose.next();
        } catch (PoolException ex) {
            throw new GangException(e.hose.name(), e.hose.poolAddress(), ex);
        } finally {
            disposed.put(e);
        }
    }

    private static class Elem {
        Elem(Hose h) { hose = h; }
        Elem(GangException e) { error = e; }
        Hose hose = null;
        GangException error = null;
    };

    private final BlockingQueue<Elem> queue;
    private final BlockingQueue<Elem> disposed;

    private static final Elem WAKE_TOKEN = new Elem((Hose)null);
}
