package com.oblong.jelly;

import com.oblong.jelly.communication.ObPoolConnector;
import com.oblong.jelly.pool.net.TCPMultiProteinTestConfig;
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

	public Protein getLastObtained() {
		return lastObtained;
	}

	private Protein lastObtained;

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


	@Override
	public void awaitNext() throws PoolException {
		int i=0;
		while (i < maxNumberOfProteins) {
			currentRound = i;
			try {
				lastObtained = defHose.awaitNext(TCPMultiProteinTestConfig.DEFAULT_AWAIT_TIMEOUT, TimeUnit.MILLISECONDS);
				int frequency = 5000;
				String textToPrint;
				if(lastObtained.matches(getTestProteinDescript())){
					checkProtein(lastObtained, i);
					textToPrint = "Protein " + i + " ok";
					i++;
				} else {
					textToPrint = "Protein doesn't match target descripts";
				}
				if(TCPMultiProteinTestConfig.SHOW_LOGS){
					printLogIfRequired(i, frequency, textToPrint);
				}
			} catch (TimeoutException e) {
				int frequency = 1000;
				String textToPrint = "Timeout " + i;
				printLogIfRequired(i, frequency, textToPrint);
				//if timeout we skip this round otherwise we lose descripts field
			} catch (PoolException e){
				printAndThrow(e, e.kind());
			} catch (Exception e){
				printAndThrow(e, PoolException.Kind.UNCLASSIFIED);
			}
		}
	}

	private void printAndThrow(Exception e, PoolException.Kind kind) throws PoolException {
		ExceptionHandler.handleException(e, "ExternalHose.awaitNext");
		throw new PoolException(kind, e);
	}

	private void printLogIfRequired(int i, int frequency, String textToPrint) {
		if((i % frequency) == 0){
			System.out.println(textToPrint);
		}
	}


	@Override
	public void checkProtein(Protein p, int i) {
		//checking if the proteins have the same hose name
		//TODO: maybe useless here
		assertEquals(getTestHoseName(), p.source());
		assertTrue(p.matches(getDescripsByIndex(i)));
	}

	// FIXME: copy-paste with makeFakeProtein??
	public static Protein makeProtein(int i, String hname) {
		final Slaw desc = list(int32(i),
				string("descrips"),
				getTestProteinDescript(),
				getDescripsByIndex(i),
				map(string("foo" /* TODO: should be a method param to avoid copy-pasted method */), nil()));
		final Slaw ings = map(string("string-key"), string("value"),
				string("nil-key"), nil(),
				string("int64-key"), int64(i),
				string("hose"), string(hname));
		//arbitrary for now
		//how much is enough?
		final byte[] data = new byte[TCPMultiProteinTestConfig.MAX_DATA_LENGH];
		for (int j = 0; j < TCPMultiProteinTestConfig.MAX_DATA_LENGH; ++j) {
			data[j] = (byte)j;
		}
		return protein(desc, ings, data);
	}

	private static SlawString getTestProteinDescript() {
		return string("test-protein");
	}

	public static Protein makeFakeProtein(int i, String hname) {
//		int randomInt = new Random().nextInt(i);
		final Slaw desc = list(int32(i),
				string("descrips"),
				map(string("fake" /* TODO: should be a method param to avoid copy-pasted method */), nil()));
		final Slaw ings = map(string("string-key"), string("value"),
				string("nil-key"), nil(),
				string("int64-key"), int64(i),
				string("hose"), string(hname));
		//arbitrary for now
		//how much is enough?
		final byte[] data = new byte[TCPMultiProteinTestConfig.MAX_DATA_LENGH];
		for (int j = 0; j < TCPMultiProteinTestConfig.MAX_DATA_LENGH; ++j) {
			data[j] = (byte)j;
		}
		return protein(desc, ings, data);
	}

	private static Slaw getDescripsByIndex(int i) {
		return Slaw.string(DEFAULT_DESCRIPTS+i);
	}
}
