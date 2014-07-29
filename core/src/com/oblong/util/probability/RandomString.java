package com.oblong.util.probability;

/**
 * @author Karol, 2014-07-28
 */
public class RandomString extends ProbabilityParam {

	public final Distribution<Integer> lengthDistribution;

	public RandomString(IntRange lengthDistribution) {
		this.lengthDistribution = lengthDistribution;
	}

	public RandomString(int minInclusive, int maxInclusive) {
		this(new IntRange(minInclusive, maxInclusive));
	}

	public String generateRandomString(String nonMandatoryPrefix) {
		return getProbabilityHost().generateRandomString(nonMandatoryPrefix, lengthDistribution);
	}
}
