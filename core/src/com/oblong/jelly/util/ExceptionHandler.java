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


	public static void handleException(Throwable e){
		handleException(e, " Unspecified ");
	}

	public static void handleException(Throwable e,String duringMsg) {
		String msg = "Exception occurred during : " + duringMsg;
		System.err.println("========= " + msg);
		e.printStackTrace();
		if ( exceptionHandler == null ) {
			System.err.println("(no installed ExceptionHandler)");
//			throw new RuntimeException(msg,e);
		} else {
			exceptionHandler.handleExceptionImpl(e, msg);
		}
	}

	public abstract void handleExceptionImpl(Throwable e, String syntheticMsg);

	public static void setExceptionHandler(ExceptionHandler exceptionHandlerParam) {
		exceptionHandler = exceptionHandlerParam;
	}
}
