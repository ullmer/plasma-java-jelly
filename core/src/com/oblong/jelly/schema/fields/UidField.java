package com.oblong.jelly.schema.fields;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.SlawSchema;

/**
 * User: karol
 * Date: 11/18/13
 * Time: 5:43 PM
 */
public class UidField<T extends HasUid> extends AbstractField<Uid<T>> {

	private final Class<T> tClass;

	public UidField(Class<T> tClass, String name) {
		this(null, false, name, tClass);
	}

	public UidField(SlawSchema schema, boolean isOptional, String name, Class<T> tClass) {
		super(schema, isOptional, name);
		this.tClass = tClass;
	}

	@Override
	protected Uid<T> fromSlaw_Custom(Slaw slaw) {
		return Uid.a(tClass, slaw.emitString());
	}

	@Override
	protected Slaw toSlaw_Custom(Uid<T> value) {
		return Slaw.string(value.uid);
	}

//	public static <T extends HasUid> UidField<T> get(Class<T> tClass, String name) {
//		return get(null, false, name, tClass);
//	}

	public static <T extends HasUid> UidField<T> get(SlawSchema slawSchema, boolean isOptional, String name, Class<T> tClass) {
		return new UidField<T>(slawSchema, isOptional, name, tClass);
	}
}
