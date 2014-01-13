package com.oblong.jelly.schema.fields;

/**
 * User: karol
 * Date: 11/18/13
 * Time: 4:34 PM
 */
public class Uid<T extends HasUid> {
	public final String uid;
	public final Class<T> targetClass;

	protected Uid(Class<T> targetClass, String uid) {
		if ( uid == null || "".equals(uid) ) {
			throw new IllegalArgumentException("uid cannot be: [" + uid + " ]");
		}
		this.targetClass = targetClass;
		this.uid = uid;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Uid) {
			Uid o1 = (Uid) o;
			return this.uid.equals(o1.uid);
		} else {
			throw new RuntimeException("Probably bug, trying to compare this with object of other type: " + o);
		}
	}

	@Override
	public int hashCode() {
		return uid.hashCode();
	}

	@Override
	public String toString() {
		return "(Uid " + uid + " with targetClass " + targetClass + ")";
	}

	public boolean safeEquals(Uid uid) {
		return equals(uid);
	}

	/** Acquire Uid object */
	public static <T extends HasUid> Uid<T> a(Class<T> targetClass, String uidString) {
		return new Uid<T>(targetClass, uidString);
	}

}
