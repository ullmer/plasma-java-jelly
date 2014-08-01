package com.oblong.util.probability;

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 9/6/13
 * Time: 2:41 PM
 */
public class FloatRange extends Range<Float> {
	public FloatRange(Float minInclusive, Float maxInclusive) {
		super(minInclusive, maxInclusive);
	}

	@Override
	public Range<Float> newInstance(Float minInclusive, Float maxInclusive) {
		return new FloatRange(minInclusive, maxInclusive);
	}


	public Float random() {
		float ret = getProbabilityHost().randomFloatInclusive(minInclusive, maxInclusive);
		return ret;
	}

	@Override
	protected Float multiply(Float value, double multiplier) {
		return Float.valueOf((float)(value * multiplier));
	}
}
