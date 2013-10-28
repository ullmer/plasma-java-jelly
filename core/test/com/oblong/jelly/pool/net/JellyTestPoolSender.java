package com.oblong.jelly.pool.net;

import com.oblong.jelly.ExternalHoseTests;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.Protein;
import com.oblong.jelly.communication.HoseFactory;
import com.oblong.jelly.communication.ObPoolCommunicationEventHandler;
import com.oblong.jelly.communication.ObPoolSender;
import com.oblong.jelly.util.ExceptionHandler;
import com.oblong.util.Util;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Random;

import static junit.framework.Assert.fail;

/**
* Created with IntelliJ IDEA.
* User: valeria
* Date: 10/3/13
* Time: 5:54 PM
*/
public class JellyTestPoolSender extends ObPoolSender {

	private static final Logger logger = Logger.getLogger(JellyTestPoolSender.class);


	private static final String TAG = "JellyTestPoolSender";
	private static final int IGNORED_VALUE = -1;
	private Random r = new Random();

	//counts the number of "correct" proteins
	private int proteinCounter = 0;
	private final int maxProteinNumber;
	private final int maxProteinBatchSize;
	public static final String ARGUMENT_FOR_PROTEIN_MAP = "foo";
	public static final boolean D = false;

	public JellyTestPoolSender(PoolServerAddress pools,
	                           String pool,
	                           ObPoolCommunicationEventHandler lis,
	                           List<Protein> proteinQueue,
	                           HoseFactory hoseFactory) {
		super(pools, pool, lis, IGNORED_VALUE, proteinQueue, hoseFactory);
		this.maxProteinNumber =
				ExternalTCPMultiProteinTestConfig.getTotalNumberOfProteins();
		this.maxProteinBatchSize = ExternalTCPMultiProteinTestConfig.getBatchSize();
	}


	@Override
	protected void handleProtein() {
	    handleOneOrMultipleProteins();
	}

	private void stopIfNeeded() {
		if(proteinCounter >= maxProteinNumber){
			halt();
		}
	}

	@Override
	public void halt() {
		super.halt();
		logger.warn("Sender Thread stopped at protein " + proteinCounter);
	}

	@Override
	protected void sendProtein(Protein protein) {
		try {
			super.sendProtein(protein);
			if ( logger.isDebugEnabled() ) {
				logger.debug("Protein sent. proteinCounter: " + proteinCounter);
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
				stopMe = true;
				logger.error("Sleep interrupted in " + TAG);
				Thread.currentThread().interrupt();
			}
		}
	}

	/***
	 * Add some extra proteins in the middle
	 * @param i
	 */
	private Protein getNoisyProtein(int i) {
		return ExternalHoseTests.makeFakeProtein(i, ExternalTCPMultiProteinTest.POOL_NAME);
	}

	private void handleOneOrMultipleProteins() {
		//no more adding if should stop or max number of proteins required has already been sent
		if(stopMe || !ExternalTCPMultiProteinTestConfig.shouldTestContinue(proteinCounter, maxProteinNumber)){
			if(stopMe){
				logger.info("Thread should be stopped");
			} else {
				logger.info("No more proteins to send in this round");
			}
			return;
		}
		int batchSize = getBatchSize();
		for(int i=0; i < batchSize; i++){
			Protein protein = ExternalHoseTests.makeProtein(proteinCounter,
					ExternalTCPMultiProteinTest.POOL_NAME, ARGUMENT_FOR_PROTEIN_MAP,
					ExternalHoseTests.getTestProteinDescript());
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


}
