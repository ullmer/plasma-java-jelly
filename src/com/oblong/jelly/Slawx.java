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

    public static SlawNumber int8(byte n) {
        return NativeSlawNumber.valueOf(INT8, n);
    }
    public static SlawNumber int16(short n) {
        return NativeSlawNumber.valueOf(INT16, n);
    }
    public static SlawNumber int32(int n) {
        return NativeSlawNumber.valueOf(INT32, n);
    }
    public static SlawNumber int64(long n) {
        return NativeSlawNumber.valueOf(INT64, n);
    }
    public static SlawNumber unt8(short n) {
        return NativeSlawNumber.valueOf(UNT8, n);
    }
    public static SlawNumber unt16(int n) {
        return NativeSlawNumber.valueOf(UNT16, n);
    }
    public static SlawNumber unt32(long n) {
        return NativeSlawNumber.valueOf(UNT32, n);
    }
    public static SlawNumber unt64(BigInteger n) {
        return NativeSlawNumber.valueOf(n);
    }
    public static SlawNumber float32(float n) {
        return NativeSlawNumber.valueOf(FLOAT32, n);
    }
    public static SlawNumber float64(double n) {
        return NativeSlawNumber.valueOf(FLOAT64, n);
    }

    public static SlawComplex complex(SlawNumber re, SlawNumber im) {
        return NativeSlawComplex.valueOf(re, im);
    }

    public static SlawNumberVector vector(SlawNumber x, SlawNumber y) {
        return NativeNumberSlawVector.valueOf(x, y);
    }
    public static SlawNumberVector vector(SlawNumber x, SlawNumber y,
                                          SlawNumber z) {
        return NativeNumberSlawVector.valueOf(x, y, z);
    }
    public static SlawNumberVector vector(SlawNumber x, SlawNumber y,
                                          SlawNumber z, SlawNumber w) {
        return NativeNumberSlawVector.valueOf(x, y, z, w);
    }

    public static SlawComplexVector vector(SlawComplex x, SlawComplex y) {
        return NativeComplexSlawVector.valueOf(x, y);
    }
    public static SlawComplexVector vector(SlawComplex x, SlawComplex y,
                                           SlawComplex z) {
        return NativeComplexSlawVector.valueOf(x, y, z);
    }
    public static SlawComplexVector vector(SlawComplex x, SlawComplex y,
                                           SlawComplex z, SlawComplex w) {
        return NativeComplexSlawVector.valueOf(x, y, z, w);
    }

    // m = multivector(v00, v01, v10, v11);
    // m.get(UP, UP).equals(v00);
    // m.get(UP, DOWN).equals(v01);
    // m.get(DOWN, UP).equals(v10);
    // m.get(DOWN, DOWN).equals(v11);
    public static SlawMultiVector multivector(SlawNumber v00, SlawNumber v01,
                                              SlawNumber v10, SlawNumber v11)
    {
        return null; // NativeMultiVector.valueOf(v00, v01, v10, v11);
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
        return null; // NativeMultiVector.valueOf(v0, v1);
    }

    public static SlawNumberArray array(SlawNumber... ns) {
        return null;
        // return array(dominantIlk(ns), ns);
    }
    public static SlawComplexArray array(SlawComplex... ns) {
        return null;
    }
    public static SlawNumberVectorArray array(SlawNumberVector... ns) {
        return null;
    }
    public static SlawComplexVectorArray array(SlawComplexVector... ns) {
        return null;
    }

    public static SlawCons cons(Slaw car, Slaw cdr) { return null; }

    public static SlawList list(Slaw... s) { return null; }

    public static SlawMap map(Map<Slaw,Slaw> m) { return null; }

    private Slawx () {}
}
