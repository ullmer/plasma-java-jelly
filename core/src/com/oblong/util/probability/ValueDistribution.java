package com.oblong.util.probability;

import java.util.Random;

/**
 * User: karol
 * Date: 10/21/13
 * Time: 5:01 PM
 */
public abstract class ValueDistribution <T extends Number> {

	public abstract T random(Random random);

	public abstract T getMinInclusive();
	public abstract T getMaxInclusive();


	public abstract ValueDistribution<T> multiplyMaxInclusive(double multiplier);

	protected abstract T multiply(T value, double multiplier);

}
