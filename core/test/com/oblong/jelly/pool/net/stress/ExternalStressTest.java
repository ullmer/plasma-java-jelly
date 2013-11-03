package com.oblong.jelly.pool.net.stress;

import com.oblong.jelly.*;
import com.oblong.jelly.util.ExceptionHandler;
import com.oblong.util.Util;
import net.jcip.annotations.GuardedBy;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * User: valeria
 * Date: 10/1/13
 * Time: 1:37 PM
 *
 * This class allows to test TCP Hoses waitNext (timeout, unit)
 *
 * using url localhost
 *
 * the pool name is provided bt you can modify, it will be created
 *
 */
public class ExternalStressTest {

	private static final Logger logger = Logger.getLogger(ExternalStressTest.class);

	final TestConfig testConfig = TestConfig.CONFIG;

	final PoolServerAddress poolServerAddress;

	public final String poolName = "ExternalTCPMultiProteinTest_pool";

	final PoolAddress poolAddress;

	final boolean runningViaJUnit;

	final Random random = new Random();

	@GuardedBy("this")
	long qtyReceivedProteins = 0;


	ExternalStressTest() {
		this(true);
	}

	public ExternalStressTest(boolean runningViaJUnit) {
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
	 * Tests sending and receiving multiple proteins
	 */
	@Test
	public void stressTestAwaitNext()  {
		throw new RuntimeException("FIXME: Implement JUnit entry point");
	}

	/** Main Method to run an endless variant of the test outside of JUnit framework */
	public static void main(String[] args) {
		initialize();
		logger.info("Starting test via main() method");
		ExternalStressTest test = new ExternalStressTest(true);
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
		qtyReceivedProteins ++;
		logger.debug("qtyReceivedProteins for whole test: " + qtyReceivedProteins);
	}


}
