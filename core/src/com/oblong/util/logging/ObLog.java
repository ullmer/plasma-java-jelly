package com.oblong.util.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * User: karol
 * Date: 1/23/14
 * Time: 1:56 PM
 *
 * Note: the assymetries and non-uniformity of method names/existence is intentional, to discourage misuse.
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

	public void assertLog(boolean assertion, String msg) { logger.assertLog(assertion, msg); }

	public boolean isEnabledFor(Priority level) { return logger.isEnabledFor(level); }

	public void log(Priority priority, Object message, Throwable t) { logger.log(priority, message, t); }

	public void log(Priority priority, Object message) { logger.log(priority, message); }

//	public void log(String callerFQCN, Priority level, Object message, Throwable t) {
//		logger.log(callerFQCN, level, message, t);
//	}

	public void fatal(CharSequence message) { f(message); }
	public void fatal(CharSequence message, Throwable t) { f(message, t); }
	public void error(CharSequence message) { e(message); }
	public void error(CharSequence message, Throwable t) { e(message, t); }
	public void warn(CharSequence message) { w(message); }
	public void warn(CharSequence message, Throwable t) { w(t, message); }

//	public boolean f() { return true; /* not present-discouraged */ }
//	public boolean e() { return true; /* not present-discouraged */ }
	public boolean w() { return logger.isEnabledFor(Level.WARN); }
	public boolean i() { return logger.isInfoEnabled(); }
	public boolean d() { return logger.isDebugEnabled(); }
	public boolean t() { return logger.isTraceEnabled(); }

	public void f(CharSequence message) { logger.fatal(message); }
	public void e(CharSequence message) { logger.error(message); }
	public void w(CharSequence message) { logger.warn(message); }
	public void i(CharSequence message) { logger.info(message); }
	public void d(CharSequence message) { logger.debug(message); }
	public void t(CharSequence message) { logger.trace(message); }

	public void f(CharSequence prefix, Object message) { f(prefix + ": " + message); }
	public void e(CharSequence prefix, Object message) { e(prefix + ": " + message); }
	public void w(CharSequence prefix, Object message) { w(prefix + ": " + message); }
	public void i(CharSequence prefix, Object message) { i(prefix + ": " + message); }
	public void d(CharSequence prefix, Object message) { d(prefix + ": " + message); }
	public void t(CharSequence prefix, Object message) { t(prefix + ": " + message); }

	public void f(Throwable t, CharSequence msg) { logger.fatal(msg, t); };
	public void e(Throwable t, CharSequence msg) { logger.error(msg, t); };
	public void w(Throwable t, CharSequence msg) { logger.warn (msg, t); };
	public void i(Throwable t, CharSequence msg) { logger.info (msg, t); };
	public void d(Throwable t, CharSequence msg) { logger.debug(msg, t); };
	public void t(Throwable t, CharSequence msg) { logger.trace(msg, t); };

	public void f(CharSequence msg, Throwable t) { logger.fatal(msg, t); };
	public void e(CharSequence msg, Throwable t) { logger.error(msg, t); };
	public void w(CharSequence msg, Throwable t) { logger.warn (msg, t); };
	public void i(CharSequence msg, Throwable t) { logger.info (msg, t); };
	public void d(CharSequence msg, Throwable t) { logger.debug(msg, t); };
	public void t(CharSequence msg, Throwable t) { logger.trace(msg, t); };

}
