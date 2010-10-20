// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.gang;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.oblong.jelly.Hose;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.Protein;
import com.oblong.jelly.GangException;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
final class FetchQueue {

    FetchQueue(int size) {
        queue = new ArrayBlockingQueue<Elem>(size);
    }

    void put(Protein p) throws InterruptedException {
        queue.put(new Elem(p));
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

    private Protein protein(Elem e)
        throws GangException, InterruptedException {
        if (e == null) return null;
        if (e == WAKE_TOKEN) throw new InterruptedException();
        if (e.error != null) throw e.error;
        return e.protein;
    }

    private static class Elem {
        Elem(Protein p) { protein = p; }
        Elem(GangException e) { error = e; }
        Protein protein = null;
        GangException error = null;
    };

    private final BlockingQueue<Elem> queue;

    private static final Elem WAKE_TOKEN = new Elem((Protein)null);
}
