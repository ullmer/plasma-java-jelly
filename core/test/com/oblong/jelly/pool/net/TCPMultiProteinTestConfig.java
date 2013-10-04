package com.oblong.jelly.pool.net;

/**
 * Created with IntelliJ IDEA.
 * User: valeria
 * Date: 10/2/13
 * Time: 8:23 PM
 *
 * Basic settings for testint multi protein sending
 */
public class TCPMultiProteinTestConfig {

	public static final boolean SHOW_LOGS = true;
	public static final int SLEEP_MILI_SECS = 10;
	public static final int DEFAULT_AWAIT_TIMEOUT = 100;
	public static final int MAX_DATA_LENGH = 100; //16384;
	public static int NUMBER_OF_DEPOSITED_PROTEINS = 10;
			//100000; this usually throws several exceptions if used with another pc wit localhost it doesn't
	public static final boolean MAKE_FAIL = false;
	public static final String URI = "tcp://localhost";

	/***
	 * decrease this number and you will get more noisy proteins
	 */
	public static final int NOISINESS = 500;
}
