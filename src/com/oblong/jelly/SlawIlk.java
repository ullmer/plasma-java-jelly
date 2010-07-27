// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.EnumSet;
import java.util.Set;

/**
 * Enumeration discriminating the data types encapsulated in Slaw.
 * Slaw works as a type union, but data in pools is strongly typed,
 * and we need a way to discriminate among the many types the data
 * encapsulated in a Slaw can have. We call this data type the Slaw's
 * ilk, and enumerate its possible values in this enum.
 *
 * Slaw ilks can be classified in groups, for which this class
 * provides predicates. Thus, we have, for instance, atomic ilks, or
 * vectors.
 *
 * A group of ilks of particular importance is that of numeric ilks,
 * which includes Slawx encapsulating a plain number (corresponding to
 * the NUMBER ilk) and composite Slawx encapsulating a fixed or
 * variable number of them (which include COMPLEX, several kinds of
 * vectors and arrays). For numeric ilks, the ilk by itself does not
 * determine the type of the stored data. A NUMBER, for instance, can
 * be integral or floating point, be signed or not, and use from 1 to
 * 8 bytes to represent its values. This information is described by
 * means of another enumeration, NumericIlk. Composite numeric Slawx
 * are homogenous when it comes to their numeric ilk, which is derived
 * from that of its components (for instance, a Slaw with ilk COMPLEX
 * and numeric ilk INT32 will be composed of two Slawx, each of them
 * with ilk NUMBER and numeric ilk INT32).
 *
 * Thus, it is important to remember that the real type of the data
 * stored by a Slaw is not described, in general, by its SlawIlk
 * alone, but by that and its numeric ilk. For non-numeric ilks,
 * however, the corresponding numeric ilk is always null, and knowing
 * the ilk suffices to determine their real type.
 *
 * @see NumericIlk
 * @see Slaw
 *
 * Created: Fri Apr 23 14:14:56 2010
 *
 * @author jao
 */
public enum SlawIlk {

    NIL, BOOL, STRING, NUMBER, COMPLEX,
    NUMBER_VECTOR, COMPLEX_VECTOR, MULTI_VECTOR,
    NUMBER_ARRAY, COMPLEX_ARRAY, VECTOR_ARRAY,
    COMPLEX_VECTOR_ARRAY, MULTI_VECTOR_ARRAY,
    CONS, LIST, MAP, PROTEIN;

    /**
     * Checks whether this ilk belongs to the set returned by
     * SlawIlk#atomicIlks.
     */
    public boolean isAtomic() { return atoms.contains(this); }

    /**
     * Checks whether this ilk belongs to the set returned by
     * SlawIlk#numericIlks.
     */
    public boolean isNumeric() { return numeric.contains(this); }

    /**
     * Checks whether this ilk belongs to the set returned by
     * SlawIlk#complexIlks.
     */
    public boolean isComplexNumeric() { return complex.contains(this); }

    /**
     * Checks whether this ilk is either NUMBER_VECTOR or
     * COMPLEX_VECTOR.
     */
    public boolean isVector() {
        return this == NUMBER_VECTOR || this == COMPLEX_VECTOR;
    }

    /**
     * Checks whether this ilk belongs to the set returned by
     * SlawIlk#arrayIlks.
     */
    public boolean isArray() { return arrays.contains(this); }

    /**
     * The set of ilks for Slawx that represent a single value. That
     * is, ilks for Slawx that are not an aggregate of other Slawx.
     * This set is composed of NIL, BOOL, STRING and NUMBER.
     */
    public static Set<SlawIlk> atomicIlks() { return atoms.clone(); }

    /**
     * The set of ilks for Slawx that represent a numeric value, or an
     * aggreate of them. This set is composed of NUMBER, COMPLEX,
     * NUMBER_VECTOR, COMPLEX_VECTOR, MULTI_VECTOR and all the array
     * ilks (contained in the set returned by SlawIlk#arrayIlks()).
     *
     * As explained in this class description, the real type of a Slaw
     * whose ilk belongs to this set is determined by both its ilk and
     * its numeric ilk.
     */
    public static Set<SlawIlk> numericIlks() { return numeric.clone(); }

    /**
     * The set of numeric ilks whose components are complex numbers.
     * This set consists of COMPLEX, COMPLEX_VECTOR, COMPLEX_ARRAY and
     * COMPLEX_VECTOR_ARRAY.
     */
    public static Set<SlawIlk> complexIlks() { return complex.clone(); }

    /**
     * The set of ilks representing arrays of numeric values. This set
     * includes NUMBER_ARRAY, COMPLEX_ARRAY, VECTOR_ARRAY,
     * COMPLEX_VECTOR_ARRAY, and MULTI_VECTOR_ARRAY.
     */
    public static Set<SlawIlk> arrayIlks() { return arrays.clone(); }

    /**
     * Checks whether all given Slaw have the same ilk. Returns true
     * when called without arguments, and false if any of its
     * arguments is null (the rationale being that we cannot
     * determine, let alone compare, the ilk of null Slawx).
     */
    public static boolean haveSameIlk(Slaw... sx) {
        if (sx.length == 0) return true;
        if (sx[0] == null) return false;
        final SlawIlk ilk = sx[0].ilk();
        for (Slaw s : sx) if (s == null || s.ilk() != ilk) return false;
        return true;
    }

    private static EnumSet<SlawIlk> atoms =
        EnumSet.of(NIL, BOOL, STRING, NUMBER);
    private static EnumSet<SlawIlk> numeric =
        EnumSet.of(NUMBER, COMPLEX,
                   NUMBER_VECTOR, COMPLEX_VECTOR, MULTI_VECTOR,
                   NUMBER_ARRAY, COMPLEX_ARRAY, VECTOR_ARRAY,
                   COMPLEX_VECTOR_ARRAY, MULTI_VECTOR_ARRAY);
    private static EnumSet<SlawIlk> complex =
        EnumSet.of(COMPLEX, COMPLEX_VECTOR,
                   COMPLEX_ARRAY, COMPLEX_VECTOR_ARRAY);
    private static EnumSet<SlawIlk> arrays =
        EnumSet.of(NUMBER_ARRAY, COMPLEX_ARRAY, VECTOR_ARRAY,
                   COMPLEX_VECTOR_ARRAY, MULTI_VECTOR_ARRAY);
}
