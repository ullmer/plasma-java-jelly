// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.mem;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.pool.PoolProtein;

/**
 *  Unit Test for class MemPool
 *
 *
 * Created: Thu Jul  1 23:53:17 2010
 *
 * @author jao
 */
public class MemPoolTest {

    @Before public void cleanup() {
        for (Slaw n : MemPool.slawNames())
            assertTrue(MemPool.dispose(n.emitString()));
    }

    @Test public void register() {
        final String[] names = {"p0", "p1", "p2"};
        assertEquals(0, MemPool.slawNames().length);
        for (String n : names) {
            assertFalse(MemPool.exists(n));
            assertNotNull(MemPool.create(n));
            assertTrue(MemPool.exists(n));
        }
        assertEquals(names.length, MemPool.slawNames().length);
        for (String n : names) {
            assertNull(MemPool.create(n));
            final MemPool pool = MemPool.get(n);
            assertNotNull(pool);
            assertEquals(n, pool.name());
        }
        for (Slaw n : MemPool.slawNames()) {
            assertTrue(MemPool.dispose(n.emitString()));
            assertFalse(MemPool.exists(n.emitString()));
        }
        assertEquals(0, MemPool.slawNames().length);
    }

    @Test public void deposit() {
        final Protein p = Slaw.protein(null, null, null);
        final MemPool pool = MemPool.create("pool");
        assertNotNull(pool);
        final int PN = 5;
        final PoolProtein[] deps = new PoolProtein[PN];
        for (int i = 0, idx = 0; i < PN; ++i, ++idx) {
            deps[i] = pool.deposit(p, i);
            assertEquals(p, deps[i].bareProtein());
            assertEquals(idx, deps[i].index());
            assertEquals(i, (int)deps[i].timestamp());
        }
        assertEquals(0, pool.oldestIndex());
        assertEquals(PN - 1, pool.newestIndex());
        for (int i = 0; i < PN; ++i) {
            assertEquals(deps[i], pool.next(i, 0));
            assertEquals(deps[i], pool.next(i, 2));
            assertEquals(deps[i], pool.nth(i));
        }
    }

    @Test public void find() {
        final MemPool pool = MemPool.create("pool");
        assertNotNull(pool);
        final int PN = 5;
        final PoolProtein[] deps = new PoolProtein[PN];
        final Slaw[] ds = new Slaw[PN];
        for (int i = 0; i < PN; ++i) {
            ds[i] = Slaw.int32(i);
            final Slaw d = Slaw.list(Slaw.string("foo"), ds[i]);
            deps[i] = pool.deposit(Slaw.protein(d, null, null));
            assertNotNull(deps[i]);
        }
        for (int i = 0; i < PN; ++i) {
            assertNull(i + "th", pool.find(i + 1, ds[i], true));
            assertNull(i + "th", pool.find(i - 1, ds[i], false));
            for (int j = 0; j < i; ++j)
                assertEquals(deps[i], pool.find(j, ds[i], true));
            for (int j = PN - 1; j >= i; --j)
                assertEquals(i + "th, " + j,
                             deps[i], pool.find(j, ds[i], false));
        }
    }
}
