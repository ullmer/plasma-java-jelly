// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import org.junit.After;
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
        defHose = null;
    }

    protected HoseTestBase(PoolServerAddress addr) throws PoolException {
        super(addr);
        defHose = null;
    }

    @Before public void openDefault() throws PoolException {
        defHose = Pool.participate(poolAddress("default-pool"), null);
    }

    @After public void closeDefault() throws PoolException {
        if (defHose != null) {
            defHose.withdraw();
            assertFalse(defHose.isConnected());
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

    @Test public void withdraw() throws PoolException {
        assertTrue(defHose.isConnected());
    }

    @Test public void deposit() throws PoolException {
        final Protein[] deposited = deposit(defHose, -1);
        assertEquals(deposited[0].index(), defHose.oldestIndex());
        assertEquals(deposited[TLEN - 1].index(), defHose.newestIndex());
    }

    @Test public void nth() throws PoolException {
        final Protein[] deposited = deposit(defHose, -1);
        for (int i = 0; i < TLEN; ++i) {
            assertEquals(deposited[i], defHose.nth(i));
            assertTrue(defHose.index() < 0);
        }
    }

    @Test public void next() throws PoolException {
        final Protein[] deposited = deposit(defHose, -1);
        for (int i = 0; i < TLEN; ++i) {
            assertEquals(deposited[i], defHose.next());
            assertEquals(i + "th", deposited[i].index(), defHose.index());
        }
        assertEquals(defHose.newestIndex(), defHose.index());
    }

    @Test public void previous() throws PoolException {
        final Protein[] deposited = deposit(defHose, -1);
        defHose.runOut();
        assertEquals(defHose.newestIndex() + 1, defHose.index());
        for (int i = TLEN - 1; i > 0; --i) {
            assertEquals(i + "th", deposited[i], defHose.previous());
            assertEquals(i + "th", deposited[i].index(), defHose.index());
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


    private Hose defHose;

    private static final Protein[] TEST_PROTEINS;
    private static final int TLEN = 5;

    static {
        TEST_PROTEINS = new Protein[TLEN];
        for (int i = 0; i < TEST_PROTEINS.length; ++i)
            TEST_PROTEINS[i] = makeProtein(i);
    }
}
