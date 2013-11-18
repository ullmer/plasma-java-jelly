package com.oblong.jelly.schema.fields;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.SlawSchema;

/**
 * User: karol
 * Date: 11/18/13
 * Time: 5:43 PM
 */
public class JellyFieldUid <T extends HasUid> extends JellyField<Uid<T>> {

	public JellyFieldUid(String name) {
		super(name);
	}

	public JellyFieldUid(String name, SlawSchema schema) {
		super(name, schema);
	}

	@Override
	public Uid<T> getCustom(Slaw slaw) {
		return new Uid<T> ( slaw.emitString() );
	}

	@Override
	public Slaw toSlaw(Uid<T> value) {
		return Slaw.string(value.uid);
	}
}
