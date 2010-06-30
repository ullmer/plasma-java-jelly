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
        return pools.contains(name);
    }

    public static MemPool create(String name) {
        if (exists(name)) return null;
        return pools.putIfAbsent(name, new MemPool(name));
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
        synchronized (proteins) {
            if (index < proteins.size()) return proteins.get((int)index);
        }
        return null;
    }

    public PoolProtein next(long index, double timeout) {
        PoolProtein p = nth(index);
        if (p != null || timeout == 0) return p;
        long nanos = Math.max((long)(timeout * 10e9), 0);
        while (p == null) {
            try {
                final long start = System.currentTimeMillis();
                synchronized (proteins) {
                    proteins.wait(nanos/THOUSAND, (int)(nanos % THOUSAND));
                }
                if (nanos > 0) {
                    nanos -= (System.currentTimeMillis() - start) * THOUSAND;
                    if (nanos <= 0) return nth(index);
                }
                p = nth(index);
            } catch (InterruptedException e) {
                return null;
            }
        }
        return p;
    }

    public PoolProtein find(long index, Slaw descrip, boolean fwd) {
        synchronized (proteins) {
            final int last = fwd ? proteins.size() - 1 : 0;
            final int first = (int)(
                    fwd ? Math.min(index + 1, last) : Math.max(index - 1, 0));
            final int step = fwd ? 1 : -1;
            for (int i = first; i != last; i += step) {
                final PoolProtein p = proteins.get(i);
                if (p.descrips().contains(descrip)) return p;
            }
        }
        return null;
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

    private final String name;
    private final ArrayList<PoolProtein> proteins = 
        new ArrayList<PoolProtein>();
    
    private static ConcurrentHashMap<String, MemPool> pools =
        new ConcurrentHashMap<String, MemPool>();

    private static final int THOUSAND = 1000000;
}
