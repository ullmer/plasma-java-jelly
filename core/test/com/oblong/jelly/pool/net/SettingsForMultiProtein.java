package com.oblong.jelly.pool.net;

/***
 * all these values can be modified
 */
public class SettingsForMultiProtein {
	private String uriForTest;

	private int defaultSleepMs;

	private int defaultAwaitTimeout;

	private int maxRudeData; //16384;

	private int batchSize;

	private int numberOfRuns;

	private double chanceOfPickingFromDistribution;

	SettingsForMultiProtein(String uriForTest,
	                        int defaultSleepMs,
	                        int defaultAwaitTimeout,
	                        int maxRudeData,
	                        int batchSize,
	                        int numberOfRuns,
	                        double chanceOfPickingFromDistribution) {
		this.setUriForTest(uriForTest);
		this.setDefaultSleepMs(defaultSleepMs);
		this.setDefaultAwaitTimeout(defaultAwaitTimeout);
		this.setMaxRudeData(maxRudeData);
		this.setBatchSize(batchSize);
		this.setNumberOfRuns(numberOfRuns);
		this.setChanceOfPickingFromDistribution(chanceOfPickingFromDistribution);
	}

	public SettingsForMultiProtein() {

	}

	public static SettingsForMultiProtein createSettingsForMultiProteinTest(String uriForTest,
	                                                                            int defaultSleepMs,
	                                                                            int defaultAwaitTimeout,
	                                                                            int maxRudeData,
	                                                                            int batchSize,
	                                                                            int numberOfRuns,
	                                                                            double chanceOfPickingFromDistribution)
	{
		return new SettingsForMultiProtein(uriForTest, defaultSleepMs, defaultAwaitTimeout, maxRudeData, batchSize,
				numberOfRuns, chanceOfPickingFromDistribution);
	}

	/***
	 * setting for uri to be used
	 */
	public String getUriForTest() {
		return uriForTest;
	}

	public void setUriForTest(String uriForTest) {
		this.uriForTest = uriForTest;
	}

	/**
	 * sleep time used b sending thread
	 */
	public int getDefaultSleepMs() {
		return defaultSleepMs;
	}

	public void setDefaultSleepMs(int defaultSleepMs) {
		this.defaultSleepMs = defaultSleepMs;
	}

	/***
	 * sleep time to use with AwaitNext
	 */
	public int getDefaultAwaitTimeout() {
		return defaultAwaitTimeout;
	}

	public void setDefaultAwaitTimeout(int defaultAwaitTimeout) {
		this.defaultAwaitTimeout = defaultAwaitTimeout;
	}

	/**
	 * setting for protein rude data array
	 */
	public int getMaxRudeData() {
		return maxRudeData;
	}

	public void setMaxRudeData(int maxRudeData) {
		this.maxRudeData = maxRudeData;
	}

	/***
	 * number of proteins sent in batch (without sleeping in between
	 */
	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	/***
	 * For stress tests maybe Integer.MAX_VALUE;
	 * use 100000; this usually throws several exceptions if used with another pc with localhost it doesn't
	 * too high value may hang the test for a long time
	 * use NO_LIMIT_PROTEIN_NUMBER to run test undefinitely
	 */
	public int getNumberOfRuns() {
		return numberOfRuns;
	}

	public void setNumberOfRuns(int numberOfRuns) {
		this.numberOfRuns = numberOfRuns;
	}

	public double getChanceOfPickingFromDistribution() {
		return chanceOfPickingFromDistribution;
	}

	public void setChanceOfPickingFromDistribution(double chanceOfPickingFromDistribution) {
		this.chanceOfPickingFromDistribution = chanceOfPickingFromDistribution;
	}
}
