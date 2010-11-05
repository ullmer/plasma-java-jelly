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
            assertEquals(i + "th step", pas[i], g.awaitNext());
        }
        testEmpty(g);
        final Protein[] pbs = deposit(pb, 5);
        final Protein[] pcs = deposit(pc, 5);
        final String bn = pb.toString();
        final String cn = pc.toString();
        int b = 0, c = 0;
        for (int i = 0; i < pbs.length + pcs.length; ++i) {
            final Protein p = g.awaitNext();
            if (bn.equals(p.source())) {
                assertTrue(b < pbs.length);
                assertEquals(pbs[b++], p);
            } else if (cn.equals(p.source())) {
                assertTrue(c < pcs.length);
                assertEquals(pcs[c++], p);
            } else
                fail("Unexpected protein: " + p
                     + "\nwith b=" + b + ", c=" + c);
        }
        assertEquals(pbs.length, b);
        assertEquals(pcs.length, c);
        g.disband();
    }

    public static void waitTest(PoolAddress pa,
                                PoolAddress pb,
                                PoolAddress pc) throws Exception {
        final HoseGang g = add(HoseGang.newGang(), pa, pb, pc);
        assertEquals(3, g.count());
        testEmpty(g);
        final Protein[] pas = deposit(pa, 4);
        final Protein[] pbs = deposit(pb, 7);
        final Protein[] pcs = deposit(pc, 3);
        final String an = pa.toString();
        final String bn = pb.toString();
        final String cn = pc.toString();
        int a = 0, b = 0, c = 0;
        for (int i = 0; i < pas.length + pbs.length + pcs.length; ++i) {
            final Protein p = g.awaitNext(1000, TimeUnit.MILLISECONDS);
            if (b < pbs.length && pbs[b].equals(p)) {
                assertEquals(bn, p.source());
                ++b;
            } else if (c < pcs.length && pcs[c].equals(p)) {
                assertEquals(cn, p.source());
                ++c;
            } else if (a < pas.length && pas[a].equals(p)) {
                assertEquals(an, p.source());
                ++a;
            } else
                fail("Unexpected protein: " + p
                     + "\nwith a=" + a + ", b=" + b + ", c=" + c);
        }
        assertEquals(pbs.length, b);
        assertEquals(pcs.length, c);
        assertEquals(pas.length, a);
        g.disband();
    }

    public static void wakeUpTest(PoolAddress... pa) throws Exception {
        class Waiter implements Runnable {
            public void run() {
                awaken = false;
                protein = null;
                try {
                    while (true) { protein = gang.awaitNext(); }
                } catch (InterruptedException e) {
                    awaken = true;
                } catch (Exception e) {
                    fail(e.getMessage());
                }
            }
            Waiter(HoseGang g) { gang = g; }
            final HoseGang gang;
            boolean awaken = false;
            Protein protein = null;
        }
        final HoseGang g = HoseGangTests.add(HoseGang.newGang(), pa);
        final Waiter w = new Waiter(g);
        Thread th = new Thread(w);
        th.start();
        Thread.yield();
        g.wakeUp();
        th.join();
        assertTrue(w.awaken);
        assertNull(w.protein);
        th = new Thread(w);
        th.start();
        final Protein p = deposit(pa[0], 1)[0];
        Thread.yield();
        g.wakeUp();
        th.join();
        assertTrue(w.awaken);
        if (w.protein == null) assertEquals(p, g.awaitNext());
        else assertEquals(p, w.protein);
        g.disband();
    }

    public static void asyncTest(PoolAddress pa, PoolAddress pb)
        throws Exception {
        class Depositor implements Runnable {
            Depositor(PoolAddress a, int no) { address = a; number = no; }
            public void run() {
                try {
                    /* deposited = */ deposit(address, number);
                } catch (PoolException e) {
                    fail(e.toString());
                }
                // System.out.println("Deposited to " + address);
            }
            // Protein[] deposited = null;
            final PoolAddress address;
            final int number;
        }

        class Reader implements Runnable {
            Reader(HoseGang g, int no) { gang = g;  number = no; }
            public void run() {
                // System.out.println("Here we go");
                while (number > 0) {
                    try {
                        gang.awaitNext();
                        --number;
                        // System.out.println("Read, " + number + " left");
                    } catch (Exception e) {
                        fail(e.toString());
                    }
                }
            }
            final HoseGang gang;
            int number;
        }
        final HoseGang gang = add(HoseGang.newGang(), pa, pb);
        final Depositor da = new Depositor(pa, 3), db = new Depositor(pb, 2);
        final Thread dta = new Thread(da), dtb = new Thread(db);
        final Reader r = new Reader(gang, da.number + db.number);
        final Thread rt = new Thread(r);
        rt.start();
        dta.start();
        dtb.start();
        rt.join();
        assertEquals(0, r.number);
        gang.disband();
    }

    static void testEmpty(HoseGang g) throws Exception {
        try {
            g.awaitNext(1, TimeUnit.MILLISECONDS);
            fail("Timed out returned");
        } catch (TimeoutException e) {
            // good
        }
    }

    public static HoseGang add(HoseGang g, PoolAddress... as)
        throws PoolException {
        for (PoolAddress a : as) {
            try { Pool.dispose(a); } catch (PoolException e) {}
            Pool.create(a, null);
            g.add(a);
        }
        return g;
    }

    public static Protein[] deposit(PoolAddress pool, int no)
        throws PoolException {
        final Hose h = Pool.participate(pool);
        final Protein[] ps = HoseTestBase.deposit(h, no);
        h.withdraw();
        return ps;
    }
}
