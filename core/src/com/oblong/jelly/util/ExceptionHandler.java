package com.oblong.jelly.util;

/**
 * Created with IntelliJ IDEA.
 * User: valeria
 * Date: 8/21/13
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ExceptionHandler {

	private static volatile ExceptionHandler exceptionHandler;

	private static boolean DEFAULT_HANDLER_PRINTS = false;
	private static boolean DEFAULT_HANDLER_RETHROWS = false;

	public static void handleException(Throwable e)  {
		handleException(e, " Unspecified ");
	}

	public static void handleException(Throwable e, String duringMsg)  {
		if (exceptionHandler != null) {
			String msg = createMessage(duringMsg);
			exceptionHandler.handleExceptionImpl(e, msg);
		} else {
			defaultHandleException(e, duringMsg);
		}
	}

	private static String createMessage(String duringMsg) {
		return "Exception during : " + duringMsg;
	}

	private static void defaultHandleException(Throwable e, String duringMsg) {
		if ( DEFAULT_HANDLER_PRINTS ) {
			System.err.println("========= " + createMessage(duringMsg));
			if ( e != null ) {
				e.printStackTrace();
			}
			System.err.println("(no installed ExceptionHandler)");
		}
		if ( DEFAULT_HANDLER_RETHROWS ) {
			throw new RuntimeException(createMessage(duringMsg),e);
		}
	}

	public abstract void handleExceptionImpl(Throwable e, String syntheticMsg) ;

	public static void setExceptionHandler(ExceptionHandler exceptionHandlerParam) {
		exceptionHandler = exceptionHandlerParam;
	}

	public static <T> void assertEquals(String message, T expected, T actual)  {
		if ( ! expected.equals(actual) ) { /* TODO: use safeEquals */
			handleException("assertEquals failed: expected: " + expected + " ; actual: " + actual+" message : "+message);
		}
	}

	public static void handleException(String msg)  {
		handleException(new JustToGetStackTrace(), msg);
	}

	public static void handleError(String message, Object details) {
		handleException(message + "; details: " + details);
	}

	public static class JustToGetStackTrace extends Exception { /*nothing*/ }

	public static void testException() {
		try {
			throw new Exception("testing exception");
		} catch ( Exception e ) {
			ExceptionHandler.handleException(e);
		}
	}


}
