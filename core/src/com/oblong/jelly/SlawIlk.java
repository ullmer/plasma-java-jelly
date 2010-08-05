// Copyright (c) 2010 Oblong Industries
// Created: Fri Apr 23 14:14:56 2010

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
 * <p> Slaw ilks can be classified in groups, for which this class
 * provides predicates. Thus, we have, for instance, atomic ilks, or
 * vectors.
 *
 * <p> A group of ilks of particular importance is that of numeric
 * ilks, which includes slawx encapsulating a plain number
 * (corresponding to the {@code NUMBER} ilk) and composite slawx
 * encapsulating a fixed or variable number of them (which include
 * {@code COMPLEX}, several kinds of vectors and arrays). For numeric
 * ilks, the ilk by itself does not determine the type of the stored
 * data. A {@code NUMBER}, for instance, can be integral or floating
 * point, be signed or not, and use from 1 to 8 bytes to represent its
 * values. This information is described by means of another
 * enumeration, {@link NumericIlk}. Composite numeric slawx are
 * homogenous when it comes to their numeric ilk, which is derived
 * from that of its components (for instance, a Slaw with ilk {@code
 * COMPLEX} and numeric ilk {@code INT32} will be composed of two
 * slawx, each of them with ilk {@code NUMBER} and numeric ilk {@code
 * INT32}).
 *
 * <p> Thus, it is important to remember that the real type of the
 * data stored by a Slaw is not described, in general, by its SlawIlk
 * alone, but by that and its numeric ilk. For non-numeric ilks,
 * however, the corresponding numeric ilk is always null, and knowing
 * the ilk suffices to determine their real type.
 *
 * @see NumericIlk
 * @see Slaw
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
     * Checks whether this ilk belongs to the set returned by {@link
     * #atomicIlks}.
     */
    public boolean isAtomic() { return atoms.contains(this); }

    /**
     * Checks whether this ilk belongs to the set returned by {@link
     * #numericIlks}.
     */
    public boolean isNumeric() { return numeric.contains(this); }

    /**
     * Checks whether this ilk belongs to the set returned by {@link
     * #complexIlks}.
     */
    public boolean isComplexNumeric() { return complex.contains(this); }

    /**
     * Checks whether this ilk is either {@code NUMBER_VECTOR} or
     * {@code COMPLEX_VECTOR}.
     */
    public boolean isVector() {
        return this == NUMBER_VECTOR || this == COMPLEX_VECTOR;
    }

    /**
     * Checks whether this ilk belongs to the set returned by
     * {@link #arrayIlks}.
     */
    public boolean isArray() { return arrays.contains(this); }

    /**
     * The set of ilks for slawx that represent a single value. That
     * is, ilks for slawx that are not an aggregate of other slawx.
     * This set is composed of {@code NIL}, {@code BOOL}, {@code
     * STRING} and {@code NUMBER}.
     */
    public static Set<SlawIlk> atomicIlks() { return atoms.clone(); }

    /**
     * The set of ilks for slawx that represent a numeric value, or an
     * aggregate of them. This set is composed of {@code NUMBER},
     * {@code COMPLEX}, {@code NUMBER_VECTOR}, {@code COMPLEX_VECTOR},
     * {@code MULTI_VECTOR} and all the array ilks (contained in the
     * set returned by {@link #arrayIlks}).
     *
     * <p> As explained in this class description, the real type of a Slaw
     * whose ilk belongs to this set is determined by both its ilk and
     * its numeric ilk.
     */
    public static Set<SlawIlk> numericIlks() { return numeric.clone(); }

    /**
     * The set of numeric ilks whose components are complex numbers.
     * This set consists of {@code COMPLEX}, {@code COMPLEX_VECTOR},
     * {@code COMPLEX_ARRAY} and {@code COMPLEX_VECTOR_ARRAY}.
     */
    public static Set<SlawIlk> complexIlks() { return complex.clone(); }

    /**
     * The set of ilks representing arrays of numeric values. This set
     * includes {@code NUMBER_ARRAY}, {@code COMPLEX_ARRAY}, {@code
     * VECTOR_ARRAY}, {@code COMPLEX_VECTOR_ARRAY}, and {@code
     * MULTI_VECTOR_ARRAY}.
     */
    public static Set<SlawIlk> arrayIlks() { return arrays.clone(); }

    /**
     * Checks whether all given Slaw have the same ilk. Returns true
     * when called without arguments, and false if any of its
     * arguments is null (the rationale being that we cannot
     * determine, let alone compare, the ilk of null slawx).
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
