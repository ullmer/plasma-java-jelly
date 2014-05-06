package com.oblong.jelly.pool.net.stress;

import com.oblong.jelly.BadAddressException;
import com.oblong.jelly.PoolAddress;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.util.Util;
import com.oblong.util.probability.ProbabilityHost;
import net.jcip.annotations.GuardedBy;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import java.util.Random;

/**
 * This test is designed to "stress-test" multiple parts of Jelly, mainly hoses, in order to verify their reliability.
 * It is designed to uncover, among other things, any problems arising from timing-dependency.
 * It requires long running (hundreds of thousands to millions of proteins), for the results to be meaningful.
 *
 * Note, that it is not a JUnit test, because it requires rather long running. Therefore we do not use the "Test"
 *   suffix, in order to prevent JUnit from picking-up this class.
 *
 *
 * This test utiilizes 2 threads (one for sending and one for receiving; the latter one re-created per connection
 *   session).
 * It sends batches of proteins and periodically connects and withdraws hoses.
 *
 * Occasionally, it disposes of the used pool and re-creates it.
 *
 *
 * Uses Log4J logging on various levels (TRACE..FATAL).
 *
 */
public class StressTestJelly {

	private static final Logger logger = Logger.getLogger(StressTestJelly.class);

	final TestConfig testConfig = TestConfig.CONFIG;

	final PoolServerAddress poolServerAddress;

	public final String poolName = "Jelly_StressTest_pool";

	final PoolAddress poolAddress;

	final boolean runningViaJUnit;

	final Random random = ProbabilityHost.the.getRandom();

	@GuardedBy("this")
	private long qtyProteinsReceivedInWholeTest = 0;


	public StressTestJelly(boolean runningViaJUnit) {
		logger.info("Random seed: " + ProbabilityHost.the.getRandomSeed());
		if ( runningViaJUnit ) {
			throw new UnsupportedOperationException("Running via JUnit not supported");
		}
		this.runningViaJUnit = runningViaJUnit;
		logger.info("runningViaJUnit: " + runningViaJUnit);
		try {
			/* Property "test.server" for compatibility with what is already in Ant/JUnit: */
			String externalServerProp = Util.getMandatorySystemProperty("com.oblong.jelly.externalServer",
					"tcp://a.b.c.d");
			poolServerAddress = PoolServerAddress.fromURI(externalServerProp);
			poolAddress = new PoolAddress(poolServerAddress, poolName);
		} catch (BadAddressException e) { throw Util.rethrow(e); }
	}

	/*
//	 * Tests sending and receiving multiple proteins
//	 */
//	@Test
//	public void stressTestAwaitNext()  {
//		throw new RuntimeException("FIXME: Implement JUnit entry point");
//	}

	/** Main Method to run an endless variant of the test outside of JUnit framework */
	public static void main(String[] args) {
		initialize();
		logger.info("Starting test via main() method");
		StressTestJelly test = new StressTestJelly(false);
		test.runTest();
	}

	private void runTest() {
		long qtyConnectionSessionCycles = testConfig.qtyConnectionSessionCycles;
		for ( long iConnSession = 0; iConnSession < qtyConnectionSessionCycles; iConnSession ++ ) {
			ConnectionSession connectionSession = null;
			try {
				connectionSession = new ConnectionSession(this, iConnSession);
				connectionSession.execute();
			} catch (Throwable throwable) {
				if ( runningViaJUnit ) {
					throw Util.rethrow(throwable); // it is fatal when running via JUnit
				} else {
					logger.error("Uncaught exception in connectionSession cycle number " + iConnSession, throwable);
//					throwable.printStackTrace();
//					stopConnectionSessionIfPossible(connectionSession);
				}
			} // no need to stop it here, execute() calls stop() at the end;
		}
	}

//	private void stopConnectionSessionIfPossible(ConnectionSession connectionSession) {
//		logger.info("Trying to stop connectionSession: " + connectionSession);
//		if ( connectionSession != null ) {
//			try {
//				connectionSession.stop();
//			} catch (Throwable t) {
//				logger.fatal("Uncaught exception when trying to stop "+connectionSession, t);
//			}
//		} else {
//			logger.warn("Unable to stop connection session, because it is " + connectionSession);
//		}
//	}

	private static void initialize() {
		initLog4J();
	}

	private static void initLog4J() {
		String confFileName = "tests_log4j_conf.xml";
		DOMConfigurator.configure(confFileName);
		logger.info("Loaded Log4J configuration from file: " + confFileName);
	}

	public synchronized void incrementQtyReceivedProteins() {
		qtyProteinsReceivedInWholeTest++;

		Level level;
		if ( qtyProteinsReceivedInWholeTest % 1000 == 0 ) {
			level = Level.INFO;
		} else {
			level = Level.DEBUG;
		}

		if ( logger.isEnabledFor(level) ) {
			logger.log(level, "qtyProteinsReceivedInWholeTest: " + qtyProteinsReceivedInWholeTest);
		}
	}


}
