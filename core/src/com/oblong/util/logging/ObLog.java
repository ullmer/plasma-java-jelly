package com.oblong.util.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
//import org.apache.logging.log4j.Level;
//import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.LogManager;

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

	/*
	protected ObLog(Class<?> aClass) {
		this(getLoggerForClass(aClass), null);
	}*/

	/* // BAU, 2024-08-06: yielding compile error, perhaps at intersection between log4j and Java8/21 evolution
	private static Logger getLoggerForClass(Class<?> aClass) {
		return Logger.getLogger(aClass);
	}*/

	protected ObLog(Logger logger, Object loggingObject) {
		this.logger = logger;
		this.loggingObject = loggingObject;
	}

	/*
	public static ObLog get(Class<?> aClass) {
		return new ObLog(aClass);
	}*/

  //	public static ObLog get(Object o) {
//		loggingObject = o;
//		String loggerName;
//		if (o instanceof LoggingObject) {
//			LoggingObject loggingObject = (LoggingObject) o;
////			loggerName = loggingObject.getLoggerName();
//		} else {
//			loggerName = o.toString();
//		}
		// later we might also show the address of the object to allow distinguishing of object instances
  //		return new ObLog(getLoggerForClass(o.getClass()), o);


//		return new ObLog(Logger.getLogger("_"), o); /* not the real logger, since we don't want to repeat the whole package
//				name and class name in log text */
  //	}


	private CharSequence addExtraInfo(CharSequence message) {
		if ( loggingObject == null ) {
			return message;
		} else {
			String loggingObjectString;
			if ( loggingObject instanceof LoggingObject ) {
				loggingObjectString = ((LoggingObject) loggingObject).toLoggingObjectString();
			} else {
				loggingObjectString = "" + loggingObject;
			}
			loggingObjectString = loggingObjectString.replace(logger.getName(), "#"); /* To avoid FQCN twice. Also,
					the resulting "#@" is useful as a visual pattern. */
			return loggingObjectString + " ==>  " + message;
		}
	}

	//public void assertLog(boolean assertion, String msg) { logger.assertLog(assertion, msg); }
	public void assertLog(boolean assertion, String msg) { 
           System.out.println("ObLog assertLog:" + msg);
	}		

	//public boolean isEnabledFor(Priority level) { return logger.isEnabledFor(level); }
	//public boolean isEnabledFor(Level level) { return logger.isEnabledFor(level); }
	public boolean isEnabledFor(Level level) { return true;}  // punting for moment

	//public void log(Priority priority, Object message, Throwable t) { logger.log(priority, message, t); }
	//public void log(Level priority, Object message, Throwable t) { logger.log(priority, message, t); }
	public void log(Level priority, Object message, Throwable t) { System.out.println("ObLog log 1: (further dev required)");}

	//public void log(Priority priority, Object message) { logger.log(priority, message); }
	//public void log(Level priority, Object message) { logger.log(priority, message); }
	public void log(Level priority, Object message) { System.out.println("ObLog log 2 (further dev required)");}

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
	//public boolean w() { return logger.isEnabledFor(Level.WARN); }
	//public boolean i() { return logger.isInfoEnabled(); }
	//public boolean d() { return logger.isDebugEnabled(); }
	//public boolean t() { return logger.isTraceEnabled(); }

	public boolean w() { return true; } //punting
	public boolean i() { return true; }
	public boolean d() { return true; }
	public boolean t() { return true; }

	//public void f(CharSequence message) { logger.fatal(addExtraInfo(message)); }
	//public void e(CharSequence message) { logger.error(addExtraInfo(message)); }

	public void f(CharSequence message) { System.out.println("ObLog f (further dev required)"); }
	public void e(CharSequence message) { System.out.println("ObLog e (further dev required)"); }

	//public void w(CharSequence message) { logger.warn(addExtraInfo(message)); }
	//public void i(CharSequence message) { logger.info(addExtraInfo(message)); }
	//public void d(CharSequence message) { logger.debug(addExtraInfo(message)); }
	//public void t(CharSequence message) { logger.trace(addExtraInfo(message)); }

	public void w(CharSequence message) { System.out.println("ObLog w (further dev required)");}
	public void i(CharSequence message) { System.out.println("ObLog i (further dev required)");}
	public void d(CharSequence message) { System.out.println("ObLog d (further dev required)");}
	public void t(CharSequence message) { System.out.println("ObLog t (further dev required)");}

	public void f(CharSequence prefix, Object message) { f(addExtraInfo(prefixMessage(prefix, message))); }

	private String prefixMessage(CharSequence prefix, Object message) {
		return prefix + ": " + message;
	}

	public void e(CharSequence prefix, Object message) { e(addExtraInfo(prefixMessage(prefix, message))); }
	public void w(CharSequence prefix, Object message) { w(addExtraInfo(prefixMessage(prefix, message))); }
	public void i(CharSequence prefix, Object message) { i(addExtraInfo(prefixMessage(prefix, message))); }
	public void d(CharSequence prefix, Object message) { d(addExtraInfo(prefixMessage(prefix, message))); }
	public void t(CharSequence prefix, Object message) { t(addExtraInfo(prefixMessage(prefix, message))); }

	//public void f(Throwable t, CharSequence message) { logger.fatal(addExtraInfo(message), t); };
	//public void e(Throwable t, CharSequence message) { logger.error(addExtraInfo(message), t); };
	//public void w(Throwable t, CharSequence message) { logger.warn (addExtraInfo(message), t); };
	//public void i(Throwable t, CharSequence message) { logger.info (addExtraInfo(message), t); };
	//public void d(Throwable t, CharSequence message) { logger.debug(addExtraInfo(message), t); };
	//public void t(Throwable t, CharSequence message) { logger.trace(addExtraInfo(message), t); };

	public void f(CharSequence message, Throwable t) { System.out.println("ObLog f2 (further dev required)");}
	public void f(Throwable t, CharSequence message) { System.out.println("ObLog f3 (further dev required)");}
	public void e(Throwable t, CharSequence message) { System.out.println("ObLog e2 (further dev required)");}
	public void w(Throwable t, CharSequence message) { System.out.println("ObLog w2 (further dev required)");}
	public void i(Throwable t, CharSequence message) { System.out.println("ObLog i2 (further dev required)");}
	public void d(Throwable t, CharSequence message) { System.out.println("ObLog d2 (further dev required)");}
	public void t(Throwable t, CharSequence message) { System.out.println("ObLog t2 (further dev required)");}

	//public void f(CharSequence message, Throwable t) { logger.fatal(addExtraInfo(message), t); };
	//public void e(CharSequence message, Throwable t) { logger.error(addExtraInfo(message), t); };
	//public void w(CharSequence message, Throwable t) { logger.warn (addExtraInfo(message), t); };
	//public void i(CharSequence message, Throwable t) { logger.info (addExtraInfo(message), t); };
	//public void d(CharSequence message, Throwable t) { logger.debug(addExtraInfo(message), t); };
	//public void t(CharSequence message, Throwable t) { logger.trace(addExtraInfo(message), t); };

	//public void f(CharSequence message, Throwable t) { System.out.println("ObLog f3 (further dev required)");}
	public void e(CharSequence message, Throwable t) { System.out.println("ObLog e3 (further dev required)");}
	public void w(CharSequence message, Throwable t) { System.out.println("ObLog w3 (further dev required)");}
	public void i(CharSequence message, Throwable t) { System.out.println("ObLog i3 (further dev required)");}
	public void d(CharSequence message, Throwable t) { System.out.println("ObLog d3 (further dev required)");}
	public void t(CharSequence message, Throwable t) { System.out.println("ObLog t3 (further dev required)");}

	public static boolean d(ObLog log) {
		if(log==null) return false;
		return log.d();
	}

}
