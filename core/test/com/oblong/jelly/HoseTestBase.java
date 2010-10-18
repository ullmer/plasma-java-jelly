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

    @Before public void openDefault() {
        if (defHose == null) {
            try {
                defHose =
                    Pool.participate(poolAddress("default-pool"), null);
                DEP_PROTEINS = deposit(defHose, -1);
            } catch (PoolException e) {
                fail("Initialisation error: " + e);
            }
        }
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

    protected static Protein makeProtein(int i) {
        final Slaw desc = list(string("descrips"), int32(i));
        final Slaw ings = map(string("string-key"), string("value"),
                              string("nil-key"), nil(),
                              string("int64-key"), int64(i));
        final byte[] data = new byte[2 * i];
        for (int j = 0; j < data.length; ++j) data[j] = (byte)j;
        return protein(desc, ings, data);
    }

    protected static Protein[] deposit(Hose h, int no) {
        if (no <= 0) no = TEST_PROTEINS.length;
        final Protein[] result = new Protein[no];
        for (int i = 0; i < no; ++i)
            try {
                result[i] = h.deposit(TEST_PROTEINS[i]);
            } catch (PoolException e) {
                fail("Deposit of " + i + "th protein failed. Protein was: "
                     + TEST_PROTEINS[i] + ". Exception: " + e);
            }
        return result;
    }

    private static Hose defHose = null;

    private static final Protein[] TEST_PROTEINS;
    private static Protein[] DEP_PROTEINS;
    private static final int TLEN = 5;

    static {
        TEST_PROTEINS = new Protein[TLEN];
        for (int i = 0; i < TEST_PROTEINS.length; ++i)
            TEST_PROTEINS[i] = makeProtein(i);
    }
}
