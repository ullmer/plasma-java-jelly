// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.mem;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.pool.PoolProtein;
import com.oblong.jelly.PoolOptions;


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
            assertNotNull(MemPool.create(n, PoolOptions.MEDIUM));
            assertTrue(MemPool.exists(n));
        }
        assertEquals(names.length, MemPool.slawNames().length);
        for (String n : names) {
            assertNull(MemPool.create(n, PoolOptions.MEDIUM));
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
        final MemPool pool = MemPool.create("pool", PoolOptions.MEDIUM);
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
        final MemPool pool = MemPool.create("pool", PoolOptions.MEDIUM);
        assertNotNull(pool);
        final int PN = 5;
        final PoolProtein[] deps = new PoolProtein[PN];
        final Slaw[] ds = new Slaw[PN];
        final Slaw e0 = Slaw.string("foo");
        final Slaw e1 =  Slaw.int8(3);
        for (int i = 0; i < PN; ++i) {
            ds[i] = Slaw.int32(i);
            final Slaw d = Slaw.list(e0, e1, ds[i]);
            deps[i] = pool.deposit(Slaw.protein(d, null, null));
            assertNotNull(deps[i]);
        }
        for (int i = 0; i < PN; ++i) {
            final Slaw[][] matches = {
                {ds[i]}, {e1, ds[i]}, {e0, ds[i]}, {e0, e1, ds[i]}
            };
            for (Slaw[] match : matches) {
                assertNull(i + "th ", pool.find(i + 1, match, true));
                assertNull(i + "th", pool.find(i - 1, match, false));
                for (int j = 0; j < i; ++j)
                    assertEquals(deps[i], pool.find(j, match, true));
                for (int j = PN - 1; j >= i; --j)
                    assertEquals(i + "th, " + j,
                                 deps[i], pool.find(j, match, false));
            }
            final Slaw[][] sure = {{e0}, {e1}, {e0, e1}};
            for (Slaw[] m : sure) {
                assertEquals(deps[i], pool.find(i, m, true));
                assertEquals(deps[i], pool.find(i, m, false));
            }
        }
    }
}
