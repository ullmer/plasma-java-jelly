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

	private final Object loggingObject;
	/**
	 * Using composition instead of inheritance because of the complexity of creating a subclass
	 * of org.apache.log4j.Logger, due to e.g. interactions with LoggerRepository and static factory methods.
	 */
	protected final Logger logger;

	protected ObLog(Class<?> aClass) {
		this(getLoggerForClass(aClass), null);
	}

	private static Logger getLoggerForClass(Class<?> aClass) {
		return Logger.getLogger(aClass);
	}

	protected ObLog(Logger logger, Object loggingObject) {
		this.logger = logger;
		this.loggingObject = loggingObject;
	}

	public static ObLog get(Class<?> aClass) {
		return new ObLog(aClass);
	}

	public static ObLog get(Object o) {
//		loggingObject = o;
//		String loggerName;
//		if (o instanceof LoggingObject) {
//			LoggingObject loggingObject = (LoggingObject) o;
////			loggerName = loggingObject.getLoggerName();
//		} else {
//			loggerName = o.toString();
//		}
		// later we might also show the address of the object to allow distinguishing of object instances
		return new ObLog(getLoggerForClass(o.getClass()), o);


//		return new ObLog(Logger.getLogger("_"), o); /* not the real logger, since we don't want to repeat the whole package
//				name and class name in log text */
	}

	private CharSequence addExtraInfo(CharSequence message) {
		if ( loggingObject == null ) {
			return message;
		} else {
			String loggingObjectString = "" + loggingObject;
			loggingObjectString = loggingObjectString.replace(logger.getName(), "#"); /* too avoid FQCN twice. #@
					to have a visual pattern */
			return loggingObjectString + " ==>  " + message;
		}
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

	public void f(CharSequence message) { logger.fatal(addExtraInfo(message)); }
	public void e(CharSequence message) { logger.error(addExtraInfo(message)); }

	public void w(CharSequence message) { logger.warn(addExtraInfo(message)); }
	public void i(CharSequence message) { logger.info(addExtraInfo(message)); }
	public void d(CharSequence message) { logger.debug(addExtraInfo(message)); }
	public void t(CharSequence message) { logger.trace(addExtraInfo(message)); }

	public void f(CharSequence prefix, Object message) { f(addExtraInfo(PrefixMessage(prefix, message))); }

	private String PrefixMessage(CharSequence prefix, Object message) {
		return prefix + ": " + message;
	}

	public void e(CharSequence prefix, Object message) { e(addExtraInfo(PrefixMessage(prefix, message))); }
	public void w(CharSequence prefix, Object message) { w(addExtraInfo(PrefixMessage(prefix, message))); }
	public void i(CharSequence prefix, Object message) { i(addExtraInfo(PrefixMessage(prefix, message))); }
	public void d(CharSequence prefix, Object message) { d(addExtraInfo(PrefixMessage(prefix, message))); }
	public void t(CharSequence prefix, Object message) { t(addExtraInfo(PrefixMessage(prefix, message))); }

	public void f(Throwable t, CharSequence message) { logger.fatal(addExtraInfo(message), t); };
	public void e(Throwable t, CharSequence message) { logger.error(addExtraInfo(message), t); };
	public void w(Throwable t, CharSequence message) { logger.warn (addExtraInfo(message), t); };
	public void i(Throwable t, CharSequence message) { logger.info (addExtraInfo(message), t); };
	public void d(Throwable t, CharSequence message) { logger.debug(addExtraInfo(message), t); };
	public void t(Throwable t, CharSequence message) { logger.trace(addExtraInfo(message), t); };

	public void f(CharSequence message, Throwable t) { logger.fatal(addExtraInfo(message), t); };
	public void e(CharSequence message, Throwable t) { logger.error(addExtraInfo(message), t); };
	public void w(CharSequence message, Throwable t) { logger.warn (addExtraInfo(message), t); };
	public void i(CharSequence message, Throwable t) { logger.info (addExtraInfo(message), t); };
	public void d(CharSequence message, Throwable t) { logger.debug(addExtraInfo(message), t); };
	public void t(CharSequence message, Throwable t) { logger.trace(addExtraInfo(message), t); };

}
