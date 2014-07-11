package com.oblong.util;

import org.apache.log4j.Logger;

/**
 * User: karol
 * Date: 10/22/13
 * Time: 12:40 PM
 */
public class Util {

	private static Logger logger = Logger.getLogger(Util.class);


	/** An idiom for circumventing Java's annoying (at least for prototyping) checked exceptions mandatoriness. */
	public static RuntimeException rethrow(Throwable throwable) {
		RuntimeException toReThrow;
		if ( throwable instanceof RuntimeException ) {
			toReThrow = (RuntimeException) throwable;
		} else {
			toReThrow = new RuntimeException(throwable);
		}
		throw toReThrow;

		// The fact that we don't return anything, but throw instead, is part of the idiom.
		// It protects against client code forgetting to throw.
	}

	public static void sleepUninterruptibly(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw Util.rethrow(e);
		}
	}

	public static String getMandatorySystemProperty(String propertyName, String exampleValue) {
		String propertyValue = System.getProperty(propertyName);
		if ( propertyValue == null ) {
			throw new RuntimeException("Mandatory system property " + propertyName + " not set." +
					" To set it, use e.g.: -D" + propertyName + "=" + exampleValue);
		} else {
			logger.info("System property " + propertyName + ": " + propertyValue);
		}
		return propertyValue;
	}

	public static float interpolate(float start, float end, float interpolatedTime) {
		float delta = end - start;
		return start + interpolatedTime * delta;
	}

	public static String getSimplifiedToString(Object o) {
		if ( o == null ) {
			return "{null}";
		}
//		return o.getClass().getSimpleName() + "#" + identityHashCodeString(o);
		return identityHashCodeString(o);
	}

	public static String identityHashCodeString(Object o) {
		int identityHashCode = System.identityHashCode(o);
		return "@$" + Integer.toHexString(identityHashCode);
	}
}
