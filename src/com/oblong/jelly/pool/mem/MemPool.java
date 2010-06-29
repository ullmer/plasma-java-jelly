// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.mem;

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

    public static MemPool get(String name) {
        return null;
    }

    public static boolean dispose(String emitString) {
        // TODO Auto-generated method stub
        return false;
    }

    public static MemPool getNew(String name) {
        return null;
    }

    public static boolean exists(String emitString) {
        // TODO Auto-generated method stub
        return false;
    }

    public static Slaw[] names() {
        // TODO Auto-generated method stub
        return null;
    }

    public String name() { return name; }

    public long oldestIndex() { return 0; }
    public long newestIndex() { return 0; }

    public PoolProtein nth(long index) {
        return null;
    }

    public PoolProtein next(long index, double timeout) {
        // TODO Auto-generated method stub
        return null;
    }

    public PoolProtein find(long index, Slaw descrip) {
        return null;
    }

    public void deposit(PoolProtein p) {}
    public PoolProtein deposit(Protein p) {
        return null;
    }

    private MemPool(String name) {
        this.name = name;
    }

    private final String name;

}
