package com.oblong.util;

/**
 * User: karol
 * Date: 2014-02-13
 * Time: 13:37
 */
public class ThreadChecker {

	private final Thread allowedThread;

	public ThreadChecker(Thread allowedThread) {
		this.allowedThread = allowedThread;

	}

	public void check() {
		// TODO: disable for release to avoid performance penalty
		Thread currentThread = Thread.currentThread();
		if(/*!DebugAndTestSettings.DEMO_MODE &&*/ allowedThread != currentThread) {
			throw new RuntimeException("This object must be created or accessed only on pre-selected thread: "
					+ allowedThread + ". The attempting thread is '"+currentThread+"'.");
		}
	}

	@Override
	public String toString() {
		return "ThreadChecker{" +
				"allowedThread=" + allowedThread +
				'}';
	}
}
