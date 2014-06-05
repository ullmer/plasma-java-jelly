package com.oblong.util.probability;

import net.jcip.annotations.Immutable;

/**
 * AKA probability
 *
 * User: karol
 * Date: 9/6/13
 * Time: 2:36 PM
 */
@Immutable
public class Chance extends ProbabilityParam {

	private static final int MULTIPLY_TO_INT = 100;

	public final double chance;

	public Chance(double chance) {
		if ( chance < 0.0 || chance > 1.0 ) {
			throw new IllegalArgumentException("Chance not in 0..1 range: " + chance);
		}
		this.chance = chance;
	}

	public boolean randomBool() {
		if ( chance == 0 ) {
			return false; // a bit of speedup
		} else if (chance == 1.0) {
			return true;
		} else {
			return getProbabilityHost().nextIntExcludingMax(MULTIPLY_TO_INT) < chance * MULTIPLY_TO_INT;
		}

		// examples:
		// nextInt <   0 => never
		// nextInt <  50 => 50% chance
		// nextInt < 100 => always
	}

	public static Chance get(double chance) {
		return new Chance(chance);
	}

	@Override
	public String toString() {
		return "Chance{" +
				"chance=" + chance +
				'}';
	}
}
