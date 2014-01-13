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
 */
public abstract class AbstractField<T> {

	private final String name;
	private final SlawSchema schema;
	private final Slaw nameSlaw;

	public AbstractField(String name) {
		this(name, null);
	}

	public AbstractField(String name, SlawSchema schema) {
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

	public final T getFrom(final SlawMap mapSlaw) {
		Slaw slaw = mapSlaw.emitMap().get(nameSlaw);
		// NOTE: this mapSlaw.emitMap() when called repeatedly through this method, could be a performance problem
		// thus, with repeated access, it's better to use getFrom(Map<Slaw,Slaw> map)
		if ( slaw == null ) {
			return null;
		}
		return getCustom(slaw);
	}

//	public Type get(Protein protein) {
//		return getCustom(protein.emitMap().get(nameSlaw));
//	}


	/**
	 * This method has protected access in order to enforce the binding between the name and the type.
	 * Otherwise it would be possible to get the value from _any_ slaw via this method and we don't want this.
	 */
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
		return getCustom(getRawSlawFrom(map));
	}

	public Slaw getRawSlawFrom(Map<Slaw,Slaw> map) {
		return map.get(getNameSlaw());
	}
}
