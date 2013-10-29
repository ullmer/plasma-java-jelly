package com.oblong.jelly.pool.net.stress;

import com.oblong.jelly.*;
import com.oblong.jelly.communication.ObPoolConnector;
import com.oblong.jelly.util.ExceptionHandler;
import org.apache.log4j.Logger;

import java.util.Random;

import static junit.framework.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: valeria
 * Date: 10/3/13
 * Time: 5:54 PM
 */
public class TestPoolSender {

	private static final Logger logger = Logger.getLogger(TestPoolSender.class);

	private static final int IGNORED_VALUE = -1;
	private Random r = new Random();

	//counts the number of "correct" proteins
	private int proteinCounter = 0;
	private final int maxProteinNumber;
	private final int maxProteinBatchSize;
	public static final String ARGUMENT_FOR_PROTEIN_MAP = "foo";
	private PoolServerAddress poolServerAddress;
	private String pool;
	private Hose hose;
	private int sleepMs;

	public TestPoolSender(PoolServerAddress pools,
	                      String pool) {
		this.poolServerAddress = pools;
		this.pool = pool;
		this.maxProteinNumber =
				ExternalTCPMultiProteinTestConfig.getTotalNumberOfProteins();
		this.maxProteinBatchSize = ExternalTCPMultiProteinTestConfig.getBatchSize();
		if ( logger.isDebugEnabled() ) {
			logger.debug("Created sender for "+maxProteinNumber+" proteins");
		}
	}


	protected void connect() throws PoolException {
		logger.info("Connecting to pool " + pool);
		initHose();
		if (hose != null)
		{
			logger.info("Connection successful to " +pool);
		}
		else
		{
			throw new RuntimeException("Hose is null");
		}
	}

	private void initHose() throws PoolException {
		PoolAddress address = new PoolAddress(poolServerAddress, pool);
		hose = Pool.participate(address, ObPoolConnector.DEFAULT_POOL_OPTIONS);

	}

	protected void withdrawHose() {
		if (hose == null) {
			logger.warn("Hose is already null.");
		} else {
			try {
				//this throws out of memory exception many times
				hose.withdraw();
			} catch (Throwable e) {
				String hoseName = hose != null ? hose.getClass().getSimpleName() : null;
				String errorMsg = "Error Withdrawing from hose "+hoseName;
				ExceptionHandler.handleException(e, errorMsg);
			} finally {
				hose = null;
				//Thread.currentThread().interrupt();
			}
		}
	}


	protected void sendProtein(Protein protein) {
		try {
			if(protein!=null){
				hose.deposit(protein);
				if ( logger.isDebugEnabled() ) {
					logger.debug("Protein sent. proteinCounter: " + proteinCounter);
				}

				if(logger.isTraceEnabled()){
					logger.trace("protein sent : "+protein);
				}
			} else {
				if ( logger.isDebugEnabled() ) {
					logger.debug("Protein is null");
				}
			}
		} catch (Exception e) {
//			fail("Unable to send protein "+protein);
			ExceptionHandler.handleException(e, "Pool exception : protein not sent "+protein.ingests());
		}
	}

	protected void maybeSleep() {
		sleepMs = ExternalTCPMultiProteinTestConfig.getRandomSleepingTime();
		if ( logger.isDebugEnabled() ) logger.debug("sleep time " + sleepMs);
		if (sleepMs > 0) {
			try {
				Thread.sleep(sleepMs);
			} catch (InterruptedException e) {
				logger.error("Sleep interrupted");
				Thread.currentThread().interrupt();
			}
		}
	}

	/***
	 * Add some extra proteins in the middle
	 * @param i
	 */
	private Protein getNoisyProtein(int i) {
		return Receiver.makeFakeProtein(i, ExternalTCPMultiProteinTest.POOL_NAME);
	}

	public void createAndSendBatch() {
		//no more adding if should stop or max number of proteins required has already been sent
		if(!ExternalTCPMultiProteinTestConfig.shouldTestContinue(proteinCounter, maxProteinNumber)){
			logger.info("No more proteins to send in this round");
			return;
		}
		int batchSize = getBatchSize();
		for(int i=0; i < batchSize; i++){
			Protein protein = Receiver.makeProtein(proteinCounter,
					ExternalTCPMultiProteinTest.POOL_NAME, ARGUMENT_FOR_PROTEIN_MAP,
					Receiver.getTestProteinDescript());
			sendProtein(protein);
			if(i==100 && ExternalTCPMultiProteinTestConfig.MAKE_FAIL ){
				//add protein 2 times : ensure test will fail
				sendProtein(protein);
			}
			if(i % ExternalTCPMultiProteinTestConfig.NOISINESS == 0){
				sendProtein(getNoisyProtein(proteinCounter));
			}
			proteinCounter++;
		}
		logger.info("Sent batch of " + batchSize
				+ " proteins / "+proteinCounter +" total sent protein");
	}

	private int getBatchSize() {
		return ExternalTCPMultiProteinTestConfig.getBatchSize();
	}


	public void startOperations() throws PoolException {
		connect();
		//TODO: fix this
			createAndSendBatch();
			maybeSleep();

	}
}
