package com.oblong.util.probability;

import com.oblong.jelly.schema.fields.HasUid;
import com.oblong.jelly.schema.util.OrderedUidMap;
import com.oblong.util.logging.ObLog;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
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

	public static final String NEW_SEED = "NEW_SEED";

	private static ProbabilityHost theInstance = null;

	private final long randomSeed;

	public static long parseSeed(String s) {
		if (s.equalsIgnoreCase(NEW_SEED)) {
			return System.currentTimeMillis();
		}
		return Long.parseLong(s, 16);
	}

	/** Using ThreadLocal to prevent threads "snatching" values from each other (forever ruining the sequence) due to race conditions */
	ThreadLocal<ThreadLocalRandom> randomThreadLocal = new ThreadLocal<ThreadLocalRandom>() {
		@Override protected ThreadLocalRandom initialValue() {
			return new ThreadLocalRandom(randomSeed);
		}
	};

	public synchronized double nextDouble(double max) {
		return getRandom().nextDouble() * max;
	}

	private Random getRandom() {
		return randomThreadLocal.get().random;
	}

	public <T extends HasUid> T pickRandomlyFrom(OrderedUidMap<T, T> collection) {
		int index = randomIntInclusive(0, collection.size() - 1);
		Iterator<T> iterator = collection.values().iterator();
		T pickedElement = null;
		for (int i = 0; i <= index; ++i ) {
			pickedElement = iterator.next();
		}

		return pickedElement;
	}

	public static void set(ProbabilityHost probabilityHost) {
		if ( theInstance != null ) {
			throw new RuntimeException("Can only be set once. Trying to set to " + probabilityHost);
		}
		theInstance = probabilityHost;
	}

	public class ThreadLocalRandom {

		private final long randomSeed;

		ThreadLocalRandom(long randomSeed) {
			this.randomSeed = randomSeed;
			random = new Random(this.randomSeed);
		}

		private final Random random;

		int index = -1;

		public String getNextIndexPrefix() {
			index ++;
			return "rnd-" + index;
		}

	}

	public ProbabilityHost(String randomSeed) {
		this.randomSeed = parseSeed(randomSeed);
		if(log.i()) log.i("ProbabilityHost Random seed (please save it if you want to re-try same scenario) : "
				+ getRandomSeedString() + " ; Date: " + new Date());
//		this.random = new Random(randomSeed);
	}

	public String getRandomSeedString() {
		return Long.toHexString(this.randomSeed);
	}

	public long getRandomSeed() {
		return this.randomSeed;
	}

	public static ProbabilityHost getInstance() {
		return theInstance;
	}

	public synchronized String generateRandomString(String nonMandatoryPrefix, int randomStringLen) {
		if (nonMandatoryPrefix.length()>randomStringLen) {
			nonMandatoryPrefix = nonMandatoryPrefix.substring(0, randomStringLen);
		}

		ThreadLocalRandom threadLocalRandom = randomThreadLocal.get();
		final StringBuilder sb = new StringBuilder(randomStringLen);
		sb.append(nonMandatoryPrefix);

		final String endSuffix = "END";

		for (int i=sb.length(); i< randomStringLen - endSuffix.length(); ++i) {
//			char c = (char) ((randomThreadLocal.get().nextInt() & 0x5F) + 20);
			char c = (char) randomIntInclusive(0x20, 0x7E, threadLocalRandom.random);
			sb.append(c);
		}
		sb.append(endSuffix);
		String s = sb.toString();
		s = s.substring(0, randomStringLen); // trim to avoid endSuffix causing it longer than desired
		if(log.t()) log.t(threadLocalRandom.getNextIndexPrefix() + " - generateRandomStringOfLength (" + s.length() + "): " + s);
		return s;
	}

	public String generateRandomString(String nonMandatoryPrefix, IntRange lengthRange) {
		return generateRandomString(nonMandatoryPrefix, lengthRange.random());
	}


	public synchronized float nextFloat() {
		ThreadLocalRandom threadLocalRandom = randomThreadLocal.get();
		float val = threadLocalRandom.random.nextFloat();
		if(log.t()) log.t(threadLocalRandom.getNextIndexPrefix() + " - nextFloat: " + val);
		return val;
	}

	public synchronized void nextBytes(byte[] data) {
		ThreadLocalRandom threadLocalRandom = randomThreadLocal.get();
		threadLocalRandom.random.nextBytes(data);
		if(log.t()) log.t(threadLocalRandom.getNextIndexPrefix() + " - nextBytes (" + data.length + "): " + Arrays.toString(data));
	}

	public synchronized int randomIntExcludingMax(int maxExclusive) {
		ThreadLocalRandom threadLocalRandom = randomThreadLocal.get();
		int val = threadLocalRandom.random.nextInt(maxExclusive);
		if(log.t()) log.t(threadLocalRandom.getNextIndexPrefix() + " - nextIntExcludingMax (" + maxExclusive + "): " + val);
		return val;
	}

	public synchronized int randomIntInclusive(int minInclusive, int maxInclusive) {
		ThreadLocalRandom threadLocalRandom = randomThreadLocal.get();
		int val = randomIntInclusive(minInclusive, maxInclusive, threadLocalRandom.random);
		if(log.t()) log.t(threadLocalRandom.getNextIndexPrefix() + " - randomIntInclusive (" +
				minInclusive + "," + maxInclusive + "): " + val);
		return val;
	}

	private int randomIntInclusive(int minInclusive, int maxInclusive, Random random) {
		int diff = maxInclusive - minInclusive;
		if ( diff > 0 ) {
			return minInclusive + random.nextInt(diff + 1);
		} else {
			return minInclusive;
		}
	}

}
