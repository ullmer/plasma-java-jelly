package com.oblong.util;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * Created with IntelliJ IDEA.
 * User: valeria
 * Date: 10/25/13
 * Time: 7:33 PM
 */
public class Log4JExperiments {

	private static final Logger logger = Logger.getLogger(Log4JExperiments.class);

	public static void main(String[] args) {
		DOMConfigurator.configure("tests_log4j_conf.xml");

//		ConsoleAppender appender = new ConsoleAppender(new PatternLayout("%-5p [%t]: %m%n"));
//		ConsoleAppender appender = new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN));
//		Logger.getRootLogger().addAppender(appender);
//		Logger.getRootLogger().setLevel(Level.INFO);
		logger.debug("Example on debug level");
		logger.trace("Example on trace level");
		logger.trace("Example on trace level");
		logger.trace("Example on trace level");
		logger.trace("Example on trace level");
		logger.trace("Example on trace level");
		logger.trace("Example on trace level");
		logger.trace("Example on trace level");
		logger.fatal("Example on fatal level");
		testException();

	}

	private static void testException() {
		logger.error("Test EXCEPTION", new RuntimeException(new RuntimeException("testing")));
	}

}
