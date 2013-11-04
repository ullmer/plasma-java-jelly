package com.oblong.jelly.pool.net.stress;

import com.oblong.util.probability.*;

/**
 * User: karol
 * Date: 10/30/13
 * Time: 4:12 PM
 */
public class TestConfig {

	public static final int MB = 1024 * 1024;

	/** Simluate failure in the test */
	public static final boolean MAKE_FAIL = false;


	public TimeMs sleepBetweenProteinBatches = new TimeMs(0.20, new IntRange(0, 100));
	public TimeMs sleepBetweenProteinsInBatch = new TimeMs(0.05, new IntRange(0, 15));

	public TimeMs awaitNextTimeout = new TimeMs(0.90, new IntRange(0, 20 * 1000));

	/** Caution: too big value will cause Receiver to never catch-up with Sender */
	public TimeMs sleepBetweenAwaitNext = new TimeMs(0.20, new IntRange(0, 300));

	public TimeMs sleepAfterRemovingPool = new TimeMs(0.20, new IntRange(0, 300));

	/** Cannot be zero, because Receiver depends on receiving a protein with last-protein-in-connection-session flag */
	public Distribution<Integer> qtyProteinsInBatch = new IntRange(1, 20);

	/** Cannot be zero, because Receiver depends on receiving a protein with last-protein-in-connection-session flag */
	public Distribution<Integer> qtyBatchesBeforeDisconnect = new IntRange(1, 100);

	public Distribution<Integer> qtyRudeDataBytes = new SkewedIntDistribution(0.90, new IntRange(0, 3000));

	public Chance chanceOfRemovingPool = new Chance(0.05);

	public long qtyConnectionSessionCycles = Long.MAX_VALUE;

	public int getMaxPossibleQtyProteinsPerConnectionSession() {
		return qtyProteinsInBatch.getMaxInclusive() * qtyBatchesBeforeDisconnect.getMaxInclusive();
	}


	public static class Presets {

		public static final TestConfig THOROUGH = new TestConfig();

		public static final TestConfig JUNIT = new TestConfig() {
			{
				qtyConnectionSessionCycles = 20;
			}
		};


		public static final TestConfig VARYING_SMALL = new TestConfig() {
			{
				qtyProteinsInBatch = new IntRange(1, 5);
				qtyBatchesBeforeDisconnect = new IntRange(1, 5);
				qtyRudeDataBytes = new SkewedIntDistribution(0.90, new IntRange(0, 30));
			}
		};

		public static final TestConfig CONST_SMALL = new TestConfig() {
			{
				qtyProteinsInBatch = new IntRange(2);
				qtyBatchesBeforeDisconnect = new IntRange(2);
				qtyRudeDataBytes = new IntRange(2);
			}
		};
	}

	public static final TestConfig CONFIG = Presets.THOROUGH;

}
