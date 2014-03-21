package com.oblong.jelly;

/**
 * Created with IntelliJ IDEA.
 * User: valeria
 * Date: 3/21/14
 * Time: 7:23 PM
 */
public class TLSException extends ProtocolException {
	private static final String MESSAGE = "TLS is not supported yet";

	public TLSException() {
		super(MESSAGE);
	}
}
