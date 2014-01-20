package com.oblong.jelly.schema.fields;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.SlawSchema;

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 7/12/13
 * Time: 2:29 PM
 *
 */
public class BoolField extends AbstractField<Boolean> {

	public BoolField(String name) {
		super(name);
	}

	public BoolField(SlawSchema schema, boolean isOptional, String name) {
		super(schema, isOptional, name);
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
