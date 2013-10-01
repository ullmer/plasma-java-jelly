package com.oblong.jelly.pool.net;

import com.oblong.jelly.*;
import com.oblong.jelly.communication.HoseFactory;
import com.oblong.jelly.communication.ObPoolCommunicationEventHandler;
import com.oblong.jelly.communication.ObPoolSender;
import com.oblong.jelly.util.ExceptionHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: valeria
 * Date: 10/1/13
 * Time: 1:37 PM
 *
 * This class allows to test TCPHoses using url localhost
 *
 * Exceptions are logged and printed or not (setup the flag)
 *
 * set up HoseTests.maxNumberOfProteins to handle more or less proteins
 */
public class ZTCPHoseTestMultipleProteins   {

	private static final boolean LOG_EXCEPTIONS = true;
	private static HoseTests tests;
	private static JellyTestPoolConnector connector;
	private static ObHandler listener =  new ObHandler();

	protected static final List<Protein> toSendproteinQueue = Collections
			.synchronizedList(new LinkedList<Protein>());

	protected static final List<Protein> sentProteinQueue = Collections
			.synchronizedList(new LinkedList<Protein>());
	protected static int numberOfDepositedProteins = 20000;//100 succeeds, 150 fails

	@BeforeClass
	public static void setUp() throws Exception {
		if (LOG_EXCEPTIONS) {
			ExceptionHandler.setExceptionHandler(new TestExceptionHandler());
		}

		PoolServerAddress poolServerAddress = PoolServerAddress.fromURI("tcp://localhost");
		System.out.println("Will test with pool server address "+poolServerAddress.toString());

		//create pool otherwise test will fail
		String poolName = "default-pool";
		PoolAddress poolAddress = new PoolAddress(poolServerAddress, poolName);
		if(!Pool.exists(poolAddress)){
			Pool.create(poolAddress, null);
		}

		connector = new JellyTestPoolConnector(poolServerAddress, poolName, listener, 1,  toSendproteinQueue, null);

		tests = new HoseTests(poolServerAddress, numberOfDepositedProteins);

	}

	@Test
	public void awaitNext()  {
		try {fff
			tests.awaitNext();
		} catch (Exception e) {
			ExceptionHandler.handleException(e, " tests.awaitNext() failed on round +" +tests.getLastExecutedRound());
		}
	}

	@AfterClass
	public static void getNumberOfExecutions(){
		System.out.println(" tests.awaitNext() finished on round " +tests.getLastExecutedRound());
	}
	public static class ObHandler implements ObPoolCommunicationEventHandler {

	@Override
	public void onProteinReceived(Protein p) {

	}

	@Override
	public void onConnected() {

	}

	@Override
	public void onErrorConnecting() {

	}

	@Override
	public void onConnectionLost(String reason) {

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

		public JellyTestPoolConnector(PoolServerAddress pools, String pool, ObPoolCommunicationEventHandler lis, int sleepSecs, List<Protein> proteinQueue, HoseFactory hoseFactory) {
			super(pools, pool, lis, sleepSecs, proteinQueue, hoseFactory);
		}

		protected void sendProtein(Protein protein) {
			super.sendProtein(protein);
			sentProteinQueue.add(protein);
		}

	}

}
