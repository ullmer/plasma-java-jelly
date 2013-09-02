package com.oblong.jelly.fields;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.slaw.java.SlawMap;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 7/10/13
 * Time: 2:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class JellyFieldList extends JellyField<List<Slaw>> {

	public JellyFieldList(String name) {
		super(name);
	}

	@Override
	protected List<Slaw> getCustom(Slaw containingSlaw) {
		return containingSlaw.emitList();
	}

	@Override
	public Slaw toSlaw(List<Slaw> value) {
		return Slaw.list(value);
	}
}
