package com.oblong.jelly.fields;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.slaw.java.SlawMap;

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 7/10/13
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class JellyFieldMap extends JellyField<SlawMap> {

	public JellyFieldMap(String name) {
		super(name);
	}

	@Override
	protected SlawMap getCustom(Slaw slaw) {
		return (SlawMap) slaw;
	}
}
