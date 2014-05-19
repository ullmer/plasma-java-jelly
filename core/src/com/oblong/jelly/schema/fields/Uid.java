package com.oblong.jelly.schema.fields;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.schema.HasToSlaw;
import com.oblong.jelly.slaw.java.SlawString;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * User: karol
 * Date: 11/18/13
 * Time: 4:34 PM
 */
public class Uid<T extends HasUid> implements HasToSlaw<SlawString> {
	public final String uid;
	public final Class<T> targetClass;

	public Uid(Class<T> targetClass, String uid) {
		if ( uid == null
//				|| "".equals(uid)
				) {
			throw new IllegalArgumentException("uid cannot be: [" + uid + "]");
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
//		return "(Uid " + uid + " with targetClass " + targetClass + ")";
		return uid; // basic form, because it is sometimes sent to native
	}

	public String toParsableString() {
		return this.uid;
	}

	public boolean safeEquals(Uid<T> uid) {
		return equals(uid);
	}

	/**
	 * _A_cquire _a_ Uid object
	 */
	public static <T extends HasUid> Uid<T> a(Class<T> targetClass, String uidString) {
		return new Uid<T>(targetClass, uidString);
	}

	@Override
	public SlawString toSlaw() {
		return Slaw.string(this.uid);
	}

//	public <T2 extends T> Uid<T2> upCastTo(T2 hasUid) {
//		return (Uid<T2>) this;
//	}
//
//	public <T2 extends T> Uid<T2> upCast2() {
//		return (Uid<T2>) this;
//	}
//
//	public static <T1 extends HasUid, T2 extends HasUid> Uid<T2> upCast(T1 uid, Class<T2> cl) {
//		return (Uid<T2>) uid;
//	}

	/** Useful for debugging */
	public static Set<String> getUids(Collection<? extends HasUid> items) {
		Set<String> uids = new LinkedHashSet<String>();
		for (HasUid item : items) {
			uids.add(item.getUid().toParsableString());
//			if ( ! uProt.getWorkspaceUids().contains(workspace.getUid() + "\n")) {
//				throw new RuntimeException("Workspace uid missing : ")
//			workspaceUids.append(workspace.getUid());
//			workspaceUids.append("\n");
		}
		return uids;
	}

}
