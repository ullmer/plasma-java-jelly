package com.oblong.jelly.schema.fields;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.SlawSchema;
import com.oblong.jelly.schema.UnmarshalledSlaw;
import com.oblong.jelly.slaw.java.SlawMap;

import java.util.Map;

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
public abstract class JellyField <T> {

	private final String name;
	private final SlawSchema schema;
	private final Slaw nameSlaw;

	public JellyField(String name) {
		this(name, null);
	}

	public JellyField(String name, SlawSchema schema) {
		this.name = name;
		this.schema = schema;
		this.nameSlaw = Slaw.string(name);
		if ( schema != null ) {
			schema.add(this);
		}
	}

	public String getName() {
		return name;
	}

	public final T getFrom(SlawMap mapSlaw) {
		return getCustom(mapSlaw.emitMap().get(nameSlaw));
	}

//	public Type get(Protein protein) {
//		return getCustom(protein.emitMap().get(nameSlaw));
//	}

	protected abstract T getCustom(Slaw slaw);

	public Slaw getNameSlaw() {
		return nameSlaw;
	}

	public abstract Slaw toSlaw(T value);


	@Override
	public String toString() {
		return super.toString() + ";name=" + getName();
	}

	public void set(UnmarshalledSlaw unmarshalledSlaw, T value) {
		unmarshalledSlaw.put(this, value);
	}

	/**
	 * Used as a marker, that a similar field, with the same name, is being reused in another schema
	 */
	public String reuseName() {
		return getName();
	}

	public T getFrom(Map<Slaw,Slaw> map) {
		return getCustom(map.get(getNameSlaw()));
	}
}
