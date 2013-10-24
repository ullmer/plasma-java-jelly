
/* (c)  oblong industries */

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
 * Basic settings for testing multi protein sending
 */
public class ExternalTCPMultiProteinTestConfig {

	public static final boolean SHOW_LOGS = true;
	public static final int NO_LIMIT_PROTEIN_NUMBER = -1;
	public static boolean INFINITE_TEST = false;
	public static final boolean MAKE_FAIL = false;
	private static final String DEFAULT_URI = "tcp://localhost";
	/***
	 * decrease this number and you will get more noisy proteins
	 */
	public static final int NOISINESS = 500;
	private static Random random = new Random();

	private static SettingsForMultiProtein settingsForMultiProteinTest;

	public static final String URI_FOR_TEST = null;

	public static final int SLEEP_MS_SMALL = 20;

	public static final int SMALL_AWAIT_TIMEOUT = 50;

	public static final int SMALL_RUDE_DATA = 156;

	public static final int SMALL_BATCH_SIZE = 10;

	public static final int SMALL_NUMBER_OF_RUNS = 10;

	public static final double CHANCE_FIFTY_FIFTY = 0.5;

	private static final SettingsForMultiProtein SETTINGS_FOR_QUICK_TEST =
			SettingsForMultiProtein.createSettingsForMultiProteinTest(URI_FOR_TEST, SLEEP_MS_SMALL, SMALL_AWAIT_TIMEOUT,
					SMALL_RUDE_DATA, SMALL_BATCH_SIZE, SMALL_NUMBER_OF_RUNS, CHANCE_FIFTY_FIFTY);

	private static final SettingsForMultiProtein SETTINGS_FOR_ENDLESS_QUICK_TEST =
			SettingsForMultiProtein.createSettingsForMultiProteinTest(URI_FOR_TEST,
					SLEEP_MS_SMALL, SMALL_AWAIT_TIMEOUT, SMALL_RUDE_DATA, SMALL_BATCH_SIZE,
					NO_LIMIT_PROTEIN_NUMBER, CHANCE_FIFTY_FIFTY);

	static {
		settingsForMultiProteinTest = SETTINGS_FOR_QUICK_TEST;
	}

	private static SkewedIntDistribution connectorSleepDistribution =
			new SkewedIntDistribution(0.5, new IntRange(0, settingsForMultiProteinTest.getDefaultSleepMs()));
	private static SkewedIntDistribution awaitNextDistribution =
			new SkewedIntDistribution(0.5, new IntRange(0, settingsForMultiProteinTest.getDefaultAwaitTimeout()));

	private static SkewedIntDistribution dataLengthDistribution =
			new SkewedIntDistribution(settingsForMultiProteinTest.getChanceOfPickingFromDistribution(),
					new IntRange(0, settingsForMultiProteinTest.getMaxRudeData()));

	private static SkewedIntDistribution batchSizeDistribution =
			new SkewedIntDistribution(settingsForMultiProteinTest.getChanceOfPickingFromDistribution(),
					new IntRange(1, settingsForMultiProteinTest.getBatchSize()));

	public static void setDefaultTestSettingsForEndlessTest(){
		settingsForMultiProteinTest = SettingsForMultiProtein.createSettingsForMultiProteinTest(URI_FOR_TEST,
				SLEEP_MS_SMALL, SMALL_AWAIT_TIMEOUT, SMALL_RUDE_DATA, SMALL_BATCH_SIZE, SMALL_NUMBER_OF_RUNS,
				CHANCE_FIFTY_FIFTY);
		INFINITE_TEST = true;
	}

	public static int getTotalNumberOfProteins() {
		return settingsForMultiProteinTest.getNumberOfRuns() == NO_LIMIT_PROTEIN_NUMBER ?
				NO_LIMIT_PROTEIN_NUMBER : settingsForMultiProteinTest.getNumberOfRuns() *
				settingsForMultiProteinTest.getBatchSize();
	}

	public static int getRandomAwaitTimeout() {
		return awaitNextDistribution.random(random);
	}

	static int getRandomSleepingTime() {
		return connectorSleepDistribution.random(random);
	}

	public static int getRandomDataLength() {
		return dataLengthDistribution.random(random);
	}

	public static boolean shouldTestContinue(int currentRound, int maxNumberOfProteins) {
		return currentRound < maxNumberOfProteins || INFINITE_TEST;
	}

	static String getDefaultUri() {
		return settingsForMultiProteinTest.getUriForTest() !=null ? settingsForMultiProteinTest.getUriForTest() : DEFAULT_URI;
	}


	static int getBatchSize() {
		return INFINITE_TEST ? getRandomBatchSize() : settingsForMultiProteinTest.getBatchSize();
	}

	private static int getRandomBatchSize() {
		return batchSizeDistribution.random(random);
	}

}
