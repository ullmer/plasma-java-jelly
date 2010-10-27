// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

/**
 * Utilities for unit tests for class HoseGang.
 *
 * @author jao
 */
public class HoseGangTests {

    public static void emptyTest(PoolAddress p1, PoolAddress p2)
        throws Exception {
        final HoseGang g = HoseGang.newGang();
        assertNotNull(g);
        testEmpty(g);
        g.disband();
        assertTrue(g.add(Pool.participate(p1, null)));
        assertEquals(1, g.count());
        testEmpty(g);
        assertTrue(g.add(Pool.participate(p2, null)));
        assertEquals(2, g.count());
        testEmpty(g);
        final Hose h = Pool.participate(p2);
        assertFalse(g.add(h));
        assertFalse(h.isConnected());
        assertEquals(2, g.count());
        testEmpty(g);
        assertTrue(g.remove(p1.toString()));
        assertFalse(g.remove(p1.toString()));
        assertFalse(g.remove("foo"));
        assertEquals(1, g.count());
        g.disband();
        assertEquals(0, g.count());
    }

    public static void seqTest(PoolAddress pa, PoolAddress pb, PoolAddress pc)
        throws Exception {
        final HoseGang g = add(HoseGang.newGang(), pa, pb, pc);
        assertEquals(3, g.count());
        testEmpty(g);
        final Protein[] pas = deposit(pa, 5);
        for (int i = 0; i < pas.length; ++i) {
            assertEquals(i + "th step", pas[i], g.next());
        }
        testEmpty(g);
        final Protein[] pbs = deposit(pb, 5);
        final Protein[] pcs = deposit(pc, 5);
        int b = 0, c = 0;
        for (int i = 0; i < pbs.length + pcs.length; ++i) {
            final Protein p = g.next();
            if (b < pbs.length && pbs[b].equals(p)) ++b;
            else if (c < pcs.length && pcs[c].equals(p)) ++c;
            else fail("Unexpected protein: " + p
                      + "\nwith b=" + b + ", c=" + c);
        }
        assertEquals(pbs.length, b);
        assertEquals(pcs.length, c);
        g.disband();
    }

    public static void waitTest(PoolAddress pa,
                                PoolAddress pb,
                                PoolAddress pc) throws Exception {
        // final HoseGang g = add(HoseGang.newGang(), pa, pb, pc);
        // assertEquals(3, g.count());
        // testEmpty(g);
        // final Protein[] pas = deposit(pa, 4);
        // final Protein[] pbs = deposit(pb, 7);
        // final Protein[] pcs = deposit(pc, 3);
        // int a =0, b = 0, c = 0;
        // for (int i = 0; i < pas.length + pbs.length + pcs.length; ++i) {
        //     final Protein p = g.awaitNext(1, TimeUnit.MILLISECONDS);
        //     if (b < pbs.length && pbs[b].equals(p)) ++b;
        //     else if (c < pcs.length && pcs[c].equals(p)) ++c;
        //     else if (a < pas.length && pas[c].equals(p)) ++a;
        //     else fail("Unexpected protein: " + p);
        // }
        // assertEquals(pbs.length, b);
        // assertEquals(pcs.length, c);
        // assertEquals(pas.length, a);
        // g.disband();
    }

    static void testEmpty(HoseGang g) throws Exception {
        try {
            g.awaitNext(1, TimeUnit.MILLISECONDS);
            fail("Timed out returned");
        } catch (TimeoutException e) {
            // good
        }
    }

    static HoseGang add(HoseGang g, PoolAddress... as) throws PoolException {
        for (PoolAddress a : as) {
            try { Pool.dispose(a); } catch (PoolException e) {}
            Pool.create(a, null);
            g.add(a);
        }
        return g;
    }

    static Protein[] deposit(PoolAddress pool, int no) throws PoolException {
        final Hose h = Pool.participate(pool);
        final Protein[] ps = HoseTestBase.deposit(h, no);
        h.withdraw();
        return ps;
    }
}
