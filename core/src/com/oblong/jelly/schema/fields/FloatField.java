package com.oblong.jelly.schema.fields;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.SlawSchema;

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 9/3/13
 * Time: 6:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class FloatField extends AbstractField<Float> {

	public FloatField(String name) {
		this(name, null);
	}

	public FloatField(String name, SlawSchema schema) {
		super(name, schema);
	}

	@Override
	protected Float getCustom(Slaw slaw) {
		return slaw.emitFloat();
	}

	@Override
	public Slaw toSlaw(Float value) {
		return Slaw.float32(value);
	}
}
