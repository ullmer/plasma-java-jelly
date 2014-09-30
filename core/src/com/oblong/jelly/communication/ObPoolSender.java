package com.oblong.jelly.communication;

import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.Protein;
import com.oblong.util.ExceptionHandler;
import com.oblong.util.logging.ObLog;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class ObPoolSender extends ObPoolConnector {
	protected final ObLog log = ObLog.get(this);

	public static final boolean D = true;

//	protected final List<Protein> proteinQueue;

	protected final BlockingQueue<Protein> outgoingProteinsQueue = new LinkedBlockingQueue<Protein>();


	public ObPoolSender(PoolServerAddress pools,
	                    String pool,
	                    ObPoolCommunicationEventHandler lis,
	                    HoseFactory hoseFactory,
	                    CountDownLatch countDownOnPoolConnected) {
		super(pools, pool, lis, hoseFactory, countDownOnPoolConnected);
	}

	protected void handleProtein() {
		Protein protein;
		/**
		 * Get all proteins available,
		 * then sleep
		 */
		while( (protein = getNext()) != null){
			sendProtein(protein);
		}

	}

	@Override protected void maybeSleep() {
		// no need to sleep, as we use blocking operations of the send queue
	}

	protected void sendProtein(Protein protein) {
		if(protein != null){
			try {
				hose.deposit(protein);
			} catch (PoolException e) {
				ExceptionHandler.handleException(e);
			}
		}
	}

	/***
	 * TODO: sometimes this method crashes (very rare but happens)
	 * What will happen if there are 2 ObPoolSenderThreads that want to access this method?
	 */
	protected Protein getNext() {
		Protein retProt;
		try {
			retProt = outgoingProteinsQueue.take();
		} catch (InterruptedException e) {
			if(log.d()) log.d("Got interrupted: " + e);
			setStopMe();
			retProt = null;
		}
		if(log.d()) log.d("getNext: from outgoingProteinsQueue.take(): " + retProt);
		return retProt;
	}

	public void enqueueForSending(Protein p) {
		try {
			this.outgoingProteinsQueue.put(p); // note: this can wait if there is insufficient space in the queue
		} catch (InterruptedException e) {
			setStopMe();
		}
	}

//	@Override public String getLoggerName() {
//		return super.toString();
//	}

}
