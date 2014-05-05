package com.oblong.util.logging;

/**
 * User: karol
 * Date: 1/23/14
 * Time: 2:08 PM
 */
public interface LoggingObject {
//	String getLoggerName();

	// TODO: toLoggableString

	public String toLoggingObjectString();

	// TODO: split into static part (pre-calculated once to save CPU cycles) and dynamic part

	// TODO: interface LoggedObject
}
