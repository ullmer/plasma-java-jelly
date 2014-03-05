package com.oblong.jelly.schema.fields;

import com.oblong.jelly.ISlawMap;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.SlawSchema;
import com.oblong.jelly.schema.UnmarshalledSlaw;

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
	private final boolean isOptional;

	public AbstractField(String name) {
		this(null, false, name);
	}

	public AbstractField(SlawSchema schema, boolean isOptional, String name) {
		this.schema = schema;
		this.isOptional = isOptional;
		this.name = name;
		this.nameSlaw = Slaw.string(name);
		if ( schema != null ) {
			schema.add(this);
		}
	}

	public String getName() {
		return name;
	}

	public final T getFrom(final ISlawMap mapSlaw) {
		if (mapSlaw==null) {
			return null;
		}

		Slaw slaw = mapSlaw.emitContainedMap().get(nameSlaw);
		// NOTE: this mapSlaw.emitMap() when called repeatedly through this method, could be a performance problem
		// thus, with repeated access, it's better to use getFrom(Map<Slaw,Slaw> map)
		if ( slaw == null ) {
			return null;
		}
		return fromSlaw_Custom(slaw);
	}

//	public Type get(Protein protein) {
//		return getCustom(protein.emitMap().get(nameSlaw));
//	}


	/**
	 * This method has protected access in order to enforce the binding between the name and the type.
	 * Otherwise it would be possible to get the value from _any_ slaw via this method and we don't want this.
	 */
	protected abstract T fromSlaw_Custom(Slaw slaw);

	public Slaw getNameSlaw() {
		return nameSlaw;
	}

	public final Slaw toSlaw(T value) {
		checkValue(value);
		if (value == null ) {
			return Slaw.nil();
		}
		Slaw slaw = toSlaw_Custom(value);
		return slaw;
	}

	protected abstract Slaw toSlaw_Custom(T value);

	private final void checkValue(T value) {
		if (value == null && !isOptional) {
			throw throwNullValException(value);
		}
	}


	@Override
	public String toString() {
		return "{" + getClass().getSimpleName() + ";name=" + getName() + "}";
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
		if (map==null) {
			return null; // not yet totally sure if this case should be fatal or allowed
		}
		Slaw rawSlaw = getRawSlawFrom(map);
		return getFromRawSlaw(rawSlaw);
	}

	protected T getFromRawSlaw(Slaw rawSlaw) {
		if (rawSlaw == null ) {
			if ( isOptional ) {
				return null; // not calling custom to avoid null-check hassle for subclasses
			} else {
				throw throwNullValException(rawSlaw);
			}
		}
		return fromSlaw_Custom(rawSlaw);
	}

	public Slaw getRawSlawFrom(Map<Slaw,Slaw> map) {
		if (map==null) {
//			return null;
			throw new IllegalArgumentException("Map<Slaw,Slaw> is null. Cannot retrieve this field " + this);
		}
		Slaw rawSlaw = map.get(getNameSlaw());
		if (rawSlaw == null ) {
			if ( isOptional ) {
				return null;
//				return Slaw.nil(); // correct way to express null value in jelly?
			} else {
				throw throwNullValException(rawSlaw);
			}
		}
		return rawSlaw;
	}

	private IllegalArgumentException throwNullValException(Object valueOrSlaw) {
		throw new IllegalArgumentException(
				"This field, " + this + ", is not optional and therefore the value cannot be " + valueOrSlaw);
	}

}
