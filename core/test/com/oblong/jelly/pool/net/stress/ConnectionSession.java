package com.oblong.jelly.pool.net.stress;

import com.oblong.jelly.*;
import com.oblong.util.Util;
import org.apache.log4j.Logger;

import java.util.Random;


/**
 * User: karol
 * Date: 10/30/13
 * Time: 12:20 PM
 */
public class ConnectionSession {

	private static final Logger logger = Logger.getLogger(ConnectionSession.class);

	public static final PoolOptions POOL_OPTIONS = PoolOptions.MEDIUM;

	final StressTestJelly parentTest;

	final Random random;

	private final long cycleId;

	final TestConfig testConfig;

	final ProteinGenerator proteinGenerator;

	final Receiver receiver;
	private final Sender sender;

	public ConnectionSession(StressTestJelly parentTest, long cycleId) {
		this.parentTest = parentTest;
		this.random = parentTest.random;
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

	void createPoolIfNonExisting(PoolAddress poolAddress) {
		try {
			if ( testConfig.chanceOfRemovingPool.randomBool(random) ) {
				removePool();
				testConfig.sleepAfterRemovingPool.sleep(random);
			}
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
			PoolAddress poolAddress = parentTest.poolAddress;
			logger.info("Removing pool " + poolAddress);
			Pool.dispose(poolAddress);
		} catch (PoolException e) {
			throw Util.rethrow(e);
		}
	}

	public long getCycleId() {
		return cycleId;
	}

}
