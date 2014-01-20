package com.oblong.jelly.schema.fields;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.SlawSchema;

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 9/2/13
 * Time: 1:16 PM
 */
public class IntField extends AbstractField<Integer> {

	public IntField(SlawSchema schema, boolean isOptional, String name) {
		super(schema, isOptional, name);
	}

	public IntField(String name) {
		super(null, false, name);
	}

	@Override
	protected Integer getCustom(Slaw slaw) {
		return slaw.emitInt();
	}

	@Override
	public Slaw toSlaw(Integer value) {
		return Slaw.int32(value); // not sure if 32 bits
	}
}
