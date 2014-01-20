package com.oblong.util.probability;

import net.jcip.annotations.Immutable;

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 9/6/13
 * Time: 2:22 PM
 *
 */
@Immutable
public abstract class Range<T extends Number> extends Distribution<T> {

	public final T minInclusive;
	public final T maxInclusive;

	@Override
	public T getMinInclusive() {
		return minInclusive;
	}

	@Override
	public T getMaxInclusive() {
		return maxInclusive;
	}

	public Range(T minInclusive, T maxInclusive) {
		this.minInclusive = minInclusive;
		this.maxInclusive = maxInclusive;
	}

	public Range(T minMaxInclusive) {
		this(minMaxInclusive, minMaxInclusive);
	}

	public abstract Range<T> newInstance(T minInclusive, T maxInclusive);

	@Override
	public Range<T> multiplyMaxInclusive(double multiplier) {
		return newInstance(minInclusive, multiply(maxInclusive, multiplier));
	}


}
