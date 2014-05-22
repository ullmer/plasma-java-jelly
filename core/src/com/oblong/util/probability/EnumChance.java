package com.oblong.util.probability;

import com.oblong.util.Util;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * By default, all enum members receive the same weight equal to 1.
 *
 * @author Karol, 2014-05-20
 */
public class EnumChance<T extends Enum> {

	ProbabilityHost probabilityHost = ProbabilityHost.get();

	private final Map<T, Double> mapMemberToWeight = new HashMap<T, Double>();
	private final Chance chanceOfAny;

	protected double weightSum;

	private EnumChance(double chanceOfAny, Class<T> enumClass) {
		this.chanceOfAny = new Chance(chanceOfAny);
		setDefaultWeights(enumClass);
	}

	private void setDefaultWeights(Class<T> enumClass) {
		T[] enumValues = getEnumValues(enumClass);
		for (T enumValue : enumValues) {
			setWeight(1, enumValue);
		}
	}

	private T[] getEnumValues(Class<T> enumClass) {
		try {
			T[] values = (T[]) enumClass.getMethod("values").invoke(null);
			return values;
		} catch (IllegalAccessException e) {
			throw Util.rethrow(e);
		} catch (InvocationTargetException e) {
			throw Util.rethrow(e);
		} catch (NoSuchMethodException e) {
			throw Util.rethrow(e);
		}
	}

	public static <T extends Enum> EnumChance<T> create(double chanceOfAny, Class<T> enumClass) {
		return new EnumChance<T>(chanceOfAny, enumClass);
	}

	public EnumChance<T> setWeight(double weight, T value) {
		mapMemberToWeight.put(value, weight);
		recalculateWeightSum();
		return this;
	}

	private void recalculateWeightSum() {
		weightSum = 0;
		for (Map.Entry<T, Double> entry : mapMemberToWeight.entrySet()) {
			weightSum += entry.getValue();
		}
	}

	@Override public String toString() {
		return "ChanceOfEnum{" +
				"mapMemberToWeight=" + mapMemberToWeight +
				'}';
	}

	public T pickRandom() {
		if (!chanceOfAny.randomBool()) {
			return null;
		}
		double pickedVal = probabilityHost.nextDouble(weightSum);
		double currentMemberMax = 0;
		for (Map.Entry<T, Double> entry : mapMemberToWeight.entrySet()) {
			currentMemberMax += entry.getValue();
			final boolean pickedValContainedInRangeOfMember = currentMemberMax >= pickedVal;
			if (pickedValContainedInRangeOfMember) {
				return entry.getKey();
			}
		}
		throw new RuntimeException("Internal error: unable to pick enum member - did not find any range that would include the picked " +
				"val: " + pickedVal + ", for this " + this);
	}


}
