package com.oblong.jelly.pool.net.stress;

import com.oblong.jelly.*;
import com.oblong.jelly.slaw.java.SlawString;
import com.oblong.jelly.util.ExceptionHandler;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.oblong.jelly.Slaw.*;
import static com.oblong.jelly.Slaw.protein;
import static com.oblong.jelly.Slaw.string;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: valeria
 * Date: 10/2/13
 * Time: 12:58 PM
 */
public class Receiver {

	public static final String DEFAULT_DESCRIPTS = "protein_number";

	static final Random random = new Random();
	private static final Logger logger = Logger.getLogger(Receiver.class);
	private final PoolServerAddress address;
	private final Hose receivingHose;


	/***
	 * create HoseTest
	 *
	 *
	 *
	 * @param addr
	 * @param poolName
	 * @throws com.oblong.jelly.PoolException
	 */
	public Receiver(PoolServerAddress addr,
	                String poolName) throws PoolException {
		this.address = addr;
		this.receivingHose = getHoseFromAddress(poolName);
	}

	private Hose getHoseFromAddress(String name) throws PoolException {
		final PoolAddress pa = new PoolAddress(address, name);
		Hose hose = Pool.participate(pa);
		logger.info("Created hose "+name);
		return hose;
	}



	public int getLastExecutedRound() {
		return currentRound;
	}

	volatile int currentRound = 0;

	private Thread awaitNextThread = new Thread("AwaitNextThread") {

		public volatile boolean stopThread = false;

		@Override
		public synchronized void start() {
			stopThread = false;
			super.start();
		}

		public void run() {
			Protein lastSuccessfullyObtainedProtein;
			currentRound = 0;
			int maxNumberOfProteins = ExternalTCPMultiProteinTestConfig.getTotalNumberOfProteins();
			printLogIfRequired(currentRound+1, 1, "Number of expected proteins "+ maxNumberOfProteins);
			while (ExternalTCPMultiProteinTestConfig.shouldTestContinue(currentRound, maxNumberOfProteins) && !stopThread) {
				try {
					Protein tempProtein = receivingHose.awaitNext(ExternalTCPMultiProteinTestConfig.getRandomAwaitTimeout(), TimeUnit.MILLISECONDS);
					if(logger.isTraceEnabled()){
						logger.trace("Protein received : "+tempProtein);
					}
					if (tempProtein!=null) {
						int frequency = 500;
						String textToPrint;
						int frequency2 = 500;
						String textToPrint2 = "We are at protein " + currentRound;
						printLogIfRequired(currentRound, frequency2, textToPrint2);

						if(tempProtein.matches(getTestProteinDescript())){
							checkProtein(tempProtein, currentRound);
							textToPrint = "Protein " + currentRound + " ok";
							currentRound++;
							lastSuccessfullyObtainedProtein = tempProtein;
						} else {
							textToPrint = "Protein doesn't match target descripts";
						}
						printLogIfRequired(currentRound, frequency, textToPrint);
					}
				} catch (TimeoutException e) {
					printNoProteinReceivedYet("Timeout ", currentRound);
					//if timeout we skip this round otherwise we lose descripts field
				} catch (NoSuchProteinException e){
					//No protein found
					printNoProteinReceivedYet("NoSuchProtein ", currentRound);
				} catch (PoolException e){
					stopAndThrow(e, e.kind());
				} catch (Exception e){
					stopAndThrow(e, PoolException.Kind.UNCLASSIFIED);
				}
			}
			//stopped
		}

		private boolean stopAndThrow(Exception e, PoolException.Kind kind) {
			stopThread = true;
			printAndThrow(e, kind);
			return stopThread;
		}
	};



	public void awaitNext() throws PoolException {
		awaitNextThread.start();
	}

	private void printNoProteinReceivedYet(String timeout, int currentRound) {
		int frequency = 1;
		String textToPrint = timeout +", we are waiting for protein " + currentRound;
		printLogIfRequired(currentRound, frequency, textToPrint);
	}

	private void printAndThrow(Exception e, PoolException.Kind kind) {
		ExceptionHandler.handleException(e, "ExternalHose.awaitNext "+kind);
		throw new RuntimeException(e);
	}

	private void printLogIfRequired(int i, int frequency, String textToPrint) {
		if((i % frequency) == 0){
			logger.info(textToPrint);
		} else {
			if ( logger.isDebugEnabled() ) {
				logger.debug(textToPrint);
			}
		}
	}


	public void checkProtein(Protein p, int i) {
//		printLogIfRequired(i, 1, " i : "+i+" protein : "+p);
		//checking if the proteins have the same hose name
		//TODO: maybe useless here
		assertEquals(receivingHose.name(), p.source());
		String errorMessage = "i is: " + i + " " + p.toString();
		assertTrue(errorMessage, p.matches(getDescripsByIndex(i)));
		assertTrue(errorMessage, checkDescriptsContainIndex(p.descrips(), i));
	}

	private boolean checkDescriptsContainIndex(Slaw descrips, int i) {
		List<Slaw> list = descrips.emitList();
		for(Slaw s : list){
			if(s.equals(getDescripsByIndex(i)))
				return true;
		}
		return false;
	}

	public static Protein makeProtein(int i, String hname, String argumentForProteinMap, SlawString testProteinDescript) {
		final Slaw desc = list(int32(i),
				string("descrips"),
				testProteinDescript,
				getDescripsByIndex(i),
				map(string(argumentForProteinMap), nil()));
		final Slaw ingests = map(string("string-key"), string("value"),
				string("nil-key"), nil(),
				string("int64-key"), int64(i),
				string("hose"), string(hname));

		//how much is enough?
		int randomDataLength = ExternalTCPMultiProteinTestConfig.getRandomDataLength();
		if (randomDataLength > 0) {
			final byte[] data = new byte[randomDataLength];
			random.nextBytes(data);
//			for (int j = 0; j < randomDataLength; ++j) {
//              data[j] =
//			}
			return protein(desc, ingests, data);
		} else {
			return protein(desc, ingests);
		}

	}

	public static SlawString getTestProteinDescript() {
		return string("test-protein");
	}

	public static SlawString getTestProteinDescriptForFake() {
		return string("test-protein-fake");
	}

	public static Protein makeFakeProtein(int i, String hname) {
		String argumentForFakeProtein = "fake";
		return makeProtein(i, hname, argumentForFakeProtein, getTestProteinDescriptForFake());
	}

	private static Slaw getDescripsByIndex(int i) {
		return Slaw.string(DEFAULT_DESCRIPTS+i);
	}


//	public void cleanUp() {
//		withdrawFromHose ();
//		removePool ();
//	}

	protected void removePool () {
		try {
			Pool.dispose(receivingHose.poolAddress());
		} catch (PoolException e) {
			ExceptionHandler.handleException(e);
		}
	}

	protected void withdrawFromHose () {
		try {
			receivingHose.withdraw ();
		} catch (Exception e) {
			ExceptionHandler.handleException (e);
		}
	}
}
