package com.oblong.jelly.schema.fields;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.SlawSchema;

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 7/12/13
 * Time: 2:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class JellyFieldBool extends JellyField<Boolean> {

	public JellyFieldBool(String name) {
		super(name);
	}

	public JellyFieldBool(String name, SlawSchema schema) {
		super(name, schema);
	}

	@Override
	protected Boolean getCustom(Slaw slaw) {
		return slaw.emitBoolean();
	}

	@Override
	public Slaw toSlaw(Boolean value) {
		return Slaw.bool(value);
	}
}
