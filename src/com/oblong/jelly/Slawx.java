// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.oblong.jelly.NumericSlaw.*;
import static com.oblong.jelly.NumericSlaw.Ilk.*;

/**
 * Slawx is a factory of Slaw instances, providing only static factory
 * methods. You may consider using a static import form to make their
 * usage less verbose.
 *
 *
 * Created: Fri Apr  2 01:39:55 2010
 *
 * @author jao
 */
public final class Slawx {

    public static Slaw nil() {
        return NativeSlawNil.INSTANCE;
    }
    public static SlawBool bool(boolean v) {
        return NativeSlawBool.valueOf(v);
    }
    public static SlawString string(String s) {
        return NativeSlawString.valueOf(s);
    }

    public static SlawNumber int8(byte n) { return number(INT8, n); }
    public static SlawNumber int16(short n) { return number(INT16, n); }
    public static SlawNumber int32(int n) { return number(INT32, n); }
    public static SlawNumber int64(long n) { return number(INT64, n); }
    public static SlawNumber unt8(short n) { return number(UNT8, n); }
    public static SlawNumber unt16(int n) { return number(UNT16, n); }
    public static SlawNumber unt32(long n) { return number(UNT32, n); }
    public static SlawNumber unt64(BigInteger n) { return number(n); }
    public static SlawNumber float32(float n) { return number(FLOAT32, n); }
    public static SlawNumber float64(double n) { return number(FLOAT64, n); }

    public static SlawComplex complex(SlawNumber re, SlawNumber im) {
        return complex(dominantIlk(re.ilk(), im.ilk()), re, im);
    }

    public static <E extends NumericSlaw> SlawArray<E> array(E... ns) {
        return array(dominantIlk(ns), ns);
    }

    public static SlawVector<SlawNumber> vector(SlawNumber x, SlawNumber y) {
        List<SlawNumber> els = Arrays.asList(x, y);
        return vector(dominantIlk(x, y), els);
    }
    public static SlawVector<SlawNumber> vector(SlawNumber x, SlawNumber y,
                                                SlawNumber z) {
        List<SlawNumber> els = Arrays.asList(x, y, z);
        return vector(dominantIlk(x, y, z), els);
    }
    public static SlawVector<SlawNumber> vector(SlawNumber x, SlawNumber y,
                                                SlawNumber z, SlawNumber w) {
        List<SlawNumber> els = Arrays.asList(x, y, z, w);
        return vector(dominantIlk(x, y, z, w), els);
    }

    public static SlawVector<SlawComplex> vector(SlawComplex x,
                                                 SlawComplex y) {
        List<SlawComplex> els = Arrays.asList(x, y);
        return cvector(dominantIlk(x, y), els);
    }
    public static SlawVector<SlawComplex> vector(SlawComplex x, SlawComplex y,
                                                 SlawComplex z) {
        List<SlawComplex> els = Arrays.asList(x, y, z);
        return cvector(dominantIlk(x, y, z), els);
    }
    public static SlawVector<SlawComplex> vector(SlawComplex x, SlawComplex y,
                                                 SlawComplex z, SlawComplex w)
    {
        List<SlawComplex> els = Arrays.asList(x, y, z, w);
        return cvector(dominantIlk(x, y, z, w), els);
    }

    // m = multivector(v00, v01, v10, v11);
    // m.get(UP, UP).equals(v00);
    // m.get(UP, DOWN).equals(v01);
    // m.get(DOWN, UP).equals(v10);
    // m.get(DOWN, DOWN).equals(v11);
    public static SlawMultiVector multivector(SlawNumber v00, SlawNumber v01,
                                              SlawNumber v10, SlawNumber v11)
    {
        return multivector(dominantIlk(v00, v01, v10, v11),
                           v00, v01, v10, v11);
    }

    // v01 = multivector(v0, v1);
    // v01.curry(UP).equals(v0);
    // v01.curry(DOWN).equals(v1);
    // v01.get(..., UP).equals(v0.get(...));
    // v01.get(..., DOWN).equals(v1.get(...));
    public static SlawMultiVector multivector(SlawMultiVector v0,
                                              SlawMultiVector v1) {
        if (v0.dimension() != v1.dimension()) {
            SlawError.maybeThrow("Multivector dimensions don't match (" +
                                 v0.dimension() + " != " + v1.dimension());
            return null;
        }
        return multivector(dominantIlk(v0, v1), v0, v1);
    }

    public static SlawCons cons(Slaw car, Slaw cdr) { return null; }

    public static SlawList list(Slaw... s) { return null; }
    public static SlawList list(List<Slaw> l) {
        return list((Slaw[])l.toArray());
    }

    public static SlawMap map(Map<Slaw,Slaw> m) { return null; }


    static SlawNumber number(Ilk ilk, long n) {
        return NativeSlawNumber.valueOf(ilk, n);
    }
    static SlawNumber number(Ilk ilk, double n) {
        return NativeSlawNumber.valueOf(ilk, n);
    }
    static SlawNumber number(BigInteger n) {
        return NativeSlawNumber.valueOf(n);
    }

    static SlawComplex complex(Ilk ilk, SlawNumber re, SlawNumber im) {
        SlawNumber r = re.withIlk(ilk);
        SlawNumber i = im.withIlk(ilk);
        return NativeSlawComplex.valueOf(r, i);
    }

    static <E extends NumericSlaw> SlawArray<E> array(Ilk ilk, E... ns) {
        return null;
    }

    static SlawVector<SlawNumber> vector(Ilk ilk, List<SlawNumber> ns) {
        return NativeNumberSlawVector.valueOf(ilk, ns);
    }

    static SlawVector<SlawComplex> cvector(Ilk ilk, List<SlawComplex> ns) {
        return NativeComplexSlawVector.valueOf(ilk, ns);
    }

    static SlawMultiVector multivector(Ilk i, SlawNumber v00, SlawNumber v01,
                                       SlawNumber v10, SlawNumber v11) {
        return null;
    }
    static SlawMultiVector multivector(Ilk i, SlawMultiVector v0,
                                       SlawMultiVector v1) {
        return null;
    }

    private Slawx () {}
}
