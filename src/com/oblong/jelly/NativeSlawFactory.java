// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;

/**
 *
 * Created: Fri Apr  2 01:39:55 2010
 *
 * @author jao
 */
final class NativeSlawFactory implements SlawFactory {

    @Override public Slaw nil() {
        return NativeSlawNil.INSTANCE;
    }

    @Override public Slaw bool(boolean v) {
        return NativeSlawBool.valueOf(v); }

    @Override public Slaw string(String s) {
        return NativeSlawString.valueOf(s);
    }

    @Override public Slaw number(NumericIlk ilk, long n) {
        return NativeSlawNumber.valueOf(ilk, n);
    }

    @Override public Slaw number(NumericIlk ilk, double n) {
        return NativeSlawNumber.valueOf(ilk, n);
    }

    @Override public Slaw number(BigInteger n) {
        return NativeSlawNumber.valueOf(n);
    }

    @Override public Slaw complex(Slaw re, Slaw im) {
        return NativeSlawComplex.valueOf(re, im);
    }

    @Override public Slaw vector(Slaw x, Slaw y) {
        return NativeSlawVector.valueOf(x, y);
    }

    @Override public Slaw vector(Slaw x, Slaw y, Slaw z) {
        return NativeSlawVector.valueOf(x, y, z);
    }

    @Override public Slaw vector(Slaw x, Slaw y, Slaw z, Slaw w) {
        return NativeSlawVector.valueOf(x, y, z, w);
    }

    @Override public Slaw multivector(Slaw v00, Slaw v01, Slaw v10, Slaw v11)
    {
        return null;
        // return NativeSlawMultiVector.valueOf(v00, v01, v10, v11);
    }

    @Override public Slaw multivector(Slaw v0, Slaw v1) {
        return null;
        // return NativeSlawMultiVector.valueOf(v0, v1);
    }

    @Override public Slaw array(Slaw... ns) {
        return null;
        // return NativeSlawArray.valueOf(ns);
    }

    @Override public Slaw cons(Slaw car, Slaw cdr) {
        return NativeSlawCons.valueOf(car, cdr);
    }

    @Override public Slaw list(Slaw... sx) {
        return NativeSlawList.valueOf(sx);
    }

    @Override public Slaw map(Map<Slaw,Slaw> m) {
        return NativeSlawMap.valueOf(m);
    }

    @Override public Slaw map(Slaw... kvs) {
        return NativeSlawMap.valueOf(Arrays.asList(kvs));
    }
}
