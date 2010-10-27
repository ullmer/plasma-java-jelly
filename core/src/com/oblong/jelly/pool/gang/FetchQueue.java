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
        ready = new LinkedBlockingQueue<Elem>();
        disposed = new LinkedBlockingQueue<Elem>();
        waiting = new LinkedBlockingQueue<Elem>();
    }

    void put(Hose h) throws InterruptedException {
        ready.put(new Elem(h));
    }

    void put(GangException e) throws InterruptedException {
        ready.put(new Elem(e));
    }

    void put(Hose hose, PoolException e) throws InterruptedException {
        put(new GangException(hose.name(), hose.poolAddress(), e));
        disposed.put(new Elem(hose));
    }

    boolean wakeUp() {
        return ready.offer(WAKE_TOKEN);
    }

    Protein next(long t, TimeUnit u)
        throws GangException, InterruptedException {
        waiting.drainTo(disposed);
        return t < 0 ? protein(ready.take()) : protein(ready.poll(t, u));
    }

    void available(Hose h) { waiting.offer(new Elem(h)); }

    Hose available() throws InterruptedException {
        final Elem e = disposed.take();
        if (e == WAKE_TOKEN) throw new InterruptedException();
        return e.hose;
    }

    void wakeUpHoseQueue() { disposed.offer(WAKE_TOKEN); }

    void clear() {
        ready.clear();
        disposed.clear();
        waiting.clear();
    }

    private Protein protein(Elem e)
        throws GangException, InterruptedException {
        if (e == null) return null;
        if (e == WAKE_TOKEN) throw new InterruptedException();
        if (e.error != null) throw e.error;
        try {
            return e.hose.next();
        } catch (PoolException ex) {
            throw new GangException(e.hose.name(), e.hose.poolAddress(), ex);
        } finally {
            disposed.offer(e);
        }
    }

    private static class Elem {
        Elem(Hose h) { hose = h; }
        Elem(GangException e) { error = e; }
        Hose hose = null;
        GangException error = null;
    };

    private final BlockingQueue<Elem> ready;
    private final BlockingQueue<Elem> disposed;
    private final BlockingQueue<Elem> waiting;

    private static final Elem WAKE_TOKEN = new Elem((Hose)null);
}
