// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;
import static org.junit.Assert.*;

import static com.oblong.jelly.Slaw.*;

/**
 * Base class defining asynchronous tests on hoses.
 *
 * Created: Wed Jul 21 15:38:23 2010
 *
 * @author jao
 */
public class AsyncHoseTestBase extends PoolServerTestBase {

    public AsyncHoseTestBase() {

    }

    protected AsyncHoseTestBase(PoolServer s) throws PoolException {
        super(s);
    }

    protected AsyncHoseTestBase(PoolServerAddress addr) throws PoolException {
        super(addr);
    }


    static class Awaiter implements Runnable {

        public Awaiter(PoolAddress pa, long ta) throws PoolException {
            hose = Pool.participate(pa);
            hose.runOut();
            protein = null;
            timeout = ta;
        }

        public Protein getProtein() { return protein; }

        public void run() {
            try {
                protein = hose.awaitNext(timeout, TimeUnit.MILLISECONDS);
            } catch (PoolException e) {
                fail(e.getMessage());
            } catch (TimeoutException e) {
                fail(e.getMessage());
            } finally {
                try { hose.withdraw(); } catch (Throwable e) {}
            }
        }

        private final Hose hose;
        private final long timeout;
        private Protein protein;
    }

    @Test public void waitForEver()
        throws PoolException, InterruptedException {
        waitTest(-1);
    }

    @Test public void waitForAWhile()
        throws PoolException, InterruptedException {
        waitTest(250);
    }

    private void waitTest(long timeout)
        throws PoolException, InterruptedException {
        final PoolAddress pa = poolAddress("waitee");
        Pool.create(pa, null);
        final Awaiter aw = new Awaiter(pa, timeout);
        final Thread th = new Thread(aw);
        th.start();
        Thread.yield();
        Hose h = Pool.participate(pa);
        Protein p = h.deposit(protein(nil(), list(int8(1))));
        th.join();
        Pool.dispose(pa);
        assertEquals(p, aw.getProtein());
    }

}
