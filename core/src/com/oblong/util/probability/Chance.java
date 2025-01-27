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

	public static final Chance SURE_CHANCE = new Chance(1.0);

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
//		} else if (chance == 1.0) {
//			return true;
		} else {
			/* picking from distribution even if chance == 1.0, to not disturb the random sequence if switching to e.g. a slower TimeMs,
				to observe the same event sequence in slow motion */
			return getProbabilityHost().randomIntExcludingMax(MULTIPLY_TO_INT) < chance * MULTIPLY_TO_INT;
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
