package com.oblong.util;

import java.util.Set;

/**
 * Utilities for runtime assertions.
 *
 * Avoiding autoboxing/unboxing performance hit, by having specialized methods for primitive types.
 *
 * For release, we might make those assertions non-crashing, e.g. by using ExceptionHandler.handleException()
 *
 * @author Karol, 2014-04-25
 */
public class AssertUtils {

	public static final String MESSAGE_PREFIX = " --  Message: ";

	private static void throwAssertEqualsError(Object val1, Object val2, String message) {
		String errorMessage = "Values should be equal: " + val1 + " === " + val2 + MESSAGE_PREFIX + message;
		ExceptionHandler.handleException(errorMessage);
//		throw new RuntimeException(errorMessage);
	}

	public static void assertEquals(boolean val1, boolean val2, String message) {
		if ( val1 != val2 ) {
			throwAssertEqualsError(val1, val2, message);
		}
	}

	public static void assertEquals(int val1, int val2, String message) {
		if ( val1 != val2 ) {
			throwAssertEqualsError(val1, val2, message);
		}
	}

	public static void assertEquals(long val1, long val2, String message) {
		if ( val1 != val2 ) {
			throwAssertEqualsError(val1, val2, message);
		}
	}

	public static void assertEquals(CharSequence val1, CharSequence val2, String message) {
		assertObjectsEquals(val1, val2, message);
	}

	private static void assertObjectsEquals(Object val1, Object val2, String message) {
		if ( ! val1.equals(val2) ) {
			throwAssertEqualsError(val1, val2, message);
		}
	}

	public static void assertNotNull(Object value, String message) {
		if ( value == null ) {
			throw new RuntimeException("Value must not be " + value + MESSAGE_PREFIX + message);
		}
	}

	public static void assertEquals(Set<String> expected, Set<String> actual, String msg) {
		assertObjectsEquals(expected, actual, msg);
	}

	public static void assertTrue(boolean b, String message) {
		if ( !b ) {
			throw new RuntimeException("Value must be true. -- Condition: " + message);
		}
	}

	public static void assertSameClass(Object o1, Object o2) {
		assertObjectsEquals(o1.getClass(), o2.getClass(), "must be the same class");
	}

}
