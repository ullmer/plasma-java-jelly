package com.oblong.jelly.communication;

import java.util.List;

import com.oblong.jelly.*;
import com.oblong.jelly.util.ExceptionHandler;

public class ObPoolSender extends ObPoolConnector {

	private static final String TAG = "ObPoolSender";
	public static final boolean D = true;

	protected final List<Protein> proteinQueue;


	public ObPoolSender(PoolServerAddress pools,String pool,ObPoolCommunicationEventHandler lis,int sleepSecs,
			List<Protein> proteinQueue, HoseFactory hoseFactory) {
		super(pools, pool, lis, hoseFactory);
		this.sleepMs = sleepSecs;
		this.proteinQueue = proteinQueue;

	}

	protected void handleProtein() {
		Protein protein = getNext();
		if(protein != null){
			sendProtein(protein);
		}

	}

	protected void sendProtein(Protein protein) {
		if(protein != null){
			try {
				hose.deposit(protein);
			} catch (PoolException e) {
				ExceptionHandler.handleException(e);
//				throw new RuntimeException(e);
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

	public void connect() {
		super.connect();
		
		if (hose != null)
		{
			System.out.println(TAG + " : Connection successful! to " + obPool + " id=" + getId());
 		}
		else
		{
			System.err.println(TAG + " : Unable to connect to pool!");
		}
	}



}
