package com.oblong.jelly.pool.net.stress;

import com.oblong.jelly.Protein;
import com.oblong.util.probability.ProbabilityHost;
import org.slf4j.*;

/**
 * Created with IntelliJ IDEA.
 * User: valeria
 * Date: 10/3/13
 * Time: 5:54 PM
 */
public class Sender extends ConnectionParticipant {

	private static final Logger logger = LoggerFactory.getLogger(Sender.class);

	final StressTestJelly test;
	final ProbabilityHost probabilityHost;
	final ProteinGenerator proteinGenerator;
	final Receiver receiver;

	long qtySentProteins = 0;

	public Sender(ConnectionSession connectionSession) {
		super(connectionSession);

		this.test = connectionSession.parentTest;
		this.probabilityHost = test.probabilityHost;
		this.proteinGenerator = connectionSession.proteinGenerator;
		this.receiver = connectionSession.receiver;

		if ( logger.isDebugEnabled() ) {
			logger.debug("Created Sender for max " +
					testConfig.qtyBatchesBeforeDisconnect.getMaxInclusive() + " batches of max " +
					testConfig.qtyProteinsInBatch.getMaxInclusive() + " proteins/batch (max " +
					testConfig.getMaxPossibleQtyProteinsPerConnectionSession() +
					" possible proteins per connection session)");
		}
	}


	protected void connect() {
//		logger.info("Sender connecting to pool " + connectionSession.);
		initHose();
	}

	protected void sendProtein(Protein protein) {
		try {
			if(protein!=null) {
				if ( logger.isDebugEnabled() ) {
					logger.debug("Will send protein. qtySentProteins: " + qtySentProteins);
				}
				hose.deposit(protein);
				qtySentProteins++;

				if ( logger.isDebugEnabled() ) {
					logger.debug("Protein sent. qtySentProteins: " + qtySentProteins);
				}

				if(logger.isTraceEnabled()){
					logger.trace("Protein sent (details) : "+protein);
				}
			} else {
				logger.error("Protein is null");
			}
		} catch (Exception e) {
			throw new RuntimeException("Unable to send protein: " + protein, e);
		}
	}

	/** @return whether a premature stop occured */
	public boolean createAndSendBatch(boolean lastBatch) {
		int batchSize = testConfig.qtyProteinsInBatch.random();
		logger.debug("Will send batch of " + batchSize + " proteins");
		for(int iProtein=0; iProtein < batchSize; iProtein++) {
			if ( checkReceiverFinishedPrematurely() ) {
				return true;
			}
			boolean isLastProteinInConnectionSession = lastBatch && iProtein == batchSize - 1;
			Protein protein = proteinGenerator.makeProtein(qtySentProteins,
					test.poolName, proteinGenerator.getTestProteinDescrip(), isLastProteinInConnectionSession);
			testConfig.sleepBetweenProteinsInBatch.sleep();
			sendProtein(protein);
			if ( isLastProteinInConnectionSession ) {
				logger.info("Sent last protein in connection session");
			}
			if(iProtein==5 && TestConfig.MAKE_FAIL ){
				logger.error("!!!!! Simulating failure in the test: adding protein 2 times !!!!!");
				sendProtein(protein);
			}
//			if(iProtein % ExternalTCPMultiProteinTestConfig.NOISINESS == 0){
//				sendProtein(getNoisyProtein(proteinCounter));
//			}
		}
		logger.info("Sent batch of " + batchSize
				+ " proteins. qtySentProteins: "+ qtySentProteins);
		return false;
	}

	private boolean checkReceiverFinishedPrematurely() {
		boolean finishedReceiving = receiver.isFinishedReceiving();
		if (finishedReceiving) {
			logger.warn("Receiver finished receiving prematurely. " +
					"Sender will stop sending as well, because there is no point sending if no-one will receive it.");
		}
		return finishedReceiving;
	}

	public void sendAll() {
		connect();

		sendAllProteinBatches();
		withdrawHose();
	}

	private void sendAllProteinBatches() {
		int qtyBatches = testConfig.qtyBatchesBeforeDisconnect.random();
		receiver.waitTillReadyToReceive();
		logger.info("Will send " + qtyBatches + " protein batches");
		for ( int iBatch = 0; iBatch < qtyBatches; iBatch ++ ) {
			testConfig.sleepBetweenProteinBatches.sleep();
			boolean prematureStop = createAndSendBatch(iBatch == qtyBatches - 1);
			if ( prematureStop ) {
				break;
			}
		}
	}

}
