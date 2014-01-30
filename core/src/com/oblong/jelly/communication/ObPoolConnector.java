package com.oblong.jelly.communication;


import com.oblong.jelly.*;
import com.oblong.util.ExceptionHandler;
import com.oblong.util.logging.LoggingObject;
import com.oblong.util.logging.ObLog;

public abstract class ObPoolConnector extends Thread implements LoggingObject {

//	private static final String TAG = "ObPoolConnector";
	private static final boolean  D = true;

	private final ObLog logger = ObLog.get(this);

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
		super(ObPoolConnector.class.getName());
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
				operateOnProtein();
			} else {
				reconnectOrNotify();
			}
		}
		logThreadStopped();
	}

	protected void operateOnProtein() {
		handleProtein();
		maybeSleep();
	}

	protected void reconnectOrNotify() {
		if(TRY_RECONNECT){
			reconnect();
		} else {
			notifyConnectionLost(getReasonForHoseError());
		}
	}

	protected void logThreadStopped() {
		logger.debug(this.toString() + " stopped ");
	}

	public String getReasonForHoseError() {
		return "Hose with address "+obPoolsAddr.toString()+" is null or disconnected.";
	}

	protected void reconnect() {
		try {
			PoolAddress address = hose.poolAddress();
			
			if (address != null) {
				if(D) logger.debug(" Will try to reconnect to " + address.toString());
				hose = Pool.participate(address, DEFAULT_POOL_OPTIONS);
				if(D) logger.debug(" Reconnected OK");
			} else {
				if(D) logger.debug("Address is null");
			}
		} catch (PoolException e) {
			if(D)logger.error(" Reconnected KO " + e.getMessage());
		} catch (Exception e) {
			if(D)logger.error("Unable to reconnect ");
			ExceptionHandler.handleException(e);
		}
	}

	public void halt() {
		this.stopMe = true;
		logger.debug("Halting " + this.toString())  ;
		try {
			withdrawHose();
			logger.debug("Halted " + this.toString())  ;
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
			if(D)logger.debug(" Hose is already null.");
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
			if(D)logger.debug(" Hose is null from thread " + this.toString());
			return false;
		} else if (!h.isConnected()) {
			logger.debug(" Hose was disconnected from thread  " + this.toString());
			return false;
		} else {
			return true;
		}
	}
	
	public boolean isConnectedHose(){
		return isHoseOkay(hose);
	}
	
	protected void connect() {
		if(D)logger.debug(" Connecting to pool " + "/" + obPool);
		try {
			initHose();
		} catch (BadAddressException e) {
			logAndNotify(e, UnableToConnectEvent.Reason.BAD_ADDRESS);
		} catch (InvalidOperationException e){
			//this happens when for ex we try to connect to secure pools
			logAndNotify(e, UnableToConnectEvent.Reason.UNSUPPORTED_OPERATION);
		} catch (PoolException e) {
			logAndNotify(e, UnableToConnectEvent.Reason.POOL_EXCEPTION);
		}
		
		if (hose != null) {
			if(D)logger.debug(" Connection successful to " + this.toString());
		} else {
			if(D)logger.debug(" Unable to connect to pool "+obPool);
		}
	}

	private void logAndNotify(Exception e, UnableToConnectEvent.Reason reason) {
		logger.error("connect", e);
		listener.onErrorConnecting(new UnableToConnectEvent(reason));
	}

	protected void initHose() throws PoolException {
		if ( hoseFactory != null ) {
			hose = hoseFactory.createHose(ObPoolConnector.this, obPoolsAddr, obPool);
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

	@Override
	public String toString() {
		return getClass().getName()+"{" +
				"obPoolsAddr=" + obPoolsAddr +
				", obPool='" + obPool + '\'' +
				", hoseFactory=" + hoseFactory +
				"} " + super.toString();
	}
}
