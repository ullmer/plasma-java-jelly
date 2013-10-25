
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

	protected static SettingsForMultiProtein settingsForMultiProteinTest;
	public static final double NEVER_ZERO = 1.0;

	private static final SettingsForMultiProtein SETTINGS_FOR_QUICK_TEST =
			SettingsForMultiProtein.createSettingsForMultiProteinTest(null, 20, 50,
					156, 10, 10, 0.5);

	private static final SettingsForMultiProtein SETTINGS_FOR_ENDLESS_TEST =
			SettingsForMultiProtein.createSettingsForMultiProteinTest(
					null, //custom url (if null use local host)
					20, //default or max sleep ms (for sending thread)
					50, //default or max await next timeout
					156, //default or max byte array size for protein raw data
					10, //default or max batch size
					NO_LIMIT_PROTEIN_NUMBER, //max number fo proteins to send
					0.5);//chance of picking from distribution: 0 means only 0 will be picked 1 means never 0

	static {
		//default settings for quick test for buildbot
		settingsForMultiProteinTest = SETTINGS_FOR_QUICK_TEST;
	}

	private static SkewedIntDistribution senderSleepDistribution =
			new SkewedIntDistribution(settingsForMultiProteinTest.getChanceOfPickingFromDistribution(),
					new IntRange(1, settingsForMultiProteinTest.getDefaultSleepMs()));

	private static SkewedIntDistribution awaitNextDistribution =
			new SkewedIntDistribution(settingsForMultiProteinTest.getChanceOfPickingFromDistribution(),
					new IntRange(1, settingsForMultiProteinTest.getDefaultAwaitTimeout()));

	private static SkewedIntDistribution dataLengthDistribution =
			new SkewedIntDistribution(
					settingsForMultiProteinTest.getChanceOfPickingFromDistribution(),
					new IntRange(1, settingsForMultiProteinTest.getMaxRudeData()));

	/***
	 * batch size should never be 0
	 */
	private static SkewedIntDistribution batchSizeDistribution =
			new SkewedIntDistribution(NEVER_ZERO,
					new IntRange(1, settingsForMultiProteinTest.getBatchSize()));

	public static void setDefaultTestSettingsForEndlessTest(){
		settingsForMultiProteinTest = SETTINGS_FOR_ENDLESS_TEST;
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
		return senderSleepDistribution.random(random);
	}

	public static int getRandomDataLength() {
		return dataLengthDistribution.random(random);
	}

	public static boolean shouldTestContinue(int currentRound, int maxNumberOfProteins) {
		return currentRound < maxNumberOfProteins || INFINITE_TEST;
	}

	static String getDefaultUri() {
		return settingsForMultiProteinTest.getUriForTest() != null ? settingsForMultiProteinTest.getUriForTest() : DEFAULT_URI;
	}


	static int getBatchSize() {
		return INFINITE_TEST ? getRandomBatchSize() : settingsForMultiProteinTest.getBatchSize();
	}

	private static int getRandomBatchSize() {
		return batchSizeDistribution.random(random);
	}

}
