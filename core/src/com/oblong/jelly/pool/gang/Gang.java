// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.gang;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.oblong.jelly.GangException;
import com.oblong.jelly.Hose;
import com.oblong.jelly.HoseGang;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.Protein;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public final class Gang extends HoseGang {

    public Gang(int prefetch) {
        queue = new FetchQueue();
        fetcher = new Fetcher(queue);
        fetcherThread = null;
    }

    @Override public int count() { return fetcher.count(); }
    @Override public Set<String> names() { return fetcher.names(); }

    @Override public boolean remove(String name) {
        return fetcher.remove(name);
    }

    @Override public void disband() {
        fetcher.removeAll();
        fetcherThread = null;
    }

    @Override public Protein next()
        throws GangException, InterruptedException {
        return queue.take();
    }

    @Override public Protein awaitNext(long period, TimeUnit unit)
        throws GangException, TimeoutException, InterruptedException {
        final Protein p = queue.next(period, unit);
        if (p == null) throw new TimeoutException();
        return p;
    }

    @Override public boolean wakeUp() {
        return queue.wakeUp();
    }

    @Override protected boolean doAdd(String name, Hose hose)
        throws PoolException {
        if (fetcherThread == null) {
            fetcherThread = new Thread(fetcher);
            fetcherThread.start();
        }
        return fetcher.add(name, hose);
    }

    private final Fetcher fetcher;
    private final FetchQueue queue;
    private Thread fetcherThread;

}
