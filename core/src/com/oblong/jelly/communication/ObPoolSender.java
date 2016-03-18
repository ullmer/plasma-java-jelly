package com.oblong.jelly.communication;

import com.oblong.jelly.*;
import com.oblong.util.ExceptionHandler;
import com.oblong.util.logging.ObLog;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class ObPoolSender extends ObPoolConnector {
	protected final ObLog log = ObLog.get(this);

	private final BlockingQueue<Protein> outgoingProteinsQueue = new LinkedBlockingQueue();


	public ObPoolSender(PoolServerAddress pools,
	                    String pool,
	                    ObPoolCommunicationEventHandler lis,
	                    HoseFactory hoseFactory,
	                    CountDownLatch countDownOnPoolConnected) {
		super(pools, pool, lis, hoseFactory, countDownOnPoolConnected);
	}

	protected boolean doWork(Hose hose) {
		Protein protein;

		while( (protein = getNext()) != null){
			try {
				hose.deposit(protein);
			} catch (PoolException e) {
				ExceptionHandler.handleException(e);
				return true;
			}
		}
		return false;
	}

	/***
	 * TODO: sometimes this method crashes (very rare but happens)
	 * What will happen if there are 2 ObPoolSenderThreads that want to access this method?
	 */
	private Protein getNext() {
		Protein retProt;
		try {
			retProt = outgoingProteinsQueue.take();
		} catch (InterruptedException e) {
			if(log.d()) log.d("Got interrupted: " + e);
			stopConnector();
			retProt = null;
		}
		//if(log.d()) log.d("getNext: from outgoingProteinsQueue.take(): just descrips(to avoid performance hit): " + ((retProt == null)?null:retProt.descrips()));
		// refrain from logging the whole protein, because it could contain e.g. big image file contents (e.g. bug 12655)
		return retProt;
	}

	public void enqueueForSending(Protein p) {
		try {
			this.outgoingProteinsQueue.put(p); // note: this can wait if there is insufficient space in the queue
		} catch (InterruptedException e) {
			stopConnector();
		}
	}
}
