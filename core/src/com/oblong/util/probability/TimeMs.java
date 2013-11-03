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
public class TimeMs extends SkewedIntDistribution {

	public TimeMs(double chanceOfPickingFromDistribution, Distribution<Integer> range) {
		this(new Chance(chanceOfPickingFromDistribution), range);
	}

	public TimeMs(Chance chanceOfPickingFromDistribution, Distribution<Integer> range) {
		super(chanceOfPickingFromDistribution, range);
	}

	public TimeMs(int exactValue) {
		this(SURE_CHANCE, new IntRange(exactValue));
	}

	@Override
	public TimeMs newInstance(Chance chanceOfPickingFromDistribution, Distribution<Integer> distribution) {
		return new TimeMs(chanceOfPickingFromDistribution, distribution);
	}

	public void sleep(Random random) {
		Util.sleepUninterruptibly(random(random));
	}

	@Override
	public TimeMs multiplyMaxInclusive(double multiplier) {
		return (TimeMs) super.multiplyMaxInclusive(multiplier);
	}

}
