package com.oblong.jelly.slaw;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.fields.JellyField;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 9/2/13
 * Time: 1:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class MapSlawToSlaw {
	protected Map<Slaw, Slaw> map = new HashMap<Slaw,Slaw>();

	public <T> void put(JellyField<T> field, T value) {
		map.put(field.getNameSlaw(), field.toSlaw(value));
	}

	public Slaw toSlaw() {
		return Slaw.map(this.map);
	}
}
