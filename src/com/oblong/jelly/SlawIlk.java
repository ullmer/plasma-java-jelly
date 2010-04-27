package com.oblong.jelly;

import java.util.EnumSet;

/**
 * Describe class SlawIlk here.
 *
 *
 * Created: Fri Apr 23 14:14:56 2010
 *
 * @author jao
 */
public enum SlawIlk {

    NIL, BOOL, STRING, NUMBER, COMPLEX,
    VECTOR, COMPLEX_VECTOR, MULTI_VECTOR,
    ARRAY, COMPLEX_ARRAY, VECTOR_ARRAY,
    COMPLEX_VECTOR_ARRAY, MULTI_VECTOR_ARRAY,
    CONS, LIST, MAP, PROTEIN;

    public boolean isAtomic() { return atoms.contains(this); }
    public boolean isNumeric() { return numeric.contains(this); }
    public boolean isVector() {
        return this == VECTOR || this == COMPLEX_VECTOR;
    }
    public boolean isArray() { return arrays.contains(this); }

    public static boolean haveSameIlk(Slaw... sx) {
        if (sx.length > 0) {
            SlawIlk ilk = sx[0].ilk();
            for (int i = 1; i < sx.length; i++)
                if (sx[i].ilk () != ilk) return false;
        }
        return true;
    }

    private static EnumSet<SlawIlk> atoms =
        EnumSet.of(NIL, BOOL, STRING, NUMBER);
    private static EnumSet<SlawIlk> numeric =
        EnumSet.of(NUMBER, COMPLEX,
                   VECTOR, COMPLEX_VECTOR, MULTI_VECTOR,
                   ARRAY, COMPLEX_ARRAY, VECTOR_ARRAY,
                   COMPLEX_VECTOR_ARRAY, MULTI_VECTOR_ARRAY);
    private static EnumSet<SlawIlk> arrays =
        EnumSet.of(ARRAY, COMPLEX_ARRAY, VECTOR_ARRAY,
                   COMPLEX_VECTOR_ARRAY, MULTI_VECTOR_ARRAY);
}
