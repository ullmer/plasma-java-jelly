package com.oblong.jelly.pool.net;

import com.oblong.jelly.ExternalHoseTests;
import com.oblong.jelly.Protein;
import com.oblong.jelly.util.ExceptionHandler;
import com.oblong.util.Util;

import java.util.Random;

import static junit.framework.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: valeria
 * Date: 10/22/13
 * Time: 2:31 PM
 */
public class ProteinGenerator extends Thread {

	public static final String ARGUMENT_FOR_PROTEIN_MAP = "foo";
	private final Random r =  new Random();
	private final int sleepMs;
	private final long maxProteinNumber;
	private final long maxProteinBatchSize;
	private int proteinCounter = 0;
	private final JellyTestPoolConnector connector;
	private volatile boolean stopMe = false;


	public ProteinGenerator(JellyTestPoolConnector connector) {
		this.connector = connector;
		this.maxProteinNumber = ExternalTCPMultiProteinTestConfig.numberOfRuns;
		this.maxProteinBatchSize = ExternalTCPMultiProteinTestConfig.NUMBER_OF_DEPOSITED_PROTEINS_IN_BATCH;
		this.sleepMs = ExternalTCPMultiProteinTestConfig.SLEEP_MILI_SECS;
		ExternalTCPMultiProteinTest.logMessage("created generator");
	}

	@Override
	public void run(){
		ExternalTCPMultiProteinTest.logMessage("will start adding proteins");
		while(!stopThread()){
			createAndAddBatch();
			try {
				Util.randomSleep(r, sleepMs);
			} catch (InterruptedException e) {
				stopSelf();
				ExternalTCPMultiProteinTest.logMessage("Thread interrupted");
				Thread.currentThread().interrupt();
//				ExceptionHandler.handleException(e);
				//fail("Error creating new protein(s)");
			}
		}
	}


	public void stopSelf(){
		stopMe = true;
		ExternalTCPMultiProteinTest.logMessage("Thread will be stopped, sent "+proteinCounter+" proteins");
	}

	private boolean stopThread() {
		return (maxProteinNumber == ExternalTCPMultiProteinTestConfig.NO_LIMIT_PROTEIN_NUMBER) ?
				stopMe :
				(proteinCounter >= maxProteinNumber) && stopMe;
	}


	private void createAndAddBatch() {
		for(int i=0; i < maxProteinBatchSize; i++){
			Protein protein;
			protein = ExternalHoseTests.makeProtein(proteinCounter, ExternalTCPMultiProteinTest.POOL_NAME, ARGUMENT_FOR_PROTEIN_MAP);
			connector.addProtein(protein);
			proteinCounter++;
			makeFail(i, protein);
			addNoise(i);
		}
//		ExternalTCPMultiProteinTest.logMessage("All "
//				+ ExternalTCPMultiProteinTestConfig.NUMBER_OF_DEPOSITED_PROTEINS_IN_BATCH
//				+ " Proteins from batch have been sent.");
	}

	/***
	 * Add some extra proteins in the middle
	 * @param i
	 */
	private void addNoise(int i) {
		Protein protein;
		if(i % ExternalTCPMultiProteinTestConfig.NOISINESS == 0){
			protein = ExternalHoseTests.makeFakeProtein(i, ExternalTCPMultiProteinTest.POOL_NAME);
			connector.addProtein(protein);
		}
	}

	private void makeFail(int i, Protein protein) {
		//add protein 2 times : ensure test will fail
		if(i==100 && ExternalTCPMultiProteinTestConfig.MAKE_FAIL ){
			connector.addProtein(protein);
		}

	}

}
