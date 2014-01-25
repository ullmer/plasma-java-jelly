package com.oblong.jelly.schema.fields;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.SlawSchema;
import com.oblong.jelly.slaw.java.SlawMap;

import java.util.Map;

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
public class MapField<T extends UnmarshalledSlawMap> extends AbstractField<T> {

	public MapField(SlawSchema schema, boolean isOptional, String name, AbstractField[] fields) {
		super(schema, isOptional, name);
	}

	public MapField(String name) {
		super(name);
	}

	@Override
	protected T getCustom(Slaw slaw) {
		throw new UnsupportedOperationException("This is handled in ProteinLang getters generation, instead of here," +
				"to avoid using reflection or having a need for bloated number of inner classes.");
	}

	@Override
	public SlawMap getRawSlawFrom(Map<Slaw, Slaw> map) {
		return (SlawMap) super.getRawSlawFrom(map);
	}

	@Override
	public SlawMap toSlaw(T value) {
		return value.toSlaw();
	}

}
