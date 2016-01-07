package com.oblong.jelly.communication;

import com.oblong.jelly.*;
import com.oblong.util.*;
import com.oblong.util.logging.*;

import java.util.concurrent.*;

public class ObPoolReceiver extends ObPoolConnector {

	private final ObLog log = ObLog.get(this);

	public ObPoolReceiver(PoolServerAddress serverAddress, String pool, ObPoolCommunicationEventHandler lis, HoseFactory hoseFactory,
	               CountDownLatch countDownOnPoolConnected) {
		super(serverAddress, pool, lis, hoseFactory, countDownOnPoolConnected);
	}

	protected boolean doWork(Hose hose) {
		try {
			Protein p = hose.awaitNext(60, TimeUnit.SECONDS);
			if (isRunning() && p != null) {
				listener.onProteinReceived(p);
			}
		} catch (TimeoutException te) {
			// Do nothing.
		} catch (PoolException e1) {
			getMessageAndNotify(e1);
			// The connector should be stopped.
			return true;
		}
		return false;
	}

	private void getMessageAndNotify(PoolException e1) {
		String reason = getClass().getSimpleName() + " PoolException : message { " + e1.getMessage() + " } ";
		String tagMessage = "hose.awaitNext, exception=" + e1.kind();
		logHandleNotify(e1, reason, tagMessage);
	}

	private void logHandleNotify(Throwable e1, String logMessage, String tagMessage) {
		/**
		 * When in demo mode, stack trace does no get logged but we need it now that we have bug 12132 and 11785
		 */
		log.e(e1, logMessage);
		ExceptionHandler.handleException(e1, tagMessage);
		notifyConnectionLost(logMessage);
	}
}
