package com.oblong.jelly.communication;

import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.Protein;
import com.oblong.util.ExceptionHandler;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ObPoolSender extends ObPoolConnector {

	private static final String TAG = "ObPoolSender";
	public static final boolean D = true;

	protected final List<Protein> proteinQueue;


	public ObPoolSender(PoolServerAddress pools, String pool, ObPoolCommunicationEventHandler lis, int sleepSecs,
	                    List<Protein> proteinQueue, HoseFactory hoseFactory, CountDownLatch countDownOnPoolConnected) {
		super(pools, pool, lis, hoseFactory, countDownOnPoolConnected);
		this.sleepMs = sleepSecs;
		this.proteinQueue = proteinQueue;

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
	 * @return
	 */
	protected Protein getNext() {
		Protein remove = null;
		synchronized (proteinQueue){
			if((proteinQueue!=null && proteinQueue.size() > 0))
				remove = proteinQueue.remove(0);
		}
		return remove;
	}

//	@Override public String getLoggerName() {
//		return super.toString();
//	}

}
