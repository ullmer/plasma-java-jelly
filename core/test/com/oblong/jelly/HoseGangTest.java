// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Unit Test for class HoseGang.
 *
 * @author jao
 */
public class HoseGangTest {

    @Test public void empty() throws Exception {
        final HoseGang g = HoseGang.newGang();
        assertNotNull(g);
        testEmpty(g);
        g.disband();
        assertTrue(g.add(Pool.participate("mem://localhost/p1", null)));
        assertEquals(1, g.count());
        testEmpty(g);
        assertTrue(g.add(Pool.participate("mem://localhost/p2", null)));
        assertEquals(2, g.count());
        testEmpty(g);
        final Hose h = Pool.participate("mem://localhost/p2");
        assertFalse(g.add(h));
        assertFalse(h.isConnected());
        assertEquals(2, g.count());
        testEmpty(g);
        assertTrue(g.remove("mem://localhost/p2"));
        assertFalse(g.remove("mem://localhost/p2"));
        assertFalse(g.remove("foo"));
        assertEquals(1, g.count());
        g.disband();
        assertEquals(0, g.count());
    }

    @Test public void mem() throws Exception {
        final HoseGang g = memGang("a", "b", "c");
        assertEquals(3, g.count());
        testEmpty(g);
        final Protein[] pas = deposit("mem://localhost/a", 5);
        for (int i = 0; i < pas.length; ++i) {
            assertEquals(pas[i], g.next());
        }
        testEmpty(g);
        final Protein[] pbs = deposit("mem://localhost/b", 5);
        final Protein[] pcs = deposit("mem://localhost/c", 5);
        int b = 0, c = 0;
        for (int i = 0; i < pbs.length + pcs.length; ++i) {
            final Protein p = g.next();
            if (b < pbs.length && pbs[b].equals(p)) ++b;
            else if (c < pcs.length && pcs[c].equals(p)) ++c;
            else fail("Unexpected protein: " + p);
        }
        assertEquals(pbs.length, b);
        assertEquals(pcs.length, c);
        g.disband();
    }

    static void testEmpty(HoseGang g) throws Exception {
        try {
            g.awaitNext(1, TimeUnit.MILLISECONDS);
            fail("Timed out returned");
        } catch (TimeoutException e) {
            // good
        }
    }

    static HoseGang memGang(String... names) throws PoolException {
        final HoseGang g = HoseGang.newGang();
        for (String n : names) {
            Pool.create("mem://localhost/" + n, null);
            g.add("mem://localhost/" + n);
        }
        return g;
    }

    static Protein[] deposit(String pool, int no) throws PoolException {
        final Hose h = Pool.participate(pool);
        final Protein[] ps = HoseTestBase.deposit(h, no);
        h.withdraw();
        return ps;
    }
}
