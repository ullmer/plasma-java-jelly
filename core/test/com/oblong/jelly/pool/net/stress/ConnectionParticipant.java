package com.oblong.jelly.pool.net.stress;

import com.oblong.jelly.Hose;
import com.oblong.jelly.util.ExceptionHandler;
import org.apache.log4j.Logger;

/**
 * User: karol
 * Date: 10/31/13
 * Time: 6:56 PM
 */
public class ConnectionParticipant {

	private static final Logger logger = Logger.getLogger(ConnectionParticipant.class);

	protected final ConnectionSession parentConnectionSession;
	protected final ProteinGenerator proteinGenerator;
	protected final TestConfig testConfig;
	Hose hose;



	public ConnectionParticipant(ConnectionSession parentConnectionSession) {
		this.parentConnectionSession = parentConnectionSession;
		this.proteinGenerator = parentConnectionSession.proteinGenerator;
		this.testConfig = parentConnectionSession.testConfig;
	}

	protected void initHose() {
		if ( hose != null ) {
			throw new IllegalStateException("Hose already non-null: " + hose);
		}
		this.hose = parentConnectionSession.createHose(this);
	}

	protected void withdrawHose() {
		if (hose == null) {
			logger.error("withdrawHose: hose is already null.");
		} else {
			try {
				logger.info("Withdrawing hose: " + hose.name());
				//this throws out of memory exception many times
				hose.withdraw();
			} catch (Throwable e) {
				String hoseName = hose != null ? hose.getClass().getSimpleName() : null;
				String errorMsg = "Error Withdrawing from hose "+hoseName;
				ExceptionHandler.handleException(e, errorMsg);
			} finally {
				hose = null;
				//Thread.currentThread().interrupt();
			}
		}
	}

}
