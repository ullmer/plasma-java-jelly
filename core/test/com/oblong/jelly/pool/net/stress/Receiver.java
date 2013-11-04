package com.oblong.jelly.pool.net.stress;

import com.oblong.jelly.*;
import com.oblong.jelly.util.ExceptionHandler;
import com.oblong.util.Util;
import net.jcip.annotations.GuardedBy;
import org.apache.log4j.Logger;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.oblong.jelly.Slaw.protein;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: valeria
 * Date: 10/2/13
 * Time: 12:58 PM
 */
public class Receiver extends ConnectionParticipant {

	private static final Logger logger = Logger.getLogger(Receiver.class);

	static final Random random = new Random();

	@GuardedBy("this")
	private boolean finishedReceiving = false;

	@GuardedBy("this")
	private boolean readyToReceive = false;

	public Receiver(ConnectionSession connectionSession) {
		super(connectionSession);
	}

	volatile int qtyProcessedProteins = 0;

	private Thread awaitNextThread = new AwaitNextThread();


	private class AwaitNextThread extends Thread {
		public AwaitNextThread() {
			super("AwaitNextThread");
		}

//		public volatile boolean stopThread = false;

		@Override
		public synchronized void start() {
//			stopThread = false;
			super.start();
		}

		public void run() {

			try {
				logger.info("Started " + getName() + " thread");
				Protein lastSuccessfullyObtainedProtein;
				qtyProcessedProteins = 0;
//			int maxNumberOfProteins = ExternalTCPMultiProteinTestConfig.getTotalNumberOfProteins();
//			printLogIfRequired(currentRound+1, 1, "Number of expected proteins "+ maxNumberOfProteins);
				while (true) {
					try {
						logEveryNth(qtyProcessedProteins, 100, "Before awaitNext(). qtyReceivedProteins= " +
								qtyProcessedProteins);
						setReadyToReceive();
						Protein protein = hose.awaitNext(testConfig.awaitNextTimeout.random(random),
								TimeUnit.MILLISECONDS);
						logger.debug("Protein received. qtyProcessedProteins: "+qtyProcessedProteins);
						if(logger.isTraceEnabled()) logger.trace("Protein received (details) : "+protein);

						if (protein!=null) {
							int frequency = 500;
							int frequency2 = 500;
	//						logEveryNth(qtyReceivedProteins, frequency2, textToPrint2);
							String textToPrint;
							if(protein.matches(proteinGenerator.getTestProteinDescrip())){
								boolean incrementCounter = proteinGenerator.checkProtein(protein, qtyProcessedProteins, hose);
								textToPrint = "Protein no. " + qtyProcessedProteins + " received ok";
								if ( incrementCounter ) {
									qtyProcessedProteins++;
									parentConnectionSession.parentTest.incrementQtyReceivedProteins();
								}
								lastSuccessfullyObtainedProtein = protein;
							} else {
								textToPrint = "Protein doesn't match target descrips";
							}
							logEveryNth(qtyProcessedProteins, frequency, textToPrint);

							if ( proteinGenerator.isLastProteinInConnectionSession(protein) ) {
								logger.info("Received last protein in connection session. Not awaitNext-ing more.");
								break;
							}
						}
						testConfig.sleepBetweenAwaitNext.random(random);

					} catch (TimeoutException e) {
						printNoProteinReceivedYet("Timeout ", qtyProcessedProteins);
						//if timeout we skip this round otherwise we lose descripts field
					} catch (NoSuchProteinException e){
						//No protein found
						printNoProteinReceivedYet("NoSuchProtein ", qtyProcessedProteins);
					} catch (PoolException e){
						throw Util.rethrow(e);
					}
				}
				withdrawHose();
				//stopped
			} catch (Throwable throwable ) {
				logger.fatal("Receiver - uncaught throwable; connection cycleId: " +
						parentConnectionSession.getCycleId(),
						throwable);
			} finally {
				setFinishedReceiving();
			}
		}

//		private void stopAndThrow(Exception e, PoolException.Kind kind) {
////			stopThread = true;
//			printAndThrow(e, kind);
////			return stopThread;
//		}
	}

	private void setReadyToReceive() {
		if ( readyToReceive ) {
			return;
		}
		synchronized(Receiver.this) {
			readyToReceive = true;
			logger.info("setReadyToReceive - will notify");
			this.notifyAll();
		}
	}

	private void setFinishedReceiving() {
		logger.debug("Finished receiving");
		synchronized(Receiver.this) {
			finishedReceiving = true;
			this.notifyAll();
			logger.info("Finished receiving - notified");
		}
	}

	public void waitTillFinishedReceiving() {
		logger.debug("waitTillFinishedReceiving");
		synchronized ( Receiver.this ) {
			while ( ! finishedReceiving ) {
				try {
					Receiver.this.wait();
				} catch (InterruptedException e) {
					throw Util.rethrow(e);
				}
			}
			logger.info("Finished waitTillFinishedReceiving");
		}
	}

	public void waitTillReadyToReceive() {
		logger.debug("waitTillReadyToReceive");
		synchronized ( Receiver.this ) {
			while ( ! readyToReceive ) {
				try {
					Receiver.this.wait();
				} catch (InterruptedException e) {
					throw Util.rethrow(e);
				}
			}
			logger.info("Finished waitTillReadyToReceive");
		}
	}


	public void startThreadWithAwaitNext() {
		initHose();
		awaitNextThread.start();
	}

	private void printNoProteinReceivedYet(String timeout, int currentRound) {
		int frequency = 1;
		String textToPrint = timeout +", we are waiting for protein " + currentRound;
		logEveryNth(currentRound, frequency, textToPrint);
	}

//	private void printAndThrow(Exception e, PoolException.Kind kind) {
//		ExceptionHandler.handleException(e, "ExternalHose.awaitNext "+kind);
//		throw new RuntimeException(e);
//	}

	private void logEveryNth(int i, int frequency, String textToPrint) {
		if ((i % frequency) == 0) {
			logger.info(textToPrint);
		} else {
			if ( logger.isDebugEnabled() ) {
				logger.debug(textToPrint);
			}
		}
	}

//	public void cleanUp() {
//		withdrawFromHose ();
//		removePool ();
//	}

	public boolean isFinishedReceiving() {
		synchronized (Receiver.this) {
			return this.finishedReceiving;
		}
	}


}
