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
 * (corresponding to the <code>NUMBER</code> ilk) and composite slawx
 * encapsulating a fixed or variable number of them (which include
 * <code>COMPLEX</code>, several kinds of vectors and arrays). For
 * numeric ilks, the ilk by itself does not determine the type of the
 * stored data. A <code>NUMBER</code>, for instance, can be integral
 * or floating point, be signed or not, and use from 1 to 8 bytes to
 * represent its values. This information is described by means of
 * another enumeration, {@link NumericIlk}. Composite numeric slawx
 * are homogenous when it comes to their numeric ilk, which is derived
 * from that of its components (for instance, a Slaw with ilk
 * <code>COMPLEX</code> and numeric ilk <code>INT32</code> will be
 * composed of two slawx, each of them with ilk <code>NUMBER</code>
 * and numeric ilk <code>INT32</code>).
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

    /** A void Slaw containing no value */
    NIL,
    /** A Slaw containing a boolean value */
    BOOL,
    /** A Slaw containing a UTF-8 encoded string */
    STRING,
    /**
     * A Slaw encapsulating a numeric value, which can be integral or
     * floating point, with a variety of precisions. The actual type
     * of the associated value is given by the Slaw's NumericIlk.
     */
    NUMBER,
    /**
     * A numeric Slaw encapsulating a complex number, as a pair of NUMBER.
     * The exact type of these numbers is given by this Slaw's NumericIlk.
     */
    COMPLEX,
    /**
     * A vector of NUMBER slawx. It can have dimension 2 to 4, and all
     * components have the same NumericIlk, which is also the vector's
     * numeric ilk.
     */
    NUMBER_VECTOR(2, 4),
    /**
     * A vector of COMPLEX slawx. It can have dimension 2 to 4, and all
     * components have the same NumericIlk, which is also the vector's
     * numeric ilk.
     */
    COMPLEX_VECTOR(2, 4),
    /**
     * All components of a multivector have ilk NUMBER, and the same
     * NumericIlk. The dimension <em>d</em> of a multivector can run
     * from 2 to 5, with <em>2**d</em> the corresponding number of
     * componets.
     */
    MULTI_VECTOR(2, 5),
    /**
     * An homogeneous array of slawx of ilk NUMBER. It can contain any
     * number of components with the same numeric ilk, which is also
     * the array's numeric ilk.
     */
    NUMBER_ARRAY,
    /**
     * An homogeneous array of slawx of ilk COMPLEX. It can contain any
     * number of components with the same numeric ilk, which is also
     * the array's numeric ilk.
     */
    COMPLEX_ARRAY,
    /**
     * An homogeneous array of slawx of ilk NUMBER_VECTOR. It can
     * contain any number of components with the same numeric ilk,
     * which is also the array's numeric ilk.
     */
    VECTOR_ARRAY(2, 4),
    /**
     * An homogeneous array of slawx of ilk COMPLEX_VECTOR. It can
     * contain any number of components with the same numeric ilk,
     * which is also the array's numeric ilk.
     */
    COMPLEX_VECTOR_ARRAY(2, 4),
    /**
     * An homogeneous array of slawx of ilk MULTI_VECTOR. It can
     * contain any number of components with the same numeric ilk,
     * which is also the array's numeric ilk.
     */
    MULTI_VECTOR_ARRAY(2, 5),
    /**
     * A composite Slaw consisting of two slawx. Often referred to as
     * its car and cdr, those components can be accessed using
     * Slaw#car() and Slaw#cdr().
     */
    CONS,
    /**
     * A composite Slaw containing a (possibly empty) list of slawx.
     * Each component is accessible via Slaw#nth(), or by means of
     * an iterator (see Slaw#iterator()).
     */
    LIST,
    /**
     * A composite Slaw representing an associative array, with both
     * keys and values arbitrary slawx. See Slaw#find() for how to
     * access them.
     */
    MAP,
    /**
     * A Slaw that is downcastable to Protein, which see.
     */
    PROTEIN;

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
     * Checks whether this ilk is either <code>NUMBER_VECTOR</code> or
     * <code>COMPLEX_VECTOR</code>.
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
     * Numeric containers have a dimension: this method you gives you
     * its minimum value (2) for them, or 0 for non-containers.
     */
    public int minDimension() {
        return minDim;
    }

    /**
     * Numeric containers have a dimension: this method you gives you
     * its minimum value (2) for them, or 0 for non-containers.
     */
    public int maxDimension() {
        return maxDim;
    }

    /**
     * Utility method checking that a given dimension is in the range
     * accepted by this ilk.
     */
    public boolean isValidDimension(int d) {
        return minDim <= d && maxDim >= d;
    }

    /**
     * The set of ilks for slawx that represent a single value. That
     * is, ilks for slawx that are not an aggregate of other slawx.
     * This set is composed of <code>NIL</code>, <code>BOOL</code>,
     * <code>STRING</code> and <code>NUMBER</code>.
     */
    public static Set<SlawIlk> atomicIlks() { return atoms.clone(); }

    /**
     * The set of ilks for slawx that represent a numeric value, or an
     * aggregate of them. This set is composed of <code>NUMBER</code>,
     * <code>COMPLEX</code>, <code>NUMBER_VECTOR</code>,
     * <code>COMPLEX_VECTOR</code>, <code>MULTI_VECTOR</code> and all
     * the array ilks (contained in the set returned by {@link
     * #arrayIlks}).
     *
     * <p> As explained in this class description, the real type of a
     * Slaw whose ilk belongs to this set is determined by both its
     * ilk and its numeric ilk.
     */
    public static Set<SlawIlk> numericIlks() { return numeric.clone(); }

    /**
     * The set of numeric ilks whose components are complex numbers.
     * This set consists of <code>COMPLEX</code>,
     * <code>COMPLEX_VECTOR</code>, <code>COMPLEX_ARRAY</code> and
     * <code>COMPLEX_VECTOR_ARRAY</code>.
     */
    public static Set<SlawIlk> complexIlks() { return complex.clone(); }

    /**
     * The set of ilks representing arrays of numeric values. This set
     * includes <code>NUMBER_ARRAY</code>, <code>COMPLEX_ARRAY</code>,
     * <code>VECTOR_ARRAY</code>, <code>COMPLEX_VECTOR_ARRAY</code>,
     * and <code>MULTI_VECTOR_ARRAY</code>.
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

    private SlawIlk() { this(0, 0); }

    private SlawIlk(int min, int max) {
        minDim = min;
        maxDim = max;
    }

    private final int minDim;
    private final int maxDim;
}
