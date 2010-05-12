// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

final class JavaSlawFactory implements SlawFactory {

    @Override public Slaw nil() {
        return SlawNil.INSTANCE;
    }

    @Override public Slaw bool(boolean v) {
        return SlawBool.valueOf(v); }

    @Override public Slaw string(String s) {
        if (s == null) throw new IllegalArgumentException("Null string");
        return SlawString.valueOf(s);
    }

    @Override public Slaw number(NumericIlk ilk, long n) {
        if (ilk == null || ilk == NumericIlk.NAN)
            throw new IllegalArgumentException("Invalid numeric ilk");
        return SlawNumber.valueOf(ilk, n);
    }

    @Override public Slaw number(NumericIlk ilk, double n) {
        if (ilk == null || ilk == NumericIlk.NAN)
            throw new IllegalArgumentException("Invalid numeric ilk");
        return SlawNumber.valueOf(ilk, n);
    }

    @Override public Slaw number(BigInteger n) {
        if (n == null) throw new IllegalArgumentException("Null argument");
        return SlawNumber.valueOf(n);
    }

    @Override public Slaw complex(Slaw re, Slaw im) {
        if (re == null || im == null || !re.isNumber() || !im.isNumber())
            throw new IllegalArgumentException("Args must be number slawx");
        return SlawComplex.valueOf(re, im);
    }

    @Override public Slaw vector(Slaw x, Slaw y) {
        return makeVector(x, y);
    }

    @Override public Slaw vector(Slaw x, Slaw y, Slaw z) {
        return makeVector(x, y, z);
    }

    @Override public Slaw vector(Slaw x, Slaw y, Slaw z, Slaw w) {
        return makeVector(x, y, z, w);
    }

    @Override public Slaw multivector(Slaw v00, Slaw v01, Slaw v10, Slaw v11)
    {
        if (v00 == null || !v00.isNumber()
            || !SlawIlk.haveSameIlk(v00, v01, v10, v11))
            throw new IllegalArgumentException("Args must be number slawx");
        return null;
        // return SlawMultiVector.valueOf(v00, v01, v10, v11);
    }

    @Override public Slaw multivector(Slaw v0, Slaw v1) {
        if (v0 == null || v1 == null
            || !v0.isMultivector() || !v1.isMultivector()
            || v0.count() != v1.count())
            throw new IllegalArgumentException
                ("Args must be multivectors of the same dimension");
        return null;
        // return SlawMultiVector.valueOf(v0, v1);
    }

    @Override public Slaw array(SlawIlk ilk, NumericIlk ni) {
        if (ilk == null || ni == null || !ilk.isArray()
            || ni == NumericIlk.NAN)
            throw new IllegalArgumentException ("Invalid ilks");
        return EmptyArray.valueOf(ilk, ni);
    }

    @Override public Slaw array(Slaw... ns) {
        Slaw[] fn = filterNulls(ns);
        if (fn.length == 0)
            throw new IllegalArgumentException("All args were null");
        if (!fn[0].isNumeric() || fn[0].isArray() || !SlawIlk.haveSameIlk(fn))
            throw new IllegalArgumentException
                ("Args must have the same ilk and be numeric non-arrays");
        return SlawArray.valueOf(fn);
    }

    @Override public Slaw cons(Slaw car, Slaw cdr) {
        if (car == null || cdr == null) throw new IllegalArgumentException();
        return SlawCons.valueOf(car, cdr);
    }

    @Override public Slaw list(Slaw... sx) { return SlawList.valueOf(sx); }

    @Override public Slaw list(List<Slaw> sx) {
        return SlawList.valueOf(sx);
    }

    @Override public Slaw map(Map<Slaw,Slaw> m) {
        return SlawMap.valueOf(m);
    }

    @Override public Slaw map(Slaw... kvs) {
        return SlawMap.valueOf(Arrays.asList(kvs));
    }

    private static Slaw[] filterNulls(Slaw[] sx) {
        if (sx.length == 0) return sx;
        List<Slaw> fs = new ArrayList<Slaw>(sx.length);
        for (Slaw s : sx) if (s != null) fs.add(s);
        return (Slaw[])fs.toArray();
    }

    private static Slaw makeVector(Slaw... cmps) {
        if (cmps[0] == null
            || (!cmps[0].isNumber() && !cmps[0].isComplex())
            || !SlawIlk.haveSameIlk(cmps))
            throw new IllegalArgumentException ("Args must have same ilk");
        return SlawVector.valueOf(cmps);
    }
}
