package com.oblong.jelly.schema;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.fields.JellyField;
import com.oblong.jelly.slaw.MapSlawToSlaw;
import com.oblong.jelly.slaw.java.SlawMap;

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 9/5/13
 * Time: 3:50 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class UnmarshalledSlaw implements HasToSlaw {

	protected MapSlawToSlaw mapSlawToSlaw = new MapSlawToSlaw();


	public <T> void put(JellyField<T> field, T value) {
		if ( getSchema().has(field) ) {
			mapSlawToSlaw.put(field, value);
		} else {
			throw new RuntimeException("My schema does not have this field: " + field);
		}
	}

	public abstract SlawSchema getSchema();

	public SlawMap toSlaw() {
		return mapSlawToSlaw.toSlaw();
	}
}
