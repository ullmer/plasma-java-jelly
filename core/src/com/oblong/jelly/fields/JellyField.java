package com.oblong.jelly.fields;

import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.slaw.java.SlawMap;

/**
 * Helps in increasing type safety and brevity of usage of maps in Slaw-s
 * by "binding" key (name) to a particular Java type.
 *
 * Created with IntelliJ IDEA.
 * User: karol
 * Date: 7/10/13
 * Time: 1:31 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class JellyField <Type> {

	private final String name;
	private Slaw nameSlaw;

	public JellyField(String name) {
		this.name = name;
		this.nameSlaw = Slaw.string(name);
	}

	public String getName() {
		return name;
	}

	public final Type get(SlawMap mapSlaw) {
		return getCustom(mapSlaw.emitMap().get(nameSlaw));
	}

//	public Type get(Protein protein) {
//		return getCustom(protein.emitMap().get(nameSlaw));
//	}

	protected abstract Type getCustom(Slaw slaw);

}
