package com.oblong.util.probability;

import com.oblong.util.logging.ObLog;

import java.util.Random;

/**
 * Mainly responsible for conveying the shared instance of Random
 *
 * User: karol
 * Date: 2014-02-18
 * Time: 17:11
 */
public class ProbabilityHost {

	protected final ObLog log = ObLog.get(this);

	public static final ProbabilityHost the = new ProbabilityHost();

	private final Random random;
	private final long randomSeed;

	public ProbabilityHost() {
		this.randomSeed = System.currentTimeMillis();
		if(log.i()) log.i("ProbabilityContext Random seed (please save it if you want to re-try same scenario) : "
				+ randomSeed);
		this.random = new Random(randomSeed);
	}

	public Random getRandom() {
		return random;
	}

	public long getRandomSeed() {
		return this.randomSeed;
	}
}
