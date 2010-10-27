// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import static com.oblong.jelly.Slaw.*;

/**
 *
 * Created: Tue Jul  6 14:46:05 2010
 *
 * @author jao
 */
public class HoseTestBase extends PoolServerTestBase {

    public HoseTestBase() {
    }

    protected HoseTestBase(PoolServer s) throws PoolException {
        super(s);
    }

    protected HoseTestBase(PoolServerAddress addr) throws PoolException {
        super(addr);
    }

    @Before public void openDefault() throws PoolException {
        assertNotNull(server);
        if (defHose == null) {
            final PoolAddress address = poolAddress("default-pool");
            defHose = Pool.participate(address, null);
            if (DEP_PROTEINS == null) {
                DEP_PROTEINS = deposit(defHose, -1);
            }
        }
        assertNotNull(defHose);
        assertNotNull(DEP_PROTEINS);
    }

    @Test public void hoseName() throws PoolException {
        final PoolAddress a = poolAddress("eipool");
        final Hose h = Pool.participate(a, PoolOptions.SMALL);
        assertEquals(a, h.poolAddress());
        assertEquals(a.toString(), h.name());
        final String newName = "this name is not the same";
        h.setName(newName);
        assertEquals(newName, h.name());
        h.withdraw();
        assertEquals(newName, h.name());
    }

    @Test public void deposit() throws PoolException {
        assertEquals(DEP_PROTEINS[0].index(), defHose.oldestIndex());
        assertEquals(DEP_PROTEINS[TLEN - 1].index(), defHose.newestIndex());
    }

    @Test public void nth() throws PoolException {
        for (int i = 0; i < TLEN; ++i)
            assertEquals(DEP_PROTEINS[i], defHose.nth(i));
    }

    @Test public void next() throws PoolException {
        defHose.rewind();
        for (int i = 0; i < TLEN; ++i) {
            assertEquals(i + "th", DEP_PROTEINS[i], defHose.next());
            assertTrue(i + "th", DEP_PROTEINS[i].index() < defHose.index());
        }
        assertEquals(defHose.newestIndex() + 1, defHose.index());
    }

    @Test public void await() throws PoolException, TimeoutException {
        defHose.seekTo(defHose.oldestIndex());
        for (int i = 0; i < TLEN; ++i) {
            assertEquals(i + "th", DEP_PROTEINS[i],
                         defHose.awaitNext(1, TimeUnit.SECONDS));
            assertTrue(i + "th", DEP_PROTEINS[i].index() < defHose.index());
        }

        assertEquals(defHose.newestIndex() + 1, defHose.index());
    }

    @Test public void awaitTimeout() throws PoolException {
        defHose.runOut();
        final long lapse = 10;
        final long t = System.currentTimeMillis();
        try {
          defHose.awaitNext(lapse, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            // we're good if the timeout expired
            assertTrue(System.currentTimeMillis() >= t + lapse);
            return;
        }
        fail("awaitNext() didn't timeout");
    }

    @Test public void awaitNext() throws PoolException {
        defHose.seekTo(defHose.oldestIndex());
        for (int i = 0; i < TLEN; ++i) {
            assertEquals(i + "th", DEP_PROTEINS[i], defHose.awaitNext());
            assertTrue(i + "th", DEP_PROTEINS[i].index() < defHose.index());
        }
        assertEquals(defHose.newestIndex() + 1, defHose.index());
    }

    @Test public void previous() throws PoolException {
        defHose.runOut();
        assertEquals(defHose.newestIndex() + 1, defHose.index());
        for (int i = TLEN - 1; i > 0; --i) {
            assertEquals(i + "th", DEP_PROTEINS[i], defHose.previous());
            assertEquals(i + "th", DEP_PROTEINS[i].index(), defHose.index());
        }
    }

    @Test public void current() throws PoolException {
        for (int i = 0; i < TLEN; i++) {
            defHose.seekTo(DEP_PROTEINS[i].index());
            assertEquals(i + "th", DEP_PROTEINS[i], defHose.current());
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

    private static final Matcher someMatcher = new Matcher () {
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
            final Slaw[] m = matcher.get(DEP_PROTEINS[i].descrips());
            assertEquals(i + "th", DEP_PROTEINS[i], defHose.next(m));
            assertTrue(i + "th", DEP_PROTEINS[i].index() < defHose.index());

            defHose.runOut();
            assertEquals(i + "th", DEP_PROTEINS[i], defHose.previous(m));
            assertEquals(i + "th", DEP_PROTEINS[i].index(), defHose.index());

            defHose.rewind();
            assertEquals(i + "th", DEP_PROTEINS[i], defHose.next(m));
            assertTrue(i + "th", DEP_PROTEINS[i].index() < defHose.index());
        }
        assertEquals(defHose.newestIndex() + 1, defHose.index());
    }

    @Test public void matchingAll() throws PoolException {
        testMatching(allMatcher);
    }

    @Test public void matchingOne() throws PoolException {
        testMatching(oneMatcher);
    }

    @Test public void matchingSome() throws PoolException {
        testMatching(someMatcher);
    }

    @Test public void poll() throws PoolException {
        defHose.rewind();
        for (int i = 0; i < TLEN; ++i) {
            assertTrue(defHose.poll());
            assertTrue(defHose.poll());
            assertEquals(DEP_PROTEINS[i], defHose.peek());
            assertEquals(DEP_PROTEINS[i], defHose.next());
            assertNull(defHose.peek());
        }
        assertFalse(defHose.poll());
        assertNull(defHose.peek());
    }

    @Test public void cancelledPoll() throws PoolException {
        defHose.rewind();
        for (int i = 0; i < TLEN; ++i) {
            assertTrue(defHose.poll());
            assertEquals(DEP_PROTEINS[i], defHose.peek());
            defHose.oldestIndex();
            assertEquals(DEP_PROTEINS[i], defHose.next());
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

    @Test public void matchingPollAll() throws PoolException {
        testMatchingPoll(allMatcher);
    }

    @Test public void matchingPollOne() throws PoolException {
        testMatchingPoll(oneMatcher);
    }

    @Test public void matchingPollSome() throws PoolException {
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

    private static Hose defHose = null;

    private static Protein[] DEP_PROTEINS = null;
    private static final int TLEN = 5;

}
