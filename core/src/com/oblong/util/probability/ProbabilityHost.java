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

	private static final ProbabilityHost the = new ProbabilityHost();

//	private final long randomSeed = parseSeed("145ceace602"); // caused null workspace
//	private final long randomSeed = parseSeed("145d26a6964"); // caused null workspace
	private final long randomSeed = parseSeed("14615d83bbb"); // caused null workspace

	public static long parseSeed(String s) {
		return Long.parseLong(s, 16);
	}
//	private final long randomSeed = System.currentTimeMillis();

	/** Using ThreadLocal to prevent threads "snatching" values from each other (forever ruining the sequence) due to race conditions */
	ThreadLocal<ThreadLocalRandom> randomThreadLocal = new ThreadLocal<ThreadLocalRandom>() {
		@Override protected ThreadLocalRandom initialValue() {
			return new ThreadLocalRandom();
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

	public class ThreadLocalRandom {

		final Random random = new Random(randomSeed);

		int index = -1;

		public String getNextIndexPrefix() {
			index ++;
			return "rnd-" + index;
		}

	}

	public ProbabilityHost() {

		if(log.i()) log.i("ProbabilityHost Random seed (please save it if you want to re-try same scenario) : "
				+ Long.toHexString(randomSeed) + " ; Date: " + new Date());
//		this.random = new Random(randomSeed);
	}

	public long getRandomSeed() {
		return this.randomSeed;
	}

	public static ProbabilityHost get() {
		return the;
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
		if(log.t()) log.t(threadLocalRandom.getNextIndexPrefix() + " - generateRandomString (" + s.length() + "): " + s);
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

	public synchronized int nextIntExcludingMax(int maxExclusive) {
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
