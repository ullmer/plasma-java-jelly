package com.oblong.jelly.schema.fields;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.SlawSchema;

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 9/2/13
 * Time: 1:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class JellyFieldInt extends JellyField<Integer> {

	public JellyFieldInt(String name, SlawSchema schema) {
		super(name, schema);
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
