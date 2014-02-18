package com.oblong.util.probability;

/**
 * User: karol
 * Date: 10/21/13
 * Time: 4:39 PM
 */
public class SkewedIntDistribution extends SkewedDistribution<Integer> {

	public SkewedIntDistribution(double chanceOfPickingFromDistribution,
	                             Distribution<Integer> distribution) {
		this(new Chance(chanceOfPickingFromDistribution), distribution);
	}

	public SkewedIntDistribution(Chance chanceOfPickingFromDistribution,
	                             Distribution<Integer> distribution) {
		super(chanceOfPickingFromDistribution, distribution);
	}

	@Override public SkewedDistribution<Integer> newInstance(Chance chanceOfPickingFromDistribution,
			Distribution<Integer> distribution) {
		return new SkewedIntDistribution(chanceOfPickingFromDistribution, distribution);
	}

	@Override public Integer random() {
		boolean fromDistribution = chanceOfPickingFromDistribution.randomBool();
		if ( fromDistribution ) {
			return distribution.random();
		} else {
			return 0;
		}
	}

	@Override protected Integer multiply(Integer value, double multiplier) {
		return Integer.valueOf((int) (value * multiplier));
	}

}
