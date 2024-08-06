package com.oblong.util.probability;

/**
 * User: karol
 * Date: 10/21/13
 * Time: 5:01 PM
 */
public abstract class Distribution<T extends Number> extends ProbabilityParam {

	public abstract T random();

	public abstract T getMinInclusive();
	public abstract T getMaxInclusive();


	public abstract Distribution<T> multiplyMaxInclusive(double multiplier);

	protected abstract T multiply(T value, double multiplier);

}
