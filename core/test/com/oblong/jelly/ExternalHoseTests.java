package com.oblong.jelly;

import com.oblong.jelly.communication.ObPoolConnector;
import com.oblong.jelly.pool.net.ExternalTCPMultiProteinTestConfig;
import com.oblong.jelly.slaw.java.SlawString;
import com.oblong.jelly.util.ExceptionHandler;

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
public class ExternalHoseTests extends HoseTests {

	public static final String DEFAULT_DESCRIPTS = "protein_number";




	/***
	 * create HoseTest
	 *
	 *
	 * @param addr
	 * @param maxNbOfProteins to handle
	 * @param poolName
	 * @throws PoolException
	 */
	public ExternalHoseTests(PoolServerAddress addr, int maxNbOfProteins, String poolName) throws PoolException {
		super(addr, null, maxNbOfProteins, poolName, ObPoolConnector.DEFAULT_POOL_OPTIONS);
	}

	public class lastProteinData {
		int last;
		Protein p;

	}

	public int getLastExecutedRound() {
		return currentRound;
	}

	volatile int currentRound = 0;

	private Thread awaitNextThread = new Thread() {

		private volatile boolean stopThread = false;



		public void run() {
			Protein lastSuccessfullyObtainedProtein;
			currentRound = 0;
			printLogIfRequired(currentRound+1, 1, "Number of expected proteins "+ getNumberOfExpectedProteins());
			while (ExternalTCPMultiProteinTestConfig.shouldTestContinue(currentRound, maxNumberOfProteins) && !stopThread) {
				try {
					Protein tempProtein = defHose.awaitNext(ExternalTCPMultiProteinTestConfig.getRandomAwaitTimeout(), TimeUnit.MILLISECONDS);
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
						if(ExternalTCPMultiProteinTestConfig.SHOW_LOGS){
							printLogIfRequired(currentRound, frequency, textToPrint);
						}
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



	@Override
	public void awaitNext() throws PoolException {
		awaitNextThread.start();
	}

	private int getNumberOfExpectedProteins() {
		return maxNumberOfProteins;
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
			System.out.println(textToPrint);
		}
	}


	@Override
	public void checkProtein(Protein p, int i) {
//		printLogIfRequired(i, 1, " i : "+i+" protein : "+p);
		//checking if the proteins have the same hose name
		//TODO: maybe useless here
		assertEquals(getTestHoseName(), p.source());
		assertTrue("i is: "+i+" "+p.toString(), p.matches(getDescripsByIndex(i)));
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
			for (int j = 0; j < randomDataLength; ++j) {
				data[j] = (byte)j;
			}
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
//		final Slaw desc = list(int32(i),
//				string("descrips"),
//				map(string("fake"), nil()));
//		final Slaw ings = map(string("string-key"), string("value"),
//				string("nil-key"), nil(),
//				string("int64-key"), int64(i),
//				string("hose"), string(hname));
//		//arbitrary for now
//		//how much is enough?
//		final byte[] data = new byte[ExternalTCPMultiProteinTestConfig.MAX_DATA_LENGTH];
//		for (int j = 0; j < ExternalTCPMultiProteinTestConfig.MAX_DATA_LENGTH; ++j) {
//			data[j] = (byte)j;
//		}
//		return protein(desc, ings, data);
		String argumentForFakeProtein = "fake";
		return makeProtein(i, hname, argumentForFakeProtein, getTestProteinDescriptForFake());
	}

	private static Slaw getDescripsByIndex(int i) {
		return Slaw.string(DEFAULT_DESCRIPTS+i);
	}



}
