package com.oblong.jelly.pool.net;

import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.Protein;
import com.oblong.jelly.communication.HoseFactory;
import com.oblong.jelly.communication.ObPoolCommunicationEventHandler;
import com.oblong.jelly.communication.ObPoolSender;
import com.oblong.util.Util;
import net.jcip.annotations.ThreadSafe;

import java.util.List;
import java.util.Random;

import static junit.framework.Assert.fail;

/**
* Created with IntelliJ IDEA.
* User: valeria
* Date: 10/3/13
* Time: 5:54 PM
*/
public class JellyTestPoolConnector extends ObPoolSender {

	private static final String TAG = "JellyTestPoolConnector";
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


	protected void addProtein(Protein p){
		synchronized (proteinQueue){
			proteinQueue.add(p);
		}
	}

	protected void sendProtein(Protein protein) {
		try {
			super.sendProtein(protein);
//			ExternalTCPMultiProteinTest.logMessage("Protein sent");
		} catch (Exception e) {
			fail("Unable to send protein "+protein);
//			ExceptionHandler.handleException(e, "Pool exception");
		}
	}

	protected void maybeSleep() {
		if (sleepSecs > 0) {
			try {
				//TODO: use Karols classes
				Util.randomSleep(r, sleepSecs);
			} catch (InterruptedException e) {
				ExternalTCPMultiProteinTest.logMessage("Sleep interrupted in "+TAG);
				Thread.currentThread().interrupt();
			}
		}
	}


}
