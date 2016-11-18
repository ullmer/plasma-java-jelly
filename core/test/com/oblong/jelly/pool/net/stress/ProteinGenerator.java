package com.oblong.jelly.pool.net.stress;

import com.oblong.jelly.Hose;
import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.slaw.java.SlawString;
import com.oblong.util.probability.ProbabilityHost;
import org.slf4j.*;

import java.util.List;
import java.util.Map;

import static com.oblong.jelly.Slaw.*;

/**
 * Generates proteins to be used for test.
 * Also responsible for verifying incoming proteins, whether they match the generated ones for the same given index.
 *
 * User: karol
 * Date: 10/31/13
 * Time: 8:16 PM
 */
public class ProteinGenerator {

	private static Logger logger = LoggerFactory.getLogger(ProteinGenerator.class);

	public static final String DEFAULT_DESCRIPTS = "protein_number";
	public static final String ARGUMENT_FOR_PROTEIN_MAP = "foo";

	public static final SlawString LAST_PROTEIN_IN_CONNECTION_SESSION = string("last-protein-in-connection-session");
	public static final SlawString CONNECTION_SESSION_CYCLE_ID = string("connection-session-cycle-id");

	final ConnectionSession connectionSession;
	final StressTestJelly test;
	final ProbabilityHost probabilityHost;

	public ProteinGenerator(ConnectionSession connectionSession) {
		this.connectionSession = connectionSession;
		this.test = connectionSession.parentTest;
		this.probabilityHost = test.probabilityHost;
	}

	public Protein makeProtein(long proteinIndex, String poolName, SlawString descrip,
			boolean lastProteinInConnectionSession) {
		final Slaw desc = Slaw.list(int64(proteinIndex),
				descrip,
				string("descrips"),
				string(DEFAULT_DESCRIPTS),
				getDescripsByIndex(proteinIndex),
				map(string(ARGUMENT_FOR_PROTEIN_MAP), nil()));
		final Slaw ingests = map(string("string-key"), string("value"),
				string("nil-key"), nil(),
				string("int64-key"), int64(proteinIndex),
				string("pool-name"), string(poolName),
				LAST_PROTEIN_IN_CONNECTION_SESSION, bool(lastProteinInConnectionSession),
				CONNECTION_SESSION_CYCLE_ID, int64(connectionSession.getCycleId()));

		//how much is enough?
		int randomDataLength = connectionSession.testConfig.qtyRudeDataBytes.random();
		if (randomDataLength > 0) {
			final byte[] data = new byte[randomDataLength];
			probabilityHost.nextBytes(data);

			return protein(desc, ingests, data);
		} else {
			return protein(desc, ingests);
		}

	}

	/** @return is interesting protein (should increment counter) */
	public boolean checkProtein(Protein p, int proteinIndex, Hose hose) {
//		printLogIfRequired(i, 1, " i : "+i+" protein : "+p);
		Map<Slaw, Slaw> ingestsMap = p.ingests().emitMap();
		boolean isProteinFromThisConnectionSession = isProteinFromThisConnSession(ingestsMap);
		long connCycleIdOfReceivedProtein = ingestsMap.get(CONNECTION_SESSION_CYCLE_ID).emitLong();
		if ( !isProteinFromThisConnectionSession ) {
			logger.warn("Ignoring received protein from wrong ConnectionSession cycleId " +
					connCycleIdOfReceivedProtein + " vs current: " + connectionSession.getCycleId() +
					"; cycleId diff: " + (connectionSession.getCycleId() -
					ingestsMap.get(CONNECTION_SESSION_CYCLE_ID).emitLong()));
			return false;
		}
		String errorMessage = "Protein does not match expected descrips." +
				" hose: " + hose +  " proteinIndex: " + proteinIndex + " protein: " + p.toString() +
				"; connection cycleId= "+connectionSession.getCycleId();

		assertTrue(errorMessage,
				p.matches(getDescripsByIndex(proteinIndex)));

		assertTrue(errorMessage,
				checkDescripsContainIndex(p.descrips(), proteinIndex));

		return true;
	}

	private boolean isProteinFromThisConnSession(Map<Slaw, Slaw> ingestsMap) {
		long connSessionIdOfReceivedProtein = ingestsMap.get(CONNECTION_SESSION_CYCLE_ID).emitLong();
		return connSessionIdOfReceivedProtein == connectionSession.getCycleId();
	}

	/** Cannot use JUnit's assertTrue, because it does not throw exception on failure :( */
	public static void assertTrue(String msg, boolean v) {
		if ( !v ) {
			throw new RuntimeException("Assertion failed. Msg: " + msg);
		}
	}

	private boolean checkDescripsContainIndex(Slaw descrips, int i) {
		List<Slaw> list = descrips.emitList();
		for(Slaw s : list){
			if(s.equals(getDescripsByIndex(i)))
				return true;
		}
		return false;
	}


	public SlawString getTestProteinDescrip() {
		return string("test-protein");
	}

	public Slaw getDescripsByIndex(long proteinIndex) {
		return Slaw.string(DEFAULT_DESCRIPTS+proteinIndex);
	}

	public boolean isLastProteinInConnectionSession(Protein protein) {
		Map<Slaw, Slaw> ingestsMap = protein.ingests().emitMap();
		boolean isLast = ingestsMap.get(LAST_PROTEIN_IN_CONNECTION_SESSION).emitBoolean();
		if (! isProteinFromThisConnSession(ingestsMap)  ) {
			logger.debug("Got " + LAST_PROTEIN_IN_CONNECTION_SESSION + " but for wrong connection session; ignoring");
			return false;
		}
		return isLast;
	}

}
