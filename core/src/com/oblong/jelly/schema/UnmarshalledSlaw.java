package com.oblong.jelly.schema;

import com.oblong.jelly.communication.OttoEvent;
import com.oblong.jelly.schema.fields.AbstractField;
import com.oblong.jelly.slaw.MapSlawToSlaw;
import com.oblong.jelly.slaw.java.SlawMap;

/**
 * Created with IntelliJ IDEA.
 *
 * FIXME remove this class?
 *
 * User: karol
 * Date: 9/5/13
 * Time: 3:50 PM
 */
public abstract class UnmarshalledSlaw implements HasToSlaw, OttoEvent {

	/** FIXME remove this */
	protected MapSlawToSlaw mapSlawToSlaw = new MapSlawToSlaw();


	/** FIXME remove this */
	public <T> void put(AbstractField<T> field, T value) {
		if ( getSchema().has(field) ) {
			mapSlawToSlaw.put(field, value);
		} else {
			throw new RuntimeException("My schema does not have this field: " + field);
		}
	}

	/** FIXME remove this */
	public abstract SlawSchema getSchema();

	/* FIXME remove this */
	public SlawMap toSlaw() {
		return mapSlawToSlaw.toSlaw();
	}
}
