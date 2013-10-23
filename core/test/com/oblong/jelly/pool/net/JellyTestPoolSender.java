package com.oblong.jelly.pool.net;

import com.oblong.jelly.ExternalHoseTests;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.Protein;
import com.oblong.jelly.communication.HoseFactory;
import com.oblong.jelly.communication.ObPoolCommunicationEventHandler;
import com.oblong.jelly.communication.ObPoolSender;
import com.oblong.jelly.util.ExceptionHandler;
import com.oblong.util.Util;

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

	private static final String TAG = "JellyTestPoolSender";
	private Random r = new Random();

	//counts the number of "correct" proteins
	private int proteinCounter = 0;
	private final int maxProteinNumber;
	private final int maxProteinBatchSize;
	public static final String ARGUMENT_FOR_PROTEIN_MAP = "foo";

	public JellyTestPoolSender(PoolServerAddress pools,
	                           String pool,
	                           ObPoolCommunicationEventHandler lis,
	                           int sleepSecs,
	                           List<Protein> proteinQueue,
	                           HoseFactory hoseFactory) {
		super(pools, pool, lis, sleepSecs, proteinQueue, hoseFactory);
		this.maxProteinNumber =
				ExternalTCPMultiProteinTestConfig.getTotalNumberOfProteins();
		this.maxProteinBatchSize = ExternalTCPMultiProteinTestConfig.NUMBER_OF_DEPOSITED_PROTEINS_IN_BATCH;
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
		ExternalTCPMultiProteinTest.logMessage("Sender Thread stopped at protein "+proteinCounter);
	}

	@Override
	protected void sendProtein(Protein protein) {
		try {
			super.sendProtein(protein);
			ExternalTCPMultiProteinTest.logMessage("Protein sent ");
		} catch (Exception e) {
//			fail("Unable to send protein "+protein);
			ExceptionHandler.handleException(e, "Pool exception : protein not sent "+protein.ingests());
		}
	}

	protected void maybeSleep() {
		sleepSecs = ExternalTCPMultiProteinTestConfig.getRandomSleepingTime();
		ExternalTCPMultiProteinTest.logMessage("sleep time "+sleepSecs);
		if (sleepSecs > 0) {
			try {
				//TODO: use Karols classes
				Util.randomSleep(r, sleepSecs);
			} catch (InterruptedException e) {
				stopMe = true;
				ExternalTCPMultiProteinTest.logErrorMessage("Sleep interrupted in "+TAG);
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
		ExternalTCPMultiProteinTest.logMessage("Sent batch of "+batchSize+" proteins");
	}

	private int getBatchSize() {
		return maxProteinBatchSize;
	}


}
