package com.oblong.util.logging;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * User: karol
 * Date: 1/23/14
 * Time: 1:56 PM
 */
public class ObLog {

	/**
	 * Using composition instead of inheritance because of the complexity of creating a subclass
	 * of org.apache.log4j.Logger, due to e.g. interactions with LoggerRepository and static factory methods.
	 */
	protected final Logger logger;

	protected ObLog(Class<?> aClass) {
		this(Logger.getLogger(aClass));
	}

	protected ObLog(Logger logger) {
		this.logger = logger;
	}

	public static ObLog get(Class<?> aClass) {
		return new ObLog(aClass);
	}

	public static ObLog get(Object o) {
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

	public boolean f() { return true; }
	public boolean e() { return true; }
	public boolean w() { return true; }
	public boolean i() { return isInfoEnabled(); }
	public boolean d() { return isDebugEnabled(); }
	public boolean t() { return isTraceEnabled(); }

	public void f(Object message) { fatal(message); }
	public void e(Object message) { error(message); }
	public void w(Object message) { warn(message); }
	public void i(Object message) { info(message); }
	public void d(Object message) { debug(message); }
	public void t(Object message) { trace(message); }

	public void f(CharSequence prefix, Object message) { fatal(prefix + ": " + message); }
	public void e(CharSequence prefix, Object message) { error(prefix + ": " + message); }
	public void w(CharSequence prefix, Object message) { warn (prefix + ": " + message); }
	public void i(CharSequence prefix, Object message) { info (prefix + ": " + message); }
	public void d(CharSequence prefix, Object message) { debug(prefix + ": " + message); }
	public void t(CharSequence prefix, Object message) { trace(prefix + ": " + message); }

	public void f(Throwable t, Object msg) { fatal(msg, t); };
	public void e(Throwable t, Object msg) { error(msg, t); };
	public void w(Throwable t, Object msg) { warn (msg, t); };
	public void i(Throwable t, Object msg) { info (msg, t); };
	public void d(Throwable t, Object msg) { debug(msg, t); };
	public void t(Throwable t, Object msg) { trace(msg, t); };

	public void f(CharSequence msg, Throwable t) { fatal(msg, t); };
	public void e(CharSequence msg, Throwable t) { error(msg, t); };
	public void w(CharSequence msg, Throwable t) { warn (msg, t); };
	public void i(CharSequence msg, Throwable t) { info (msg, t); };
	public void d(CharSequence msg, Throwable t) { debug(msg, t); };
	public void t(CharSequence msg, Throwable t) { trace(msg, t); };
}
