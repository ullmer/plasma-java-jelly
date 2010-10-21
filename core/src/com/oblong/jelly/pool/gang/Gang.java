// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.gang;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.oblong.jelly.Hose;
import com.oblong.jelly.GangException;
import com.oblong.jelly.Pool;
import com.oblong.jelly.PoolAddress;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.HoseGang;
import com.oblong.jelly.Protein;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public final class Gang extends HoseGang {

    public Gang(int prefetch) {
        fetchers = new ConcurrentHashMap<String, Fetcher>();
        queue = new FetchQueue(prefetch);
    }

    @Override public int count() { return fetchers.size(); }

    @Override public boolean add(String name, PoolAddress addr, long index)
        throws PoolException {
        final Hose h = Pool.participate(addr);
        if (name != null) h.setName(name);
        if (index > 0) h.seekTo(index);
        return addFetcher(h, true);
    }

    @Override public boolean remove(String name) {
        final Fetcher f = fetchers.remove(name);
        if (f != null) f.withdraw();
        return f != null;
    }

    @Override public void disband() {
        for (Fetcher f : fetchers.values()) f.withdraw();
        fetchers.clear();
    }

    @Override public Protein next()
        throws GangException, InterruptedException {
        Protein p = tryNext();
        if (p == null) p = queue.take();
        return p;
    }

    @Override public Protein awaitNext(long period, TimeUnit unit)
        throws GangException, TimeoutException, InterruptedException {
        Protein p = tryNext();
        if (p == null) p = queue.next(period, unit);
        if (p == null) throw new TimeoutException();
        return p;
    }

    @Override public boolean wakeUp() {
        return queue.wakeUp();
    }

    private Protein tryNext() throws GangException, InterruptedException {
        final Protein p = queue.next(0, TimeUnit.SECONDS);
        if (p == null) launchFetchers();
        return p;
    }

    private void launchFetchers() {
        for (Fetcher f : fetchers.values()) {
            if (f.isRunnable()) executor.execute(f);
        }
    }

    private boolean addFetcher(Hose h, boolean e) {
        final Fetcher old = fetchers.put(h.name(), new Fetcher(h, queue, e));
        if (old != null) old.withdraw();
        return old == null;
    }

    private final ConcurrentHashMap<String, Fetcher> fetchers;
    private final FetchQueue queue;

    private static final Executor executor = Executors.newCachedThreadPool();
}
