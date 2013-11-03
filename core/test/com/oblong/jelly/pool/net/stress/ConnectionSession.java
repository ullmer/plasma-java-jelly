package com.oblong.jelly.pool.net.stress;

import com.oblong.jelly.*;
import com.oblong.util.Util;
import net.jcip.annotations.GuardedBy;
import org.apache.log4j.Logger;


/**
 * User: karol
 * Date: 10/30/13
 * Time: 12:20 PM
 */
public class ConnectionSession {

	private static final Logger logger = Logger.getLogger(ConnectionSession.class);

	public static final PoolOptions POOL_OPTIONS = PoolOptions.MEDIUM;

	final ExternalStressTest parentTest;

	private final long cycleId;

	final TestConfig testConfig;

	final ProteinGenerator proteinGenerator;

	final Receiver receiver;
	private final Sender sender;

	public ConnectionSession(ExternalStressTest parentTest, long cycleId) {
		this.parentTest = parentTest;
		this.cycleId = cycleId;
		this.proteinGenerator = new ProteinGenerator(this);
		this.testConfig = parentTest.testConfig;
		this.receiver = new Receiver(this);
		this.sender = new Sender(this);
	}


	/** Entry point starting the connection session */
	public void execute() {
		logger.info("Executing ConnectionSession; cycleId= " + cycleId + ", poolAddress=" + parentTest.poolAddress);
		createPoolIfNonExisting(parentTest.poolAddress);
		receiver.startThreadWithAwaitNext();
		sender.sendAll();
		receiver.waitTillFinishedReceiving();


//		stop();
	}

	static void createPoolIfNonExisting(PoolAddress poolAddress) {
		try {
			if(!Pool.exists(poolAddress)){
				Pool.create(poolAddress, POOL_OPTIONS);
			}
		} catch (PoolException e) {
			Util.rethrow(e);
		}
	}

	public Hose createHose(ConnectionParticipant forWhom) {
		logger.debug("Creating hose for " + forWhom);
		Hose hose = null;
		try {
			hose = Pool.participate(parentTest.poolAddress);
			if (hose != null) {
				logger.info("Created hose " + hose + " for pool " + parentTest.poolAddress + " for " + forWhom);
				hose.setName("Hose_"+forWhom);
				return hose;
			} else {
				throw new RuntimeException("Hose is null");
			}
		} catch (PoolException e) { throw Util.rethrow(e); }
	}

//	public void stop() {
//		logger.fatal("Not yet implemented");
//	}


	protected void removePool () {
		try {
			Pool.dispose(parentTest.poolAddress);
		} catch (PoolException e) {
			throw Util.rethrow(e);
		}
	}

	public long getCycleId() {
		return cycleId;
	}

}
