package com.oblong.util.probability;

import net.jcip.annotations.Immutable;

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 9/6/13
 * Time: 2:29 PM
 */
@Immutable
public class IntRange extends Range<Integer> {

	public IntRange(Integer minInclusive, Integer maxInclusive) {
		super(minInclusive, maxInclusive);
	}

	@Override
	public Range<Integer> newInstance(Integer minInclusive, Integer maxInclusive) {
		return new IntRange(minInclusive, maxInclusive);
	}

	public IntRange(int minMax) {
		this(minMax,minMax);
	}

	/**
	 * NOTE: max value is intentionally *inclusive*, unlike in java.util.Random.nextInt
	 */
	@Override public Integer random() {
		return getProbabilityHost().randomIntInclusive(minInclusive, maxInclusive);
	}

	@Override
	public IntRange multiplyMaxInclusive(double multiplier) {
		return (IntRange) super.multiplyMaxInclusive(multiplier);
	}

	@Override
	protected Integer multiply(Integer value, double multiplier) {
		return Integer.valueOf((int) (maxInclusive * multiplier));
	}

	public String generateRandomStringOfLength(String nonMandatoryPrefix) {
		return getProbabilityHost().
				generateRandomString(nonMandatoryPrefix, this);
	}

}
