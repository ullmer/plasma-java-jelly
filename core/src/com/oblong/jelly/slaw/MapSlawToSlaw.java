package com.oblong.jelly.slaw;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.HasToSlaw;
import com.oblong.jelly.schema.fields.AbstractField;
import com.oblong.jelly.slaw.java.SlawMap;

import java.util.HashMap;
import java.util.Map;

/**
 * User: karol
 * Date: 9/2/13
 * Time: 1:09 PM
 */
public class MapSlawToSlaw implements HasToSlaw<SlawMap> {
	protected Map<Slaw, Slaw> map = new HashMap<Slaw,Slaw>();

	public <T> void put(AbstractField<T> field, T value) {
		Slaw toSlaw = field.toSlaw(value);
		if ( toSlaw == null ) {
			toSlaw = Slaw.nil();
		}
		map.put(field.getNameSlaw(), toSlaw);

		// consider what happens if value == null ?
	}

	public void put(HasToSlaw<?> key, HasToSlaw<?> value) {
		map.put(key.toSlaw(), value.toSlaw());
	}

	public SlawMap toSlaw() {
		return Slaw.map(this.map);
	}
}
