package com.oblong.jelly.communication;

import com.oblong.jelly.*;
import com.oblong.jelly.pool.net.Request;
import com.oblong.util.ExceptionHandler;
//import com.oblong.util.logging.ObLog;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.*;

public abstract class ObPoolConnector extends Thread {

	private static final boolean  D = true;

	//private final ObLog logger = ObLog.get(this); //BAU 2024-08-06: punting during bootstrapping phase

	/***
	 * According to javadoc this field should be ignored if the pool already exists
	 */
	public static final PoolOptions DEFAULT_POOL_OPTIONS = PoolOptions.MEDIUM;
	private static final ThreadGroup THREAD_GROUP = new ThreadGroup("ObPoolConnectors");
	private final CountDownLatch countDownOnPoolConnected;

	private volatile boolean running = true;
	private Hose hose;

	private String obPool;
	private PoolServerAddress obPoolsAddr;
	protected ObPoolCommunicationEventHandler listener;
	protected boolean TRY_RECONNECT = false;
	private final HoseFactory hoseFactory;

	/**
	 * @param hose to do work with.
	 * @return true if the connector should be stopped.
	 */
	protected abstract boolean doWork(Hose hose);

	/**
	 *
	 * @param addr
	 * @param pool
	 * @param lis
	 * @param hoseFactory
	 * @param countDownOnPoolConnected The latch will be used to notify another thread about the successful connection (ej. for mezzanine).
	 *                                    Latch can be null and in that case it will be ignored.
	 */
	protected ObPoolConnector(PoolServerAddress addr, String pool, ObPoolCommunicationEventHandler lis, HoseFactory hoseFactory,
	                          CountDownLatch countDownOnPoolConnected) {
		super(THREAD_GROUP, pool);
		this.countDownOnPoolConnected = countDownOnPoolConnected;
//		super();
		setName(getClass().getSimpleName()+"/"+hashCode()+"__"+pool);
		this.obPool = pool;
		this.obPoolsAddr = addr;
		this.hoseFactory = hoseFactory;
		this.listener = lis;
	}

	@Override public void run() {
		connect();
		while (running) {
			if (hose.isConnected()) {
				if (doWork(hose)) {
					running = false;
				}
			} else {
				reconnectOrNotify();
			}
		}

//		logThreadStopped();
//		logger.d("Halting " + this.toString());
		try {
			withdrawHose();
//			logger.d("Halted " + this.toString());
		} catch(NoClassDefFoundError ex){
			//TODO: maybe remove this catch clause
			ExceptionHandler.handleException(ex);
		} catch(Exception e){
			ExceptionHandler.handleException(e);
		}
	}

	protected void reconnectOrNotify() {
		if(TRY_RECONNECT){
			reconnect();
		} else {
			notifyConnectionLost(getReasonForHoseError());
		}
	}

	public String getReasonForHoseError() {
		return "Hose with address "+obPoolsAddr.toString()+" is null or disconnected.";
	}

	protected void reconnect() {
		try {
			PoolAddress address = hose.poolAddress();
			
			if (address != null) {
//				if(D) logger.d(" Will try to reconnect to " + address.toString());
				hose = Pool.participate(address, DEFAULT_POOL_OPTIONS);
//				if(D) logger.d(" Reconnected OK");
			} else {
//				if(D) logger.d("Address is null");
			}
		} catch (PoolException e) {
//			if(D)logger.error(" Reconnected KO " + e.getMessage());
		} catch (Exception e) {
//			if(D)logger.error("Unable to reconnect ");
			ExceptionHandler.handleException(e);
		}
	}

	public void stopConnector() {
		if (running) {
			running = false;
		}
		interrupt();
	}

	public boolean isRunning() {
		return running;
	}

	// -- private --

	private void withdrawHose() {
		if (hose == null) {
//			if(D) logger.d(" Hose is already null.");
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

	protected void connect() {
//		if(D) logger.d(" Connecting to pool " + "/" + obPool);
		try {
			initHose();
		} catch (BadAddressException e) {
			logAndNotify(e, UnableToConnectEvent.Reason.BAD_ADDRESS);
		} catch (TLSException e){
			logAndNotify(e, UnableToConnectEvent.Reason.POOL_REQUIRES_TLS);
		} catch (InvalidOperationException e){
			/**this happens when for ex we try to connect to secure pools or
			 * or if we try to connect to a non secure pool with tcps protocol**
			 */
			if(e.getMessage().contains(Request.STARTTLS.name())){
				logAndNotify(e, UnableToConnectEvent.Reason.CLIENT_REQUIRES_TLS);
			} else {
				logAndNotify(e, UnableToConnectEvent.Reason.UNSUPPORTED_OPERATION);
			}
		} catch (PoolException e) {
			logAndNotify(e, UnableToConnectEvent.Reason.POOL_EXCEPTION);
		}
		
		if (hose != null) {
			onSuccessfulConnection();
		} else {
//			if(D) logger.d(" Unable to connect to pool " + obPool);
		}
	}

	private void onSuccessfulConnection() {
//		if(D) logger.d(" Connection successful to " + this.toString());
		listener.onPoolConnected();
		if(countDownOnPoolConnected != null){
			countDownOnPoolConnected.countDown();
		}
	}

	private void logAndNotify(Exception e, UnableToConnectEvent.Reason reason) {
		//logger.error("connection error, reason : "+reason.name(), e);
		//System.out.println("connection error, reason: " + String(reason.name));
		System.out.println("connection error, reason (update string)");
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
		stopConnector();
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
