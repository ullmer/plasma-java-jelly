package com.oblong.jelly.pool.net;

import com.oblong.util.probability.IntRange;
import com.oblong.util.probability.SkewedIntDistribution;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: valeria
 * Date: 10/2/13
 * Time: 8:23 PM
 *
 * Basic settings for testint multi protein sending
 */
public class ExternalTCPMultiProteinTestConfig {

	public static final boolean SHOW_LOGS = true;
	public static final int DEFAULT_SLEEP_MS = 50;
	public static final int DEFAULT_AWAIT_TIMEOUT = 200;
	public static final int MAX_DATA_LENGTH = 100; //16384;
	public static final long NO_LIMIT_PROTEIN_NUMBER = -1;
	public static final boolean INFINITE_TEST = false;

	public static int NUMBER_OF_DEPOSITED_PROTEINS_IN_BATCH = 5;
	//100000; this usually throws several exceptions if used with another pc with localhost it doesn't

	public static final boolean MAKE_FAIL = false;
	public static final String URI = "tcp://10.3.10.111";
	private static SkewedIntDistribution connectorSleepDistribution =
			new SkewedIntDistribution(0.5, new IntRange(0, DEFAULT_SLEEP_MS));
	private static SkewedIntDistribution awaitNextDistribution =
			new SkewedIntDistribution(0.5, new IntRange(0, DEFAULT_AWAIT_TIMEOUT));
	private static SkewedIntDistribution dataLengthDistribution =
			new SkewedIntDistribution(0.5, new IntRange(0, MAX_DATA_LENGTH));

	/***
	 * decrease this number and you will get more noisy proteins
	 */
	public static final int NOISINESS = 500;
	public static int numberOfRuns = 10; //for stress tests maybe Integer.MAX_VALUE;
	private static Random random = new Random();

	public static int getTotalNumberOfProteins() {
		return numberOfRuns *
				NUMBER_OF_DEPOSITED_PROTEINS_IN_BATCH;
	}

	public static int getRandomAwaitTimeout() {
		//return DEFAULT_AWAIT_TIMEOUT;
		return awaitNextDistribution.random(random);
	}

	static int getRandomSleepingTime() {
		//return DEFAULT_SLEEP_MS;
		return connectorSleepDistribution.random(random);
	}

	public static int getRandomDataLength() {
//		return MAX_DATA_LENGTH;
		return dataLengthDistribution.random(random);
	}

	public static boolean shouldTestContinue(int currentRound, int maxNumberOfProteins) {
		return currentRound < maxNumberOfProteins || INFINITE_TEST;
	}
}
