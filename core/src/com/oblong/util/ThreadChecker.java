package com.oblong.util;

/**
 * User: karol
 * Date: 2014-02-13
 * Time: 13:37
 */
public class ThreadChecker {

	private final Thread allowedThread;

        protected boolean threadCheckerEngaged = true;                             //BAU
        public    boolean isThreadCheckerEngaged() {return threadCheckerEngaged;}  //BAU
        public    void    engageThreadChecker()    {threadCheckerEngaged = true;}  //BAU
        public    void    disengageThreadChecker() {threadCheckerEngaged = false;} //BAU

	public ThreadChecker(Thread allowedThread) {
		this.allowedThread = allowedThread;

	}

	public ThreadChecker() {
		this(Thread.currentThread());
	}

	public void check() {
                if (!isThreadCheckerEngaged()) {return;} //BAU
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

	/** Introduced to prevent NullPointerException in cases where superclass constructor calls overriden methods which use threadChecker */
	public static void check(ThreadChecker threadChecker) {
		if (threadChecker != null) {
			threadChecker.check();
		}
	}
}
