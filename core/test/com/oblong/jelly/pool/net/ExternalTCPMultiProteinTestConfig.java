
/* (c)  oblong industries */

package com.oblong.jelly.pool.net;

import com.oblong.util.probability.IntRange;
import com.oblong.util.probability.SkewedIntDistribution;

import java.net.InetAddress;
import java.net.UnknownHostException;
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

    private static SettingsForMultiProteinTest settingsForMultiProteinTest;

    public static final String URI_FOR_TEST = "tcp://10.3.10.111";

    public static final int SLEEP_MS_SMALL = 20;

    public static final int SMALL_AWAIT_TIMEOUT = 50;

    public static final int SMALL_RUDE_DATA = 100;

    public static final int SMALL_BATCH_SIZE = 10;

    public static final int SMALL_NUMBER_OF_RUNS = 10;

    public static final double CHANCE_FIFT_FIFTY = 0.5;

    private static final SettingsForMultiProteinTest SETTINGS_FOR_QUICK_TEST =
        new SettingsForMultiProteinTest (URI_FOR_TEST, SLEEP_MS_SMALL, SMALL_AWAIT_TIMEOUT,
                                         SMALL_RUDE_DATA, SMALL_BATCH_SIZE,
                                         SMALL_NUMBER_OF_RUNS, CHANCE_FIFT_FIFTY);

    private static final SettingsForMultiProteinTest SETTINGS_FOR_ENDLESS_QUICK_TEST =
        new SettingsForMultiProteinTest (URI_FOR_TEST, SLEEP_MS_SMALL, SMALL_AWAIT_TIMEOUT,
                                         SMALL_RUDE_DATA, SMALL_BATCH_SIZE,
                                         NO_LIMIT_PROTEIN_NUMBER, CHANCE_FIFT_FIFTY);

    static {
        settingsForMultiProteinTest = SETTINGS_FOR_ENDLESS_QUICK_TEST;
    }

    private static SkewedIntDistribution connectorSleepDistribution =
        new SkewedIntDistribution (0.5, new IntRange (0, settingsForMultiProteinTest.defaultSleepMs));
    private static SkewedIntDistribution awaitNextDistribution =
        new SkewedIntDistribution (0.5, new IntRange (0, settingsForMultiProteinTest.defaultAwaitTimeout));

    private static SkewedIntDistribution dataLengthDistribution =
        new SkewedIntDistribution (settingsForMultiProteinTest.chanceOfPickingFromDistribution,
                                   new IntRange (0, settingsForMultiProteinTest.maxRudeData));

    private static SkewedIntDistribution batchSizeDistribution =
        new SkewedIntDistribution (settingsForMultiProteinTest.chanceOfPickingFromDistribution,
                                   new IntRange (0, settingsForMultiProteinTest.batchSize));

    public static void setDefaultTestSettingsForEndlessTest () {
        settingsForMultiProteinTest =
            new SettingsForMultiProteinTest (URI_FOR_TEST, SLEEP_MS_SMALL, SMALL_AWAIT_TIMEOUT,
                                             SMALL_RUDE_DATA, SMALL_BATCH_SIZE,
                                             SMALL_NUMBER_OF_RUNS, CHANCE_FIFT_FIFTY);
        INFINITE_TEST = true;
    }

    public static int getTotalNumberOfProteins () {
        return settingsForMultiProteinTest.numberOfRuns ==
            NO_LIMIT_PROTEIN_NUMBER ? NO_LIMIT_PROTEIN_NUMBER : settingsForMultiProteinTest.numberOfRuns *
            settingsForMultiProteinTest.batchSize;
    }

    public static int getRandomAwaitTimeout () {
        //return defaultAwaitTimeout;
        return awaitNextDistribution.random (random);
    }

    static int getRandomSleepingTime () {
        //return defaultSleepMs;
        return connectorSleepDistribution.random (random);
    }

    public static int getRandomDataLength () {
        // return maxRudeData;
        return dataLengthDistribution.random (random);
    }

    public static boolean shouldTestContinue (int currentRound, int maxNumberOfProteins) {
        return currentRound < maxNumberOfProteins || INFINITE_TEST;
    }

    static String getDefaultUri () {
        try {
            if (isBuildbot ()) {
                return DEFAULT_URI;
            } else {
                return settingsForMultiProteinTest.uriForTest;
            }
        } catch (UnknownHostException e) {
            return DEFAULT_URI;
        }

    }

    private static boolean isBuildbot () throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName().contains("buildbot");
    }

    static int getBatchSize () {
        return INFINITE_TEST ? getRandomBatchSize() : settingsForMultiProteinTest.batchSize;
    }

    private static int getRandomBatchSize() {
        return batchSizeDistribution.random (random);
    }

    /***
     * all these values can be modified
     */
    public static class SettingsForMultiProteinTest {
        /***
         * setting for uri to be used
         */
        private String uriForTest = "tcp://10.3.10.111";

        /**
         * sleep time used b sending thread
         */
        private int defaultSleepMs = 20;

        /***
         * sleep time to use with AwaitNext
         */
        private int defaultAwaitTimeout = 50;

        /**
         * setting for protein rude data array
         */
        private int maxRudeData = 100; //16384;

        /***
         * number of proteins sent in batch (without sleeping in between
         */
        private int batchSize = 10;

        /***
         * For stress tests maybe Integer.MAX_VALUE;
         * use 100000; this usually throws several exceptions if used with another pc with localhost it doesn't
         * too high value may hang the test for a long time
         * use NO_LIMIT_PROTEIN_NUMBER to run test undefinitely
         */
        public int numberOfRuns = 10;

        private double chanceOfPickingFromDistribution = 0.5;

        public SettingsForMultiProteinTest(String uriForTest,
                                           int defaultSleepMs,
                                           int defaultAwaitTimeout,
                                           int maxRudeData,
                                           int batchSize,
                                           int numberOfRuns,
                                           double chanceOfPickingFromDistribution) {
            this.uriForTest = uriForTest;
            this.defaultSleepMs = defaultSleepMs;
            this.defaultAwaitTimeout = defaultAwaitTimeout;
            this.maxRudeData = maxRudeData;
            this.batchSize = batchSize;
            this.numberOfRuns = numberOfRuns;
            this.chanceOfPickingFromDistribution = chanceOfPickingFromDistribution;
        }
    }
}
