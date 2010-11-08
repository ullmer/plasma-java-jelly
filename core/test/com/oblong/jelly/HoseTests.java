// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import static com.oblong.jelly.Slaw.*;

/**
 *
 * Created: Tue Jul  6 14:46:05 2010
 *
 * @author jao
 */
public class HoseTests {

    public static class Tests<Dummy> {

        @Before public void maybeDisable() {
            assumeTrue(tests != null);
        }

        @Test public void hoseName() throws Exception { tests.hoseName(); }

        @Test public void deposit() throws Exception { tests.deposit(); }

        @Test public void next() throws Exception { tests.next(); }
        @Test public void await() throws Exception { tests.await(); }
        @Test public void timeout() throws Exception { tests.awaitTimeout(); }
        @Test public void awaitNext() throws Exception { tests.awaitNext(); }

        @Test public void nth() throws Exception { tests.nth(); }
        @Test public void previous() throws Exception { tests.previous(); }
        @Test public void current() throws Exception { tests.current(); }
        @Test public void range() throws Exception { tests.range(); }

        @Test public void matchingAll() throws Exception {
            tests.matchingAll();
        }
        @Test public void matchingOne() throws Exception {
            tests.matchingOne();
        }
        @Test public void matchSome() throws Exception {
            tests.matchingSome();
        }

        @Test public void poll() throws Exception { tests.poll(); }
        @Test public void cancelPoll() throws Exception {
            tests.cancelledPoll();
        }
        @Test public void matchPollAll() throws Exception {
            tests.matchingPollAll();
        }
        @Test public void matchPollOne() throws Exception {
            tests.matchingPollOne();
        }
        @Test public void matchPollSome() throws Exception {
            tests.matchingPollSome();
        }

        @AfterClass public static void cleanUpTests() {
            if (tests != null) {
                tests.cleanUp();
                tests = null;
            }
        }

        protected static void initTests(PoolServerAddress addr)
            throws Exception {
            if (addr != null) tests = new HoseTests(addr);
        }

        private static HoseTests tests;
    }

    public HoseTests() {
        address = null;
        defHose = null;
        depProteins = null;
    }

    public HoseTests(PoolServerAddress addr) throws PoolException {
        address = addr;
        final PoolAddress pa = new PoolAddress(address, "default-pool");
        try { Pool.dispose(pa); } catch (NoSuchPoolException e) {}
        defHose = Pool.participate(pa, null);
        depProteins = deposit(defHose, -1);
    }

    public void cleanUp() {
        try { defHose.withdraw(); } catch (Exception e) {}
        try {
            Pool.dispose(defHose.poolAddress());
        } catch (PoolException e) {
        }
    }

    public void hoseName() throws PoolException {
        final PoolAddress a = new PoolAddress(address, "eipool");
        final Hose h = Pool.participate(a, PoolOptions.SMALL);
        assertEquals(a, h.poolAddress());
        assertEquals(a.toString(), h.name());
        final String newName = "this name is not the same";
        h.setName(newName);
        assertEquals(newName, h.name());
        h.withdraw();
        assertEquals(newName, h.name());
    }

    public void deposit() throws PoolException {
        assertEquals(depProteins[0].index(), defHose.oldestIndex());
        assertEquals(depProteins[TLEN - 1].index(), defHose.newestIndex());
    }

    public void nth() throws PoolException {
        for (int i = 0; i < TLEN; ++i) {
            checkProtein(defHose.nth(i), i);
            final ProteinMetadata md = defHose.metadata(i);
            assertEquals(depProteins[i].index(), md.index());
            assertEquals(depProteins[i].timestamp(), md.timestamp(), 0.00001);
            assertEquals(3, md.descripsNumber());
            assertEquals(4, md.ingestsNumber());
            assertEquals(depProteins[i].dataLength(), md.dataSize());
        }
    }

    public void next() throws PoolException {
        defHose.rewind();
        for (int i = 0; i < TLEN; ++i) {
            checkProtein(defHose.next(), i);
            assertTrue(i + "th", depProteins[i].index() < defHose.index());
        }
        assertEquals(defHose.newestIndex() + 1, defHose.index());
    }

    public void range() throws PoolException {
        defHose.runOut();
        for (int i = 1; i <= TLEN; ++i) {
            final List<Protein> ps = defHose.range(0, i);
            assertEquals(i, ps.size());
            for (int k = 0; k < i; ++k) checkProtein(ps.get(k), k);
        }
    }

    public void await() throws PoolException, TimeoutException {
        defHose.seekTo(defHose.oldestIndex());
        for (int i = 0; i < TLEN; ++i) {
            checkProtein(defHose.awaitNext(1, TimeUnit.SECONDS), i);
            assertTrue(i + "th", depProteins[i].index() < defHose.index());
        }

        assertEquals(defHose.newestIndex() + 1, defHose.index());
    }

    public void awaitTimeout() throws PoolException {
        final Hose h = defHose.dup();
        h.runOut();
        final long lapse = 10;
        final long t = System.currentTimeMillis();
        try {
          h.awaitNext(lapse, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            // we're good if the timeout expired
            assertTrue(System.currentTimeMillis() >= t + lapse);
            return;
        } finally {
            h.withdraw();
        }
        fail("awaitNext() didn't timeout");
    }

    public void awaitNext() throws PoolException {
        defHose.seekTo(defHose.oldestIndex());
        for (int i = 0; i < TLEN; ++i) {
            checkProtein(defHose.awaitNext(), i);
            assertTrue(i + "th", depProteins[i].index() < defHose.index());
        }
        assertEquals(defHose.newestIndex() + 1, defHose.index());
    }

    public void previous() throws PoolException {
        defHose.runOut();
        assertEquals(defHose.newestIndex() + 1, defHose.index());
        for (int i = TLEN - 1; i > 0; --i) {
            checkProtein(defHose.previous(), i);
            assertEquals(i + "th", depProteins[i].index(), defHose.index());
        }
    }

    public void current() throws PoolException {
        for (int i = 0; i < TLEN; i++) {
            defHose.seekTo(depProteins[i].index());
            checkProtein(defHose.current(), i);
        }
    }

    private interface Matcher {
        Slaw[] get(Slaw descrips) throws PoolException;
    }

    private static final Matcher allMatcher = new Matcher() {
            public Slaw[] get(Slaw d) { return d.emitArray(); }
        };

    private static final Matcher oneMatcher = new Matcher() {
            public Slaw[] get(Slaw d) {
                final Slaw[] m = { d.nth(0) };
                return m;
            }
        };

    private final Matcher someMatcher = new Matcher () {
            public Slaw[] get(Slaw d) throws PoolException {
                final Slaw m = d.nth(0);
                final Slaw m2 = d.nth(1 + (i++) % 2);
                try {
                    final Protein p = defHose.next(m2, m);
                    fail("Found " + p);
                } catch (NoSuchProteinException e) {
                    // expected
                }
                final Slaw[] ms = {m, m2};
                return ms;
            }
            int i = 0;
        };

    private void testMatching(Matcher matcher) throws PoolException {
        defHose.rewind();
        for (int i = 0; i < TLEN; ++i) {
            final Slaw[] m = matcher.get(depProteins[i].descrips());
            assertEquals(i + "th", depProteins[i], defHose.next(m));
            assertTrue(i + "th", depProteins[i].index() < defHose.index());

            defHose.runOut();
            assertEquals(i + "th", depProteins[i], defHose.previous(m));
            assertEquals(i + "th", depProteins[i].index(), defHose.index());

            defHose.rewind();
            assertEquals(i + "th", depProteins[i], defHose.next(m));
            assertTrue(i + "th", depProteins[i].index() < defHose.index());
        }
        assertEquals(defHose.newestIndex() + 1, defHose.index());
    }

    public void matchingAll() throws PoolException {
        testMatching(allMatcher);
    }

    public void matchingOne() throws PoolException {
        testMatching(oneMatcher);
    }

    public void matchingSome() throws PoolException {
        testMatching(someMatcher);
    }

    public void poll() throws PoolException {
        defHose.rewind();
        for (int i = 0; i < TLEN; ++i) {
            assertTrue(defHose.poll());
            assertTrue(defHose.poll());
            assertEquals(depProteins[i], defHose.peek());
            assertEquals(depProteins[i], defHose.next());
            assertNull(defHose.peek());
        }
        assertFalse(defHose.poll());
        assertNull(defHose.peek());
    }

    public void cancelledPoll() throws PoolException {
        defHose.rewind();
        for (int i = 0; i < TLEN; ++i) {
            assertTrue(defHose.poll());
            assertEquals(depProteins[i], defHose.peek());
            defHose.oldestIndex();
            assertEquals(depProteins[i], defHose.next());
            assertNull(defHose.peek());
            defHose.oldestIndex();
        }
        assertFalse(defHose.poll());
        assertNull(defHose.peek());
    }

    private void testMatchingPoll(final Matcher matcher)
        throws PoolException {
        testMatching(new Matcher() {
                public Slaw[] get(Slaw d) throws PoolException {
                    final Slaw[] m = matcher.get(d);
                    defHose.poll(m);
                    return m;
                }
            });
    }

    public void matchingPollAll() throws PoolException {
        testMatchingPoll(allMatcher);
    }

    public void matchingPollOne() throws PoolException {
        testMatchingPoll(oneMatcher);
    }

    public void matchingPollSome() throws PoolException {
        testMatchingPoll(someMatcher);
    }

    static Protein makeProtein(int i, String hname) {
        final Slaw desc = list(int32(i),
                               string("descrips"),
                               map(string("foo"), nil()));
        final Slaw ings = map(string("string-key"), string("value"),
                              string("nil-key"), nil(),
                              string("int64-key"), int64(i),
                              string("hose"), string(hname));
        final byte[] data = new byte[2 * i];
        for (int j = 0; j < data.length; ++j) data[j] = (byte)j;
        return protein(desc, ings, data);
    }

    static Protein[] deposit(Hose h, int no) throws PoolException {
        if (no <= 0) no = TLEN;
        final Protein[] result = new Protein[no];
        for (int i = 0; i < no; ++i)
            try {
                result[i] = h.deposit(makeProtein(i, h.name()));
            } catch (PoolException e) {
                fail("Deposit of " + i + "th protein failed");
            }
        return result;
    }

    void checkProtein(Protein p, int i) {
        assertEquals(defHose.name(), p.source());
        assertEquals(i + "th", depProteins[i], p);
    }

    private final Hose defHose;
    private final Protein[] depProteins;
    private final PoolServerAddress address;
    private static final int TLEN = 5;

}
