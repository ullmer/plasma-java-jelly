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
final class JavaSlawFactory implements SlawFactory {

    @Override public Slaw nil() {
        return SlawNil.INSTANCE;
    }

    @Override public Slaw bool(boolean v) {
        return SlawBool.valueOf(v); }

    @Override public Slaw string(String s) {
        return SlawString.valueOf(s);
    }

    @Override public Slaw number(NumericIlk ilk, long n) {
        return SlawNumber.valueOf(ilk, n);
    }

    @Override public Slaw number(NumericIlk ilk, double n) {
        return SlawNumber.valueOf(ilk, n);
    }

    @Override public Slaw number(BigInteger n) {
        return SlawNumber.valueOf(n);
    }

    @Override public Slaw complex(Slaw re, Slaw im) {
        return null; // SlawComplex.valueOf(re, im);
    }

    @Override public Slaw vector(Slaw x, Slaw y) {
        return null; // SlawVector.valueOf(x, y);
    }

    @Override public Slaw vector(Slaw x, Slaw y, Slaw z) {
        return null; // SlawVector.valueOf(x, y, z);
    }

    @Override public Slaw vector(Slaw x, Slaw y, Slaw z, Slaw w) {
        return null; // SlawVector.valueOf(x, y, z, w);
    }

    @Override public Slaw multivector(Slaw v00, Slaw v01, Slaw v10, Slaw v11)
    {
        return null;
        // return SlawMultiVector.valueOf(v00, v01, v10, v11);
    }

    @Override public Slaw multivector(Slaw v0, Slaw v1) {
        return null;
        // return SlawMultiVector.valueOf(v0, v1);
    }

    @Override public Slaw array(Slaw... ns) {
        return null;
        // return SlawArray.valueOf(ns);
    }

    @Override public Slaw cons(Slaw car, Slaw cdr) {
        return null; // SlawCons.valueOf(car, cdr);
    }

    @Override public Slaw list(Slaw... sx) {
        return null; // SlawList.valueOf(sx);
    }

    @Override public Slaw map(Map<Slaw,Slaw> m) {
        return null; // SlawMap.valueOf(m);
    }

    @Override public Slaw map(Slaw... kvs) {
        return null; // SlawMap.valueOf(Arrays.asList(kvs));
    }
}
