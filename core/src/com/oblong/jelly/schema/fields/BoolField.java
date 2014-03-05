package com.oblong.jelly.schema.fields;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.SlawSchema;
import com.oblong.util.ExceptionHandler;
import com.oblong.util.logging.ObLog;

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 7/12/13
 * Time: 2:29 PM
 *
 */
public class BoolField extends AbstractField<Boolean> {

	private final ObLog log = ObLog.get(this);

	public BoolField(String name) {
		super(name);
	}

	public BoolField(SlawSchema schema, boolean isOptional, String name) {
		super(schema, isOptional, name);
	}

	@Override
	protected Boolean fromSlaw_Custom(Slaw slaw) {
		if ( slaw.isBoolean() ) {
			return slaw.emitBoolean();
		} else {
			if ( slaw.isString() ) {
				String str = slaw.emitString();
				ExceptionHandler.handleException("Expected bool but got string. Will try to parse: " + str);
				/*--*/ if ( "true".equals(str) ) {
					logBoolFromStringWarning(str);
					return true;
				} else if ( "false".equals(str) ) {
					logBoolFromStringWarning(str);
					return false;
				}
			}
		}
//		return slaw.emitBoolean();
		throw new RuntimeException("Unable to convert slaw to bool. Field : " + BoolField.this
				+ "; slaw:" + slaw);
	}

	private void logBoolFromStringWarning(String str) {
		log.e("Bool from string '"+str+"', for field " + toString() + " - will work, but is discouraged.");
	}

	@Override
	public Slaw toSlaw_Custom(Boolean value) {
		return Slaw.bool(value);
	}
}
