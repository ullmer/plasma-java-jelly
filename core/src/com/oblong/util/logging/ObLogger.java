package com.oblong.util.logging;

import org.apache.log4j.Logger;

/**
 * User: karol
 * Date: 1/23/14
 * Time: 1:56 PM
 */
public class ObLogger extends Logger {

	protected ObLogger(Class<?> aClass) {
		super(aClass.getName());
	}

	public static ObLogger get(Class<?> aClass) {
		return new ObLogger(aClass);
	}

	public static ObLogger get(LoggingObject o) {
		// later we might also show the address of the object to allow distinguishing of object instances
		return get(o.getClass());
	}

}
