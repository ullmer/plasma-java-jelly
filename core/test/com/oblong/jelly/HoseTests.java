
/* (c)  oblong industries */

package com.oblong.jelly;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.oblong.util.ExceptionHandler;
import org.slf4j.*;
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

//    public static final String TAG = "HoseTests";
    public static final PoolOptions POOL_OPTIONS = PoolOptions.MEDIUM;
	private static final Logger logger = LoggerFactory.getLogger(HoseTests.class);


    public static class Tests<Dummy> {

        private static HoseTests tests;

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
        @Test public void pNth() throws Exception { tests.partialNth(); }
        @Test public void previous() throws Exception { tests.previous(); }
        @Test public void current() throws Exception { tests.current(); }
        @Test public void range() throws Exception { tests.range(); }
        @Test public void meta() throws Exception { tests.simpleMeta(); }
        @Test public void mixMeta() throws Exception { tests.mixedMeta(); }


        @Test public void matchingAll() throws Exception {
            tests.matchingAll();
        }
        @Test public void matchingOne() throws Exception {
            tests.matchingOne();
        }
        @Test public void matchSome() throws Exception {
            tests.matchingSome();
        }

        @AfterClass public static void cleanUpTests() {
            if (tests != null) {
                tests.cleanUp();
                tests = null;
            }
        }

        protected static void initTests(PoolServerAddress addr)
                            throws Exception {
            if (addr != null) {
                tests = new HoseTests(addr);
            } else {
                logPoolServerAddressError("HoseTests.initTests : Nothing tested ... pool server addr is null / not provided");
            }
        }
    }

	public static void logPoolServerAddressError(String classAndMethod) {
		logger.warn(classAndMethod);
	}

    public HoseTests() {
        address = null;
        defHose = null;
        depProteins = null;
        this.maxNumberOfProteins = DEFAULT_NUMBER_OF_PROTEINS;
    }

    public HoseTests(PoolServerAddress addr) throws PoolException {
        this.address = addr;
        this.defHose = getHoseFromAddress();
        this.maxNumberOfProteins = DEFAULT_NUMBER_OF_PROTEINS;
        this.depProteins = deposit(defHose, -1);
    }

    public HoseTests (PoolServerAddress addr,
                      Protein[] proteins,
                      int maxNumOfProteins,
                      String poolName,
                      PoolOptions opts) throws PoolException {
        this.address = addr;
        this.depProteins = proteins;
        this.maxNumberOfProteins = maxNumOfProteins;
        this.defHose = getHoseFromAddress(poolName, opts);
    }

    private Hose getHoseFromAddress() throws PoolException {
        final PoolAddress pa = getPoolAddress("default-pool");
        logPoolInfo(POOL_OPTIONS);
        return Pool.participate(pa, POOL_OPTIONS);
    }

    private Hose getHoseFromAddress(String name, PoolOptions opts) throws PoolException {
        final PoolAddress pa = new PoolAddress(address, name);
        logPoolInfo(opts);
        return Pool.participate(pa, opts);
    }

	private void logPoolInfo(PoolOptions opts) {
		logger.info("Created pool with capacity " + opts.poolSize());
	}

    private PoolAddress getPoolAddress(String name) throws PoolException {
        final PoolAddress pa = new PoolAddress(address, name);
        try {
            Pool.dispose(pa);
        } catch (NoSuchPoolException e) {
            ExceptionHandler.handleException(e, "Exception in getPoolAddress");
        }
        return pa;
    }

    public HoseTests(PoolServerAddress poolServerAddress, int numberOfDepositedProteins) throws PoolException {
        this.address = poolServerAddress;
        this.defHose = getHoseFromAddress();

        this.maxNumberOfProteins = numberOfDepositedProteins;
        this.depProteins = deposit(defHose, -1);
    }

    public void cleanUp() {
        withdrawFromHose ();
        removePool ();
    }

    private void removePool () {
        try {
            Pool.dispose (defHose.poolAddress ());
        } catch (PoolException e) {
            ExceptionHandler.handleException(e);
        }
    }

    public void withdrawFromHose () {
        try {
            defHose.withdraw ();
        } catch (Exception e) {
            ExceptionHandler.handleException (e);
        }
    }

    public void hoseName () throws PoolException {
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
        assertEquals(depProteins[maxNumberOfProteins - 1].index(), defHose.newestIndex());
    }

    public void nth() throws PoolException {
        for (int i = 0; i < maxNumberOfProteins; ++i) {
            checkProtein(defHose.nth(i), i);
        }
    }

    public void partialNth() throws Exception {
        Protein p = defHose.nth(0, false, false, false);
        assertNull(p.descrips());
        assertNull(p.ingests());
        assertEquals(0, p.dataLength());
        assertEquals(depProteins[0].index(), p.index());
        assertEquals(depProteins[0].timestamp(), p.timestamp(), 0.0);
        assertEquals(getTestHoseName(), p.source());

        p = defHose.nth(1, true, false, false);
        assertEquals(depProteins[1].descrips(), p.descrips());
        assertNull(p.ingests());
        assertEquals(0, p.dataLength());
        assertEquals(depProteins[1].index(), p.index());
        assertEquals(depProteins[1].timestamp(), p.timestamp(), 0.0);
        assertEquals(getTestHoseName(), p.source());

        p = defHose.nth(2, false, true, false);
        assertNull(p.descrips());
        assertEquals(depProteins[2].ingests(), p.ingests());
        assertEquals(0, p.dataLength());
        assertEquals(depProteins[2].index(), p.index());
        assertEquals(depProteins[2].timestamp(), p.timestamp(), 0.0);
        assertEquals(getTestHoseName(), p.source());

        p = defHose.nth(2, false, false, true);
        assertNull(p.ingests());
        assertNull(p.descrips());
        assertArrayEquals(depProteins[2].copyData(), p.copyData());
        assertEquals(depProteins[2].index(), p.index());
        assertEquals(depProteins[2].timestamp(), p.timestamp(), 0.0);
        assertEquals(getTestHoseName(), p.source());

        p = defHose.nth(3, true, false, true);
        assertEquals(depProteins[3].descrips(), p.descrips());
        assertNull(p.ingests());
        assertArrayEquals(depProteins[3].copyData(), p.copyData());
        assertEquals(depProteins[3].index(), p.index());
        assertEquals(depProteins[3].timestamp(), p.timestamp(), 0.0);
        assertEquals(getTestHoseName(), p.source());

        p = defHose.nth(1, false, true, true);
        assertNull(p.descrips());
        assertEquals(depProteins[1].ingests(), p.ingests());
        assertArrayEquals(depProteins[1].copyData(), p.copyData());
        assertEquals(depProteins[1].index(), p.index());
        assertEquals(depProteins[1].timestamp(), p.timestamp(), 0.0);
        assertEquals(getTestHoseName(), p.source());

        checkProtein(defHose.nth(maxNumberOfProteins - 1, true, true, true), maxNumberOfProteins - 1);
    }

    protected String getTestHoseName() {
        return defHose.name();
    }

    public void simpleMeta() throws PoolException {
        for (int i = 0; i < maxNumberOfProteins; ++i) {
            final ProteinMetadata md =
                defHose.metadata(new MetadataRequest(i));
            checkMeta(md, i);
            assertNull(md.descrips());
            assertNull(md.ingests());
            assertEquals(0, md.data().length);
        }
    }

    public void mixedMeta() throws PoolException {
        final MetadataRequest[] reqs = {
            new MetadataRequest(0).descrips(true),
            new MetadataRequest(2).ingests(true),
            new MetadataRequest(3).dataStart(1).dataLength(2)
        };
        final List<ProteinMetadata> mds = defHose.metadata(reqs);
        for (int i = 0; i < reqs.length; ++i) {
            final int idx = (int)reqs[i].index();
            checkMeta(mds.get(i), idx);

            final Protein p = depProteins[idx];
            if (reqs[i].descrips())
                assertEquals(p.descrips(), mds.get(i).descrips());
            else
                assertNull(mds.get(i).descrips());

            if (reqs[i].ingests())
                assertEquals(p.ingests(), mds.get(i).ingests());
            else
                assertNull(mds.get(i).ingests());

            final byte[] data = mds.get(i).data();
            assertEquals(Math.max(0, reqs[i].dataLength()), data.length);
            final int start = (int)reqs[i].dataStart();
            for (int k = 0; k < data.length; ++k)
                assertEquals(p.datum(start + k), data[k]);
        }
    }

    public void next() throws PoolException {
        defHose.rewind();
        for (int i = 0; i < maxNumberOfProteins; ++i) {
            checkProtein(defHose.next(), i);
            assertTrue(i + "th", depProteins[i].index() < defHose.index());
        }
        assertEquals(defHose.newestIndex() + 1, defHose.index());
    }

    public void range() throws PoolException {
        defHose.runOut();
        for (int i = 1; i <= maxNumberOfProteins; ++i) {
            final List<Protein> ps = defHose.range(0, i);
            assertEquals(i, ps.size());
            for (int k = 0; k < i; ++k) checkProtein(ps.get(k), k);
        }
    }

    public void await() throws PoolException, TimeoutException {
        defHose.seekTo(defHose.oldestIndex());
        for (int i = 0; i < maxNumberOfProteins; ++i) {
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
        for (int i = 0; i < maxNumberOfProteins; ++i) {
//            currentRound = i;
            checkProtein(defHose.awaitNext(), i);
            assertTrue(i + "th", depProteins[i].index() < defHose.index());
        }
        assertEquals(defHose.newestIndex() + 1, defHose.index());
    }

    public void previous() throws PoolException {
        defHose.runOut();
        assertEquals(defHose.newestIndex() + 1, defHose.index());
        for (int i = maxNumberOfProteins - 1; i > 0; --i) {
            checkProtein(defHose.previous(), i);
            assertEquals(i + "th", depProteins[i].index(), defHose.index());
        }
    }

    public void current() throws PoolException {
        for (int i = 0; i < maxNumberOfProteins; i++) {
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
        for (int i = 0; i < maxNumberOfProteins; ++i) {
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

    public static Protein makeProtein(int i, String hname) {
        final Slaw desc = list(int32(i),
                               string("descrips"),
                               map(string("foo"), nil()));
        final Slaw ings = map(string("string-key"), string("value"),
                              string("nil-key"), nil(),
                              string("int64-key"), int64(i),
                              string("hose"), string(hname));
        final byte[] data = new byte[2 * i];
        for (int j = 0; j < data.length; ++j) {
            data[j] = (byte)j;
        }
        return protein(desc, ings, data);
    }

    static Protein[] deposit(Hose h, int no) throws PoolException {
        if (no <= 0) no = maxNumberOfProteins;
        final Protein[] result = new Protein[no];
        for (int i = 0; i < no; ++i)
            try {
                result[i] = h.deposit(makeProtein(i, h.name()));
            } catch (PoolException e) {
                fail("Deposit of " + i + "th protein failed");
            }
        return result;
    }

    protected void checkProtein(Protein p, int i) {
        assertEquals(getTestHoseName(), p.source());
        assertEquals(i + "th", depProteins[i], p);
    }


    void checkMeta(ProteinMetadata md, int i) {
        assertEquals(depProteins[i].index(), md.index());
        assertEquals(depProteins[i].timestamp(), md.timestamp(), 0.00001);
        assertEquals(3, md.descripsNumber());
        assertEquals(4, md.ingestsNumber());
        assertEquals(depProteins[i].dataLength(), md.dataSize());
    }



    protected final Hose defHose;
    private final Protein[] depProteins;
    private final PoolServerAddress address;
    public static final int DEFAULT_NUMBER_OF_PROTEINS = 5;
    public static int maxNumberOfProteins;


}
