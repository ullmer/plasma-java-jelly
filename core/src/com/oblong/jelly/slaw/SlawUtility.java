package com.oblong.jelly.slaw;

import com.oblong.jelly.Slaw;

/**
	 * Created with IntelliJ IDEA.
	 * User: valeria
	 * Date: 10/2/13
	 * Time: 1:26 PM
	 */
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
	}