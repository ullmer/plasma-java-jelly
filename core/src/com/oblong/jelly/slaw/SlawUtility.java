package com.oblong.jelly.slaw;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.slaw.java.*;

public class SlawUtility {
	public static Slaw arrayCons(String s, byte[] e) {
		return Slaw.cons(Slaw.string(s), Slaw.array(e, false));
	}

	public static Slaw float64Cons(String s, float f) {
		return Slaw.cons(Slaw.string(s), Slaw.float64(f));
	}

	public static Slaw int32Cons(String s, int i) {
		return Slaw.cons(Slaw.string(s), Slaw.int32(i));
	}

	public static Slaw int16Cons(String s, int i) {
		return Slaw.cons(Slaw.string(s), Slaw.int16(i));
	}

	public static Slaw strCons(String s0, String s1) {
		return Slaw.cons(Slaw.string(s0), Slaw.string(s1));
	}

	public static SlawList list(String ... strings) {
		if (strings == null) {
			return null;
		}
		Slaw[] slaws = new Slaw[strings.length];
		for (int i = 0 ; i < strings.length ; i++) {
			slaws[i] = Slaw.string(strings[i]);
		}
		return SlawList.list(slaws);
	}

	public static String[] fromSlawStringList(SlawList slawStringList) {
		if (slawStringList == null) {
			return new String[0];
		}
		String[] strings = new String[slawStringList.count()];

		int i = 0;

		for (Slaw slaw : slawStringList) {
			strings[i++] = slaw.emitString();
		}

		return strings;
	}
}
