// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.mem;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.jcip.annotations.ThreadSafe;

import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.pool.impl.PoolProtein;

/**
 *
 * Created: Tue Jun 29 23:18:48 2010
 *
 * @author jao
 */
@ThreadSafe
final class MemPool {

    public static boolean exists(String name) {
        return pools.containsKey(name);
    }

    public static MemPool create(String name) {
        if (exists(name)) return null;
        final MemPool p = new MemPool(name);
        final MemPool old = pools.putIfAbsent(name, p);
        return old == null ? p : old;
    }

    public static MemPool get(String name) {
        return pools.get(name);
    }

    public static boolean dispose(String name) {
        return pools.remove(name) != null;
    }

    public static Slaw[] names() {
        final Set<String> ks = pools.keySet();
        final Slaw[] result = new Slaw[ks.size()];
        int i = 0;
        for (String k : ks) result[i++] = Slaw.string(k);
        return result;
    }

    public String name() { return name; }

    public long oldestIndex() {
        synchronized (proteins) {
            return proteins.size() > 0 ? 0 : -1;
        }
    }

    public long newestIndex() {
        synchronized (proteins) {
            return proteins.size() - 1;
        }
    }

    public PoolProtein nth(long index) {
        if (index >= 0) {
            synchronized (proteins) {
                if (index < proteins.size()) return proteins.get((int)index);
            }
        }
        return null;
    }

    public PoolProtein next(long index, double timeout) {
        if (timeout == 0) return nth(index + 1);
        if (timeout < 0) return waitNext(index);
        synchronized (proteins) {
            final long idx = index + 1;
            PoolProtein p = nth(idx);
            if (p != null) return p;
            long nanos = (long)(timeout * 10e9);
            while (nanos > 0 && p == null) {
                final long start = System.currentTimeMillis();
                try {
                    proteins.wait(nanos/THOUSAND, (int)(nanos % THOUSAND));
                } catch (InterruptedException e) {
                    return null;
                }
                p = nth(idx);
                nanos -= (System.currentTimeMillis() - start) * THOUSAND;
            }
            return p;
        }
    }

    public PoolProtein waitNext(long index) {
        synchronized (proteins) {
            PoolProtein p = nth(index + 1);
            while (p == null) {
                try {
                    proteins.wait();
                    p = nth(index + 1);
                } catch (InterruptedException e) {
                    return null;
                }
            }
            return p;
        }
    }

    public PoolProtein find(long index, Slaw descrip, boolean fwd) {
        final int idx = (int)index;
        return fwd ? findFwd(idx, descrip) : findBack(idx, descrip);
    }

    public PoolProtein deposit(Protein p) {
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

    private PoolProtein findBack(int index, Slaw descrip) {
        synchronized (proteins) {
            if (index > proteins.size()) index = proteins.size();
            for (int i = index - 1; i > -1; --i) {
                final PoolProtein p = proteins.get(i);
                if (p.descrips().contains(descrip)) return p;
            }
        }
        return null;
    }

    private PoolProtein findFwd(int index, Slaw descrip) {
        synchronized (proteins) {
            if (index < 0) index = -1;
            final int last = proteins.size();
            for (int i = index + 1; i < last; ++i) {
                final PoolProtein p = proteins.get(i);
                if (p.descrips().contains(descrip)) return p;
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
