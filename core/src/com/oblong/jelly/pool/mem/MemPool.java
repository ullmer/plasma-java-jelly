
/* (c)  oblong industries */

package com.oblong.jelly.pool.mem;

import com.oblong.jelly.PoolOptions;
import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;
import net.jcip.annotations.ThreadSafe;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ThreadSafe
final public class MemPool {

	private long sequentialProteinIndex = 0;

	static boolean exists(String name) {
        return pools.containsKey(name);
    }

    public static MemPool create(String name) {
        return create(name, PoolOptions.MEDIUM);
    }

    public static MemPool create(String name, PoolOptions opts) {
        if (exists(name)) return null;
        final MemPool p = new MemPool(name, opts.poolSize());
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

	MemPoolProtein nth(long index) {
        if (index >= 0) {
            synchronized (proteins) {
                if (index < proteins.size()) return proteins.get((int)index);
            }
        }
        return null;
    }

	MemPoolProtein nth(long idx, boolean d, boolean i, long s, long l) {
        final MemPoolProtein p = nth(idx);
        if (p == null) return null;
        byte[] data = null;
        if (s >= 0 && l != 0) {
            l = l < 0 ? p.dataLength() : Math.min(l, p.dataLength());
            data = new byte[(int)l];
            for (long k = 0; k < l; ++k) {
                data[(int)k] = p.datum((int)(s + k));
            }
        }
        return new MemPoolProtein(Slaw.protein(d ? p.descrips() : null,
                                            i ? p.ingests() : null,
                                            data),
                               p.index(),
                               p.timestamp(),
                               null,
		                       p.getSequentialProteinIndex());
    }

	MemPoolProtein next(long index, double timeout) {
		MemPoolProtein p = nth(index);
        if (p == null && timeout != 0) {
            if (timeout < 0) return waitNext(index);
            synchronized (proteins) {
                long nanos = (long)(timeout * 1e9);
                while (nanos > 0 && p == null) {
                    final long start = System.currentTimeMillis();
                    try {
                        proteins.wait(nanos / NANOS_PER_MILLI,
                                      (int)(nanos % NANOS_PER_MILLI));
                    } catch (InterruptedException e) {
                        return null;
                    }
                    p = nth(index);
                    nanos -= ((System.currentTimeMillis() - start)
                              * NANOS_PER_MILLI);
                }
            }
        }
        return p;
    }

	MemPoolProtein waitNext(long index) {
        synchronized (proteins) {
	        MemPoolProtein p = nth(index);
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

	MemPoolProtein find(long index, Slaw[] descs, boolean fwd) {
        final int idx = (int)index;
        return fwd ? findFwd(idx, descs) : findBack(idx, descs);
    }

	MemPoolProtein deposit(Protein p) {
        return deposit(p, ((double)System.currentTimeMillis()) / 10e3);
    }

	MemPoolProtein deposit(Protein p, double stamp) {
        synchronized (proteins) {
            final MemPoolProtein pp =
                new MemPoolProtein(p, proteins.size(), stamp, null, nextSequentialProteinIndex());
            //  If we've hit the size limit, start dropping from the front
            if (proteins.size() >= max_size)
                proteins.remove(0);
            proteins.add(pp);
            proteins.notifyAll();
            return pp;
        }
    }

	private long nextSequentialProteinIndex() {
		return sequentialProteinIndex ++;
	}

	private MemPool(String name) {
        this (name, 1000 * 1000 /* default max pool size.  Arbitrary.*/);
    }

    private MemPool(String name, long size) {
        this.name = name;
        this.max_size = size;
    }

    private MemPoolProtein findBack(int index, Slaw[] descrip) {
        synchronized (proteins) {
            if (index >= proteins.size()) index = proteins.size() - 1;
            for (int i = index; i > -1; --i) {
                final MemPoolProtein p = proteins.get(i);
                if (p.matches(descrip)) return p;
            }
        }
        return null;
    }

    private MemPoolProtein findFwd(int index, Slaw[] descrip) {
        synchronized (proteins) {
            if (index < 0) index = 0;
            final int last = proteins.size();
            for (int i = index; i < last; ++i) {
                final MemPoolProtein p = proteins.get(i);
                if (p.matches(descrip)) return p;
            }
        }
        return null;
    }

    private final String name;
    private final ArrayList<MemPoolProtein> proteins =
        new ArrayList<MemPoolProtein>();
    private final long max_size;

    private static ConcurrentHashMap<String, MemPool> pools =
        new ConcurrentHashMap<String, MemPool>();

    // nanoseconds per millisecond (1e6)
    private static final int NANOS_PER_MILLI = 1000000;
}
