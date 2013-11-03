package com.oblong.util.probability;

import net.jcip.annotations.Immutable;

/**
 * User: karol
 * Date: 10/21/13
 * Time: 2:16 PM
 */
@Immutable
public abstract class SkewedDistribution<T extends Number> extends Distribution<T> {

	public static final Chance SURE_CHANCE = Chance.get(1.0);

	public final Chance chanceOfPickingFromDistribution;
	public final Distribution<T> distribution;

	public SkewedDistribution(Chance chanceOfPickingFromDistribution, Distribution<T> range)  {
		this.chanceOfPickingFromDistribution = chanceOfPickingFromDistribution;
		this.distribution = range;
	}

	@Override
	public T getMinInclusive() {
		throw throwMethodNotUseful();
	}

	@Override
	public T getMaxInclusive() {
		return distribution.getMaxInclusive();
	}
	
	private UnsupportedOperationException throwMethodNotUseful() {
		throw new UnsupportedOperationException("This method is not useful in this class (and can be misleading). " +
				"Call this method on the member field 'distribution'.");
	}

	public abstract SkewedDistribution<T> newInstance(Chance chanceOfPickingFromDistribution,
		Distribution<T> distribution);

	@Override
	public SkewedDistribution<T> multiplyMaxInclusive(double multiplier) {
		return newInstance(
				this.chanceOfPickingFromDistribution,
				distribution.multiplyMaxInclusive(multiplier)
		);
	}

}
