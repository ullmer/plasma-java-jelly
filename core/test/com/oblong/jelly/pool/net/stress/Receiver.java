package com.oblong.jelly.pool.net.stress;

import com.oblong.jelly.NoSuchProteinException;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.Protein;
import com.oblong.util.Util;
import net.jcip.annotations.GuardedBy;
import org.slf4j.*;

import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Created with IntelliJ IDEA.
 * User: valeria
 * Date: 10/2/13
 * Time: 12:58 PM
 */
public class Receiver extends ConnectionParticipant {

	private static final Logger logger = LoggerFactory.getLogger(Receiver.class);

	@GuardedBy("this")
	private boolean finishedReceiving = false;

	@GuardedBy("this")
	private boolean readyToReceive = false;

	public Receiver(ConnectionSession connectionSession) {
		super(connectionSession);
	}

	volatile int qtyProcessedReceivedProteins = 0;

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
				qtyProcessedReceivedProteins = 0;
//			int maxNumberOfProteins = ExternalTCPMultiProteinTestConfig.getTotalNumberOfProteins();
//			printLogIfRequired(currentRound+1, 1, "Number of expected proteins "+ maxNumberOfProteins);
				while (true) {
					try {
						logEveryNth(qtyProcessedReceivedProteins, 100, "Before awaitNext(). qtyProcessedReceivedProteins= " +
								qtyProcessedReceivedProteins);
						setReadyToReceive();
						Protein protein = hose.awaitNext(testConfig.awaitNextTimeout.random(), MILLISECONDS);
						logger.debug("Protein received. qtyProcessedReceivedProteins: "+ qtyProcessedReceivedProteins);
						if(logger.isTraceEnabled()) logger.trace("Protein received (details) : "+protein);

						if (protein!=null) {
							int frequency = 500;
							int frequency2 = 500;
	//						logEveryNth(qtyReceivedProteins, frequency2, textToPrint2);
							String textToPrint;
							if(protein.matches(proteinGenerator.getTestProteinDescrip())){
								boolean incrementCounter = proteinGenerator.checkProtein(protein, qtyProcessedReceivedProteins, hose);
								textToPrint = "Protein no. " + qtyProcessedReceivedProteins + " received ok";
								if ( incrementCounter ) {
									qtyProcessedReceivedProteins++;
									parentConnectionSession.parentTest.incrementQtyReceivedProteins();
								}
								lastSuccessfullyObtainedProtein = protein;
							} else {
								textToPrint = "Protein doesn't match target descrips";
							}
							logEveryNth(qtyProcessedReceivedProteins, frequency, textToPrint);

							if ( proteinGenerator.isLastProteinInConnectionSession(protein) ) {
								logger.info("Received last protein in connection session. Not awaitNext-ing more.");
								break;
							}
						}
						testConfig.sleepBetweenAwaitNext.random();

					} catch (TimeoutException e) {
						printNoProteinReceivedYet("Timeout ", qtyProcessedReceivedProteins);
						//if timeout we skip this round otherwise we lose descripts field
					} catch (NoSuchProteinException e){
						//No protein found
						printNoProteinReceivedYet("NoSuchProtein ", qtyProcessedReceivedProteins);
					} catch (PoolException e){
						throw Util.rethrow(e);
					}
				}
				withdrawHose();
				//stopped
			} catch (Throwable throwable ) {
				logger.error("Receiver - uncaught throwable; connection cycleId: " +
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
