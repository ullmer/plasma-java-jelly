package com.oblong.jelly.schema.fields;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.SlawSchema;

/**
 * User: karol
 * Date: 11/18/13
 * Time: 5:43 PM
 */
public class UidField<T extends HasUid> extends AbstractField<Uid<T>> {

	public <T> UidField(Class<T> tClass, String name) {
		this(tClass, name, null);
	}

	public <T> UidField(Class<T> tClass, String name, SlawSchema schema) {
		super(name, schema);
	}

	@Override
	protected Uid<T> getCustom(Slaw slaw) {
		return new Uid<T> ( slaw.emitString() );
	}

	@Override
	public Slaw toSlaw(Uid<T> value) {
		return Slaw.string(value.uid);
	}

	public static <T extends HasUid> UidField<T> get(Class<T> tClass, String name) {
		return get(null, name, tClass);
	}

	public static <T extends HasUid> UidField<T> get(SlawSchema slawSchema, String name, Class<T> tClass) {
		return new UidField<T>(tClass, name, slawSchema);
	}
}
