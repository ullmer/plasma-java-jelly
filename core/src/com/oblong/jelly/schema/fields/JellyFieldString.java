package com.oblong.jelly.schema.fields;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.SlawSchema;

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 7/10/13
 * Time: 1:33 PM
 */
public class JellyFieldString extends JellyField<String> {

	public JellyFieldString(String name) {
		super(name);
	}

	public JellyFieldString(String name, SlawSchema schema) {
		super(name, schema);
	}

	@Override
	protected String getCustom(Slaw slaw) {
		return slaw.emitString();
	}

	@Override
	public Slaw toSlaw(String value) {
		return Slaw.string(value);
	}
}
