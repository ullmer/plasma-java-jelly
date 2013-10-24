package com.oblong.jelly.communication;


import com.oblong.jelly.*;
import com.oblong.jelly.util.ExceptionHandler;

public abstract class ObPoolConnector extends Thread {

	private static final String TAG = "ObPoolConnector";
	private static final boolean  D = true;

	/***
	 * According to javadoc this field should be ignored if the pool already exists
	 */
	public static final PoolOptions DEFAULT_POOL_OPTIONS = PoolOptions.MEDIUM;

	// These must be accessible in subclasses.
	protected volatile boolean stopMe = false;
	protected Hose hose;

	protected int sleepMs = 5;
	
	protected String obPool;
	private PoolServerAddress obPoolsAddr;
	protected ObPoolCommunicationEventHandler listener;
	protected boolean TRY_RECONNECT = false;
	private final HoseFactory hoseFactory;

	protected ObPoolConnector(PoolServerAddress addr, String pool, ObPoolCommunicationEventHandler lis, HoseFactory hoseFactory) {
		this.obPool = pool;
		this.obPoolsAddr = addr;
		this.hoseFactory = hoseFactory;
		this.stopMe = false;
		this.listener = lis;
	}

	public void run() {
		connect();
		while (!stopMe) {
			if (isHoseOkay(hose)) {
				handleProtein();
				maybeSleep();
			} else {
				if(TRY_RECONNECT)
					reconnect();
				else notifyConnectionLost(getReasonForHoseError());
			}
		}
		System.out.println(TAG + ": Thread Stopped" + getThreadNameID());
		//withdrawHose();
	}

	public String getReasonForHoseError() {
		return "Hose with address "+obPoolsAddr.toString()+" is null or disconnected ";
	}

	protected void reconnect() {
		try {
			PoolAddress address = hose.poolAddress();
			
			if (address != null) {
				if(D) System.out.println(TAG + ": will try to reconnect to " + address.toString());
				hose = Pool.participate(address, DEFAULT_POOL_OPTIONS);
				if(D) System.out.println(TAG + ": Reconnected OK");
			} else {
				if(D) System.out.println(TAG + "Address is null or hose is null");
			}
		} catch (PoolException e) {
			if(D)System.err.println(TAG + ": Reconnected KO " + e.getMessage());
		}catch (Exception e) {
			ExceptionHandler.handleException(e);
			if(D)System.err.println(TAG + "Unable to reconnect ");
		}

	}

	public void halt() {
		this.stopMe = true;
		System.out.println("Halting " + getThreadNameID())  ;
		try {
			withdrawHose();
			System.out.println("Halted " + getThreadNameID())  ;
		} catch(NoClassDefFoundError ex){
			//TODO: maybe remove this catch clause
			ExceptionHandler.handleException(ex);
		} catch(Exception e){
			ExceptionHandler.handleException(e);
		}
	}

	/****
	 * for sending proteins
	 */
	protected abstract void handleProtein();

	// -- private --

	private void withdrawHose() {
		if (hose == null) {
			if(D)System.out.println(TAG + " : Hose is already null.");
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

	protected void maybeSleep() {
		if (sleepMs > 0) {
			try {
				Thread.sleep(sleepMs);
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}

	protected boolean isHoseOkay(Hose h) {
		if (h == null) {
			if(D)System.out.println(TAG + " : Hose is null from thread id=" + getId());
			return false;
		} else if (!h.isConnected()) {
			System.out.println(TAG + ": Hose was disconnected from thread id= " + getThreadNameID());
			return false;
		} else {
			return true;
		}
	}
	
	public boolean isConnectedHose(){
		return isHoseOkay(hose);
	}
	
	protected void connect() {
		if(D)System.out.println(TAG + ": Connecting to pool " + "/" + obPool);
		try {
			initHose();
		} catch (BadAddressException e) {
			e.printStackTrace();
		} catch (PoolException e) {
			e.printStackTrace();
		}
		
		if (hose != null)
		{
			if(D)System.out.println(TAG + " : Connection successful! id=" + getId());
			
		}
		else
		{
			if(D)System.out.println(TAG + " : Unable to connect to pool!");
		}
	}

	protected void initHose() throws PoolException {
		if ( hoseFactory != null ) {
			hose = hoseFactory.createHose(this, obPoolsAddr, obPool);
		} else {
			PoolAddress address = new PoolAddress(obPoolsAddr, obPool);
			hose = Pool.participate(address, DEFAULT_POOL_OPTIONS);
		}
	}


	/***
	 * instead of reconnecting, we
	 * will notify connectionLost()
	 * after setting stop me to true
	 * ***/
	protected void notifyConnectionLost(String reason) {
		stopMe = true;
		listener.onConnectionLost(reason);
	}

	private String getThreadNameID(){
		return getName()+"-"+getId()+"-"+getState()+"_"+getClass().getName();
	}

}
