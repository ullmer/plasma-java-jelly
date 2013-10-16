package com.oblong.jelly.pool.net;

import com.oblong.jelly.*;
import com.oblong.jelly.communication.ObPoolCommunicationEventHandler;
import com.oblong.jelly.communication.ObPoolConnector;
import com.oblong.jelly.util.ExceptionHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
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
 * Exceptions are logged and printed or not (setup the flag)
 *
 * set up NUMBER_OF_DEPOSITED_PROTEINS to handle more or less proteins
 */
public class ExternalTCPMultiProteinTest {

	/***if set to false exceptions/logs will no be logged ***/
	private static final List<Protein> toSendProteinQueue = Collections
			.synchronizedList(new LinkedList<Protein>());

	private static ExternalHoseTests tests;
	private static JellyTestPoolConnector connector;
	private static ObHandler listener =  new ObHandler();
	private static final String TAG = "ExternalTCPMultiProteinTest";

	public static final String POOL_NAME = "external-tests-pool";

	@BeforeClass
	public static void setUp() throws Exception {
		if (TCPMultiProteinTestConfig.SHOW_LOGS) {
			ExceptionHandler.setExceptionHandler(new TestExceptionHandler());
		}

		final PoolServerAddress poolServerAddress = getPoolServerAddress();
		logMessage("Will test with pool server address "+poolServerAddress.toString());
		final PoolAddress poolAddress = new PoolAddress(poolServerAddress, POOL_NAME);
		try {
			//create pool otherwise test will fail
			createPoolIfNonExisting(poolAddress);

			connector = new JellyTestPoolConnector(poolServerAddress,
					POOL_NAME,
					listener,
					TCPMultiProteinTestConfig.SLEEP_MILI_SECS,
					toSendProteinQueue,
					null, true);
			connector.start();

			tests = new ExternalHoseTests(poolServerAddress, TCPMultiProteinTestConfig.NUMBER_OF_DEPOSITED_PROTEINS, POOL_NAME);
		} catch (Exception e){
			//something wrong with server
			ExceptionHandler.handleException(e);
			fail("Unable to connect to pool server, you need a running pool server and g-speak installed");
		}
	}

	static void createPoolIfNonExisting(PoolAddress poolAddress) throws PoolException {
		if(!Pool.exists(poolAddress)){
			Pool.create(poolAddress, ObPoolConnector.DEFAULT_POOL_OPTIONS);
		}
	}

	static PoolServerAddress getPoolServerAddress() throws BadAddressException {
		String uri = System.getProperty("com.oblong.jelly.externalServer", TCPMultiProteinTestConfig.URI);
		return PoolServerAddress.fromURI(uri);
	}

	/***
	 * Tests receiving multiple proteins
	 */
	@Test
	public void awaitNext()  {
		try {
			if(tests!=null){
				tests.awaitNext();
			} else {
				// FIXME shouldn't this be fatal?
				ExceptionHandler.handleException("Tests not initiated");
			}
		} catch (PoolException e) {
			// FIXME shouldn't this be fatal?
			ExceptionHandler.handleException(e, " tests.awaitNext() failed on round +" +tests.getLastExecutedRound());
		}
	}

	private static void logErrorMessage(String errorMessage) {
		if(TCPMultiProteinTestConfig.SHOW_LOGS){
			System.err.println(errorMessage);
		}
	}

	static void logMessage(String message) {
		if(TCPMultiProteinTestConfig.SHOW_LOGS){
			System.out.println(message);
		}
	}

	@AfterClass
	public static void afterTesting(){
		try {
			System.out.println(" tests.awaitNext() finished on round " + tests.getLastExecutedRound());
			System.out.println(" last received protein " + tests.getLastObtained());
			connector.halt();
			tests.cleanUp();
		} catch (Exception e){
			ExceptionHandler.handleException(e);
		}
	}

	public static class ObHandler implements ObPoolCommunicationEventHandler {

		@Override
		public void onProteinReceived(Protein p) {
			//ignored here
		}

		@Override
		public void onConnected() {
			logMessage(TAG + " Hose connected correctly");

		}

		@Override
		public void onErrorConnecting() {
			logErrorMessage(TAG + " Error connecting sending hose ");
		}

		@Override
		public void onConnectionLost(String reason) {
			logErrorMessage(TAG + " Connection lost to sending hose ");
		}

	}

	public static class TestExceptionHandler extends ExceptionHandler {

		public TestExceptionHandler() {	}

		@Override
		public void handleExceptionImpl(Throwable e, String syntheticMsg)  {
			System.err.println("====== " + syntheticMsg);
			e.printStackTrace();
		}

	}

}
