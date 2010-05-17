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
        return SlawMultivector.valueOf(v00, v01, v10, v11);
    }

    @Override public Slaw multivector(Slaw v0, Slaw v1) {
        if (v0 == null || v1 == null
            || !v0.isMultivector() || !v1.isMultivector()
            || v0.count() != v1.count())
            throw new IllegalArgumentException
                ("Args must be multivectors of the same dimension");
        return SlawMultivector.valueOf(v0, v1);
    }

    @Override public Slaw array(SlawIlk ilk, NumericIlk ni, int d) {
        if (ilk == null || ni == null || !ilk.isArray() || d <= 0 || d > 5
            || ni == NumericIlk.NAN
            || ((ilk == SlawIlk.NUMBER || ilk == SlawIlk.COMPLEX) && d > 1)
            || (ilk.isVector() && (d < 2 || d > 4))
            || (ilk == SlawIlk.MULTI_VECTOR && (d < 2 || d > 5)))
            throw new IllegalArgumentException ("Invalid ilks");
        return EmptyArray.valueOf(ilk, ni, d);
    }

    @Override public Slaw array(Slaw n, Slaw... ns) {
        if (!SlawIlk.haveSameIlk(ns)
            || (n != null && ns.length > 0 && n.ilk() != ns[0].ilk()))
            throw new IllegalArgumentException(
                "All args must have the same ilk");
        List<Slaw> cmps = new ArrayList<Slaw>(1 + ns.length);
        addArrayComponent(n, cmps);
        for (Slaw s : ns) addArrayComponent(s, cmps);
        if (cmps.size() == 0)
            throw new IllegalArgumentException("All args were null");
        return SlawArray.valueOf(cmps);
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

    @Override public Slaw map(List<Slaw> l) {
        return SlawMap.valueOf(l);
    }

    @Override public Protein protein(Slaw descrips, Slaw ingests,
                                     byte[] data) {
        return SlawProtein.valueOf(descrips, ingests, data);
    }

    private static Slaw makeVector(Slaw... cmps) {
        if (cmps[0] == null
            || (!cmps[0].isNumber() && !cmps[0].isComplex())
            || !SlawIlk.haveSameIlk(cmps))
            throw new IllegalArgumentException ("Args must have same ilk");
        return SlawVector.valueOf(cmps);
    }

    private void addArrayComponent(Slaw s, List<Slaw> l) {
        if (s != null) {
            if (!s.isNumeric() || s.isArray())
                throw new IllegalArgumentException(s.toString());
            l.add(s);
        }
    }
}
