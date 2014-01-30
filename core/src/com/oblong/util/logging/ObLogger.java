package com.oblong.util.logging;

import com.oblong.jelly.Protein;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * User: karol
 * Date: 1/23/14
 * Time: 1:56 PM
 */
public class ObLogger {

	/**
	 * Using composition instead of inheritance because of the complexity of creating a subclass
	 * of org.apache.log4j.Logger, due to e.g. interactions with LoggerRepository and static factory methods.
	 */
	protected final Logger logger;

	protected ObLogger(Class<?> aClass) {
		this(Logger.getLogger(aClass));
	}

	protected ObLogger(Logger logger) {
		this.logger = logger;
	}

	public static ObLogger get(Class<?> aClass) {
		return new ObLogger(aClass);
	}

	public static ObLogger get(Object o) {
		// later we might also show the address of the object to allow distinguishing of object instances
		return get(o.getClass());
	}

	public void trace(Object message) {
		logger.trace(message);
	}

	public void trace(Object message, Throwable t) {
		logger.trace(message, t);
	}

	public void debug(Object message) {
		logger.debug(message);
	}

	public void debug(Object message, Throwable t) {
		logger.debug(message, t);
	}

	public void error(Object message) {
		logger.error(message);
	}

	public void error(Object message, Throwable t) {
		logger.error(message, t);
	}

	public void assertLog(boolean assertion, String msg) {
		logger.assertLog(assertion, msg);
	}

	public void fatal(Object message) {
		logger.fatal(message);
	}

	public void fatal(Object message, Throwable t) {
		logger.fatal(message, t);
	}

	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	public boolean isEnabledFor(Priority level) {
		return logger.isEnabledFor(level);
	}

	public void log(Priority priority, Object message, Throwable t) {
		logger.log(priority, message, t);
	}

	public void log(Priority priority, Object message) {
		logger.log(priority, message);
	}

	public void log(String callerFQCN, Priority level, Object message, Throwable t) {
		logger.log(callerFQCN, level, message, t);
	}

	public void warn(Object message) {
		logger.warn(message);
	}

	public void warn(Object message, Throwable t) {
		logger.warn(message, t);
	}

	public void info(Object message) {
		logger.info(message);
	}

	public void info(Object message, Throwable t) {
		logger.info(message, t);
	}

	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}


	public void e(Object message) { error(message); }

	public void w(Object message) { warn(message); }

	public void i(Object message) { info(message); }

	public void d(Object message) { debug(message); }

	public void t(Object message) { trace(message); }
}
