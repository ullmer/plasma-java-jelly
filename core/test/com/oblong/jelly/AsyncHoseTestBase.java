
/* (c)  oblong industries */

package com.oblong.jelly;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.oblong.util.ExceptionHandler;
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



        public Awaiter(PoolAddress pa, long ta) {
            poolAddress = pa;
            timeout = ta;
        }

        protected void connect() {
            hose = Pool.participate(poolAddress);
            //hose.runOut();
            protein = null;
        }

        public Protein getProtein() { return protein; }

        public void run() {
            try {
                connect();
                protein = hose.awaitNext(timeout, TimeUnit.MILLISECONDS);
            } catch (PoolException e) {
                fail(e.getMessage()+" : Pool exception");
            } catch (TimeoutException e) {
                fail(e.getMessage()+" : Timeout");
            } finally {
                try {
                    hose.withdraw();
                } catch (Throwable e) {
                    ExceptionHandler.handleException(e);
                }
            }
        }
	    private final PoolAddress poolAddress;
        private Hose hose;
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
        if(!Pool.exists(pa)){
            Pool.create(pa, null);
        }

        final Awaiter aw = new Awaiter(pa, timeout);
        final Thread th = new Thread(aw);
        th.start();
        Hose h = Pool.participate(pa);
        Protein p = h.deposit(protein(nil(), list(int8(1))));
        Thread.yield();
        th.join();
        h . withdraw ();
        Pool.dispose(pa);
        assertEquals(p, aw.getProtein());
    }

}
