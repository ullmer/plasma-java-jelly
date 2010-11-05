// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.mem;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.jcip.annotations.ThreadSafe;

import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.pool.PoolProtein;

@ThreadSafe
final class MemPool {

    static boolean exists(String name) {
        return pools.containsKey(name);
    }

    static MemPool create(String name) {
        if (exists(name)) return null;
        final MemPool p = new MemPool(name);
        final MemPool old = pools.putIfAbsent(name, p);
        return old == null ? p : old;
    }

    static MemPool get(String name) { return pools.get(name); }

    static boolean dispose(String name) {
        return pools.remove(name) != null;
    }

    static Set<String> names() { return pools.keySet(); }

    static Slaw[] slawNames() {
        final Set<String> ks = pools.keySet();
        final Slaw[] result = new Slaw[ks.size()];
        int i = 0;
        for (String k : ks) result[i++] = Slaw.string(k);
        return result;
    }

    String name() { return name; }

    long oldestIndex() {
        synchronized (proteins) {
            return proteins.size() > 0 ? 0 : -1;
        }
    }

    long newestIndex() {
        synchronized (proteins) {
            return proteins.size() - 1;
        }
    }

    PoolProtein nth(long index) {
        if (index >= 0) {
            synchronized (proteins) {
                if (index < proteins.size()) return proteins.get((int)index);
            }
        }
        return null;
    }

    PoolProtein nth(long idx, boolean d, boolean i, long s, long l) {
        final PoolProtein p = nth(idx);
        if (p == null) return null;
        byte[] data = null;
        if (s >= 0 && l != 0) {
            l = l < 0 ? p.dataLength() : Math.min(l, p.dataLength());
            data = new byte[(int)l];
            for (long k = 0; k < l; ++k) {
                data[(int)k] = p.datum((int)(s + k));
            }
        }
        return new PoolProtein(Slaw.protein(d ? p.descrips() : null,
                                            i ? p.ingests() : null,
                                            data),
                               p.index(),
                               p.timestamp(),
                               null);
    }

    PoolProtein next(long index, double timeout) {
        PoolProtein p = nth(index);
        if (p == null && timeout != 0) {
            if (timeout < 0) return waitNext(index);
            synchronized (proteins) {
                long nanos = (long)(timeout * 1e9);
                while (nanos > 0 && p == null) {
                    final long start = System.currentTimeMillis();
                    try {
                        proteins.wait(nanos/THOUSAND,
                                      (int)(nanos % THOUSAND));
                    } catch (InterruptedException e) {
                        return null;
                    }
                    p = nth(index);
                    nanos -= (System.currentTimeMillis() - start) * THOUSAND;
                }
            }
        }
        return p;
    }

    PoolProtein waitNext(long index) {
        synchronized (proteins) {
            PoolProtein p = nth(index);
            while (p == null) {
                try {
                    proteins.wait();
                    p = nth(index);
                } catch (InterruptedException e) {
                    return null;
                }
            }
            return p;
        }
    }

    PoolProtein find(long index, Slaw[] descs, boolean fwd) {
        final int idx = (int)index;
        return fwd ? findFwd(idx, descs) : findBack(idx, descs);
    }

    PoolProtein deposit(Protein p) {
        return deposit(p, ((double)System.currentTimeMillis()) / 10e3);
    }

    PoolProtein deposit(Protein p, double stamp) {
        synchronized (proteins) {
            final PoolProtein pp =
                new PoolProtein(p, proteins.size(), stamp, null);
            proteins.add(pp);
            proteins.notifyAll();
            return pp;
        }
    }

    private MemPool(String name) {
        this.name = name;
    }

    private PoolProtein findBack(int index, Slaw[] descrip) {
        synchronized (proteins) {
            if (index >= proteins.size()) index = proteins.size() - 1;
            for (int i = index; i > -1; --i) {
                final PoolProtein p = proteins.get(i);
                if (p.matches(descrip)) return p;
            }
        }
        return null;
    }

    private PoolProtein findFwd(int index, Slaw[] descrip) {
        synchronized (proteins) {
            if (index < 0) index = 0;
            final int last = proteins.size();
            for (int i = index; i < last; ++i) {
                final PoolProtein p = proteins.get(i);
                if (p.matches(descrip)) return p;
            }
        }
        return null;
    }

    private final String name;
    private final ArrayList<PoolProtein> proteins =
        new ArrayList<PoolProtein>();

    private static ConcurrentHashMap<String, MemPool> pools =
        new ConcurrentHashMap<String, MemPool>();

    private static final int THOUSAND = 1000000;
}
