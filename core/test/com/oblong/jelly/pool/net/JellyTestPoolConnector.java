package com.oblong.jelly.pool.net;

import com.oblong.jelly.ExternalHoseTests;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.Protein;
import com.oblong.jelly.communication.HoseFactory;
import com.oblong.jelly.communication.ObPoolCommunicationEventHandler;
import com.oblong.jelly.communication.ObPoolSender;
import com.oblong.jelly.util.ExceptionHandler;

import java.util.List;
import java.util.Random;

/**
* Created with IntelliJ IDEA.
* User: valeria
* Date: 10/3/13
* Time: 5:54 PM
*/
public class JellyTestPoolConnector extends ObPoolSender {

	private Random r = new Random();
	private static boolean realProteins;


	public JellyTestPoolConnector(PoolServerAddress pools,
	                              String pool,
	                              ObPoolCommunicationEventHandler lis,
	                              int sleepSecs,
	                              List<Protein> proteinQueue,
	                              HoseFactory hoseFactory, boolean realProteins) {
		super(pools, pool, lis, sleepSecs, proteinQueue, hoseFactory);
		this.realProteins = realProteins;
	}

	@Override
	public void connect() {
		super.connect();
		createAndSendProteins();
	}

	private void createAndSendProteins() {
		for(int i=0; i < TCPMultiProteinTestConfig.NUMBER_OF_DEPOSITED_PROTEINS; i++){
			Protein protein;
			protein = ExternalHoseTests.makeProtein(i, ExternalTCPMultiProteinTest.POOL_NAME);
			proteinQueue.add(protein);

			makeFail(i, protein);
			addNoise(i);
		}
		ExternalTCPMultiProteinTest.logMessage("All Proteins have been sent");
	}

	/***
	 * add some extra proteins in the middle
	 * @param i
	 */
	private void addNoise(int i) {
		Protein protein;
		if(i % TCPMultiProteinTestConfig.NOISINESS == 0){
			protein = ExternalHoseTests.makeFakeProtein(i, ExternalTCPMultiProteinTest.POOL_NAME);
			proteinQueue.add(protein);
		}
	}

	private void makeFail(int i, Protein protein) {

		//add protein 2 times : ensure test will fail
		if(i==100 && TCPMultiProteinTestConfig.MAKE_FAIL ){
			proteinQueue.add(protein);
		}

	}

	protected void sendProtein(Protein protein) {
//			logMessage("Sending protein "+protein);
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
