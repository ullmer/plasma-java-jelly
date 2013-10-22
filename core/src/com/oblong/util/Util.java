package com.oblong.util;

import java.util.Random;

/**
 * User: karol
 * Date: 10/22/13
 * Time: 12:40 PM
 */
public class Util {
	public static RuntimeException rethrow(Throwable throwable) {
		RuntimeException toReThrow;
		if ( throwable instanceof RuntimeException ) {
			toReThrow = (RuntimeException) throwable;
		} else {
			toReThrow = new RuntimeException(throwable);
		}
		throw toReThrow;

		// the fact that we don't return anything, but throw instead, is an idiom
	}

	public static void sleepUninterruptibly(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw Util.rethrow(e);
		}
	}

	public static void randomSleep(Random r, int sleepMs) throws InterruptedException {
		Thread.sleep(r.nextInt(sleepMs));
	}

}
