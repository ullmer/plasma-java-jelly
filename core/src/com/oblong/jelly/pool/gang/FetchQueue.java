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

    Protein next(long t, TimeUnit u) throws GangException {
        try {
            return (t < 0 || u == null) ? null : protein(queue.poll(t, u));
        } catch (InterruptedException e) {
            return null;
        }
    }

    Protein take() throws GangException {
        try {
            return protein(queue.take());
        } catch (InterruptedException e) {
            return null;
        }
    }

    private Protein protein(Elem e) throws GangException {
        if (e == null) return null;
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
}
