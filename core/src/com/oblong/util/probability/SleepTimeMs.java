package com.oblong.util.probability;

import com.oblong.util.Util;
import net.jcip.annotations.Immutable;

import java.util.Random;

/**
 * Represents a probability distribution of sleep time.
 *
 * The distribution is skewed to give more chance to non-sleep (zero ms), which is an edge case
 * (with more probability for uncovering race conditions), and should be
 * exercised more often than other values.
 *
 * User: karol
 * Date: 10/21/13
 * Time: 1:44 PM
 */
@Immutable
public class SleepTimeMs extends SkewedIntDistribution {

	public SleepTimeMs(double chanceOfPickingFromDistribution, ValueDistribution<Integer> range) {
		this(new Chance(chanceOfPickingFromDistribution), range);
	}

	public SleepTimeMs(Chance chanceOfPickingFromDistribution, ValueDistribution<Integer> range) {
		super(chanceOfPickingFromDistribution, range);
	}

	public SleepTimeMs(int exactValue) {
		this(SURE_CHANCE, new IntRange(exactValue));
	}

	@Override
	public SleepTimeMs newInstance(Chance chanceOfPickingFromDistribution, ValueDistribution<Integer> distribution) {
		return new SleepTimeMs(chanceOfPickingFromDistribution, distribution);
	}

	public void randomSleep(Random random) {
		Util.sleepUninterruptibly(random(random));
	}

	@Override
	public SleepTimeMs multiplyMaxInclusive(double multiplier) {
		return (SleepTimeMs) super.multiplyMaxInclusive(multiplier);
	}

}
