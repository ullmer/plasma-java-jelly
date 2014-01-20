package com.oblong.jelly.schema.fields;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.SlawSchema;
import com.oblong.jelly.slaw.java.SlawMap;

/**
 *
 * TODO: consider making it "extends AbstractField<T>", for consistency with ListField
 *
 * TODO: consider making a relationship between MapField and SlawSchema, maybe through AbstractField[] fields
 *
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 7/10/13
 * Time: 1:38 PM
 */
public class MapField extends AbstractField<SlawMap> {

	public MapField(SlawSchema schema, boolean isOptional, String name, AbstractField[] fields) {
		super(schema, isOptional, name);
	}

	public MapField(String name) {
		super(name);
	}

	@Override
	protected SlawMap getCustom(Slaw slaw) {
		return (SlawMap) slaw;
	}

	@Override
	public Slaw toSlaw(SlawMap value) {
		return value;
	}
}
