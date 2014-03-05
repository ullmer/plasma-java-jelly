package com.oblong.jelly.schema.fields;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.SlawSchema;

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 7/10/13
 * Time: 1:33 PM
 */
public class StringField extends AbstractField<String> {

	public StringField(String name) {
		this(null, false, name);
	}

	public StringField(SlawSchema schema, boolean isOptional, String name) {
		super(schema, isOptional, name);
	}

	@Override
	protected String fromSlaw_Custom(Slaw slaw) {
		return slaw.emitString();
	}

	@Override
	public Slaw toSlaw_Custom(String value) {
		return Slaw.string(value);
	}
}
