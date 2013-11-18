package com.oblong.jelly.schema.fields;

/**
 * User: karol
 * Date: 11/18/13
 * Time: 4:34 PM
 */
public class Uid<T extends HasUid> {
	public final String uid;

	public Uid(String uid) {
		if ( uid == null || "".equals(uid) ) {
			throw new IllegalArgumentException("uid cannot be: [" + uid + " ]");
		}
		this.uid = uid;
	}

	@Override
	public boolean equals(Object o) {
		if ( ! (o instanceof Uid) ) {
			throw new RuntimeException("Probably bug, trying to compare this with object of other type: " + o);
		}
		return uid.equals(o);
	}

	@Override
	public int hashCode() {
		return uid.hashCode();
	}

	@Override
	public String toString() {
		return uid;
	}

	public boolean safeEquals(Uid uid) {
		return equals(uid);
	}

}
