package com.oblong.jelly.pool.net;

import com.oblong.jelly.*;
import com.oblong.jelly.communication.HoseFactory;
import com.oblong.jelly.communication.ObPoolCommunicationEventHandler;
import com.oblong.jelly.communication.ObPoolConnector;
import com.oblong.jelly.communication.ObPoolSender;
import com.oblong.jelly.util.ExceptionHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
public class TCPMultiProteinTest {

	/***if set to false exceptions/logs will no be logged ***/
	protected static final List<Protein> toSendProteinQueue = Collections
			.synchronizedList(new LinkedList<Protein>());

	private static ExternalHoseTests tests;
	private static JellyTestPoolConnector connector;
	private static ObHandler listener =  new ObHandler();
	private static final String TAG = "TCPMultiProteinTest";

	public static final String POOL_NAME = "external-tests-pool";

	@BeforeClass
	public static void setUp() throws Exception {
		if (TCPMultiProteinTestConfig.SHOW_LOGS) {
			ExceptionHandler.setExceptionHandler(new TestExceptionHandler());
		}

		final PoolServerAddress poolServerAddress = PoolServerAddress.fromURI("tcp://localhost");
		logMessage("Will test with pool server address "+poolServerAddress.toString());

		//create pool otherwise test will fail
		final PoolAddress poolAddress = new PoolAddress(poolServerAddress, POOL_NAME);

		try {
			if(!Pool.exists(poolAddress)){
				Pool.create(poolAddress, ObPoolConnector.DEFAULT_POOL_OPTIONS);
			}

			connector = new JellyTestPoolConnector(poolServerAddress,
					POOL_NAME,
					listener,
					TCPMultiProteinTestConfig.SLEEP_SECS,
					toSendProteinQueue,
					null);
			connector.start();

			tests = new ExternalHoseTests(poolServerAddress, TCPMultiProteinTestConfig.NUMBER_OF_DEPOSITED_PROTEINS, POOL_NAME);
		} catch (Exception e){
			//something wrong with server
			ExceptionHandler.handleException(e);
			//if uncommented the test fails and builbot gives error!
			//ToDO: fix this
//			fail("Unable to connect to pool server, you need a running pool server and g-speak installed");
		}
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
				ExceptionHandler.handleException("Tests not intiated");
			}
		} catch (PoolException e) {
			ExceptionHandler.handleException(e, " tests.awaitNext() failed on round +" +tests.getLastExecutedRound());
		}
	}

	private static void logErrorMessage(String errorMessage) {
		if(TCPMultiProteinTestConfig.SHOW_LOGS){
			System.err.println(errorMessage);
		}
	}

	private static void logMessage(String message) {
		if(TCPMultiProteinTestConfig.SHOW_LOGS){
			System.out.println(message);
		}
	}

	@AfterClass
	public static void afterTesting(){
		try {
			System.out.println(" tests.awaitNext() finished on round " + tests.getLastExecutedRound());
			System.out.println(" last received protein "+tests.getLastObtained());
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
		public void handleExceptionImpl(Throwable e, String syntheticMsg) {
			System.err.println("====== " + syntheticMsg);
			e.printStackTrace();
		}
	}

	public static class JellyTestPoolConnector extends ObPoolSender {
		private Random r = new Random();

		public JellyTestPoolConnector(PoolServerAddress pools,
		                              String pool,
		                              ObPoolCommunicationEventHandler lis,
		                              int sleepSecs,
		                              List<Protein> proteinQueue,
		                              HoseFactory hoseFactory) {
			super(pools, pool, lis, sleepSecs, proteinQueue, hoseFactory);
		}

		@Override
		public void connect() {
			super.connect();
			createAndSendProteins();
		}

		private static void createAndSendProteins() {
			for(int i=0; i < TCPMultiProteinTestConfig.NUMBER_OF_DEPOSITED_PROTEINS; i++){
				Protein protein = ExternalHoseTests.makeProtein(i, POOL_NAME);
				toSendProteinQueue.add(protein);
			}

			logMessage("All Proteins have been sent");
		}

		protected void sendProtein(Protein protein) {
			logMessage("Sending protein "+protein);
			try {
				super.sendProtein(protein);
			} catch (Exception e) {
				ExceptionHandler.handleException(e, "Pool exception");
			}
		}

		protected void maybeSleep() {
			if (sleepSecs > 0) {
				try {
					randomSleep();
				} catch (InterruptedException e) {
					e.printStackTrace();
					Thread.currentThread().interrupt();
				}
			}
		}

		private void randomSleep() throws InterruptedException {
			Thread.sleep(r.nextInt(sleepSecs));
		}

	}



}
