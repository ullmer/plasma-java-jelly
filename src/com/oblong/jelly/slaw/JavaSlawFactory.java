// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawFactory;
import com.oblong.jelly.SlawIlk;

import net.jcip.annotations.Immutable;

@Immutable
public final class JavaSlawFactory implements SlawFactory {

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
        if (ilk == null)
            throw new IllegalArgumentException("Invalid numeric ilk");
        return SlawNumber.valueOf(ilk, n);
    }

    @Override public Slaw number(NumericIlk ilk, double n) {
        if (ilk == null)
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

    @Override public Slaw vector(Slaw... cs) {
        if (cs.length < 2 || cs.length > 4)
            throw new IllegalArgumentException("Invalid component number");
        return makeVector(cs);
    }

    @Override public Slaw multivector(Slaw...cs) {
        if (cs.length != 4 && cs.length != 8 && cs.length != 16
             && cs.length != 32 && cs.length != 64)
            throw new IllegalArgumentException("Invalid component number");
        if (cs[0] == null || !cs[0].isNumber() || !SlawIlk.haveSameIlk(cs))
            throw new IllegalArgumentException("Args must be number slawx");
        return SlawMultivector.valueOf(cs);
    }

    @Override public Slaw array(SlawIlk ilk, NumericIlk ni, int d) {
        if (ilk == null || ni == null)
            throw new IllegalArgumentException("Null arg");
        if (!ilk.isArray())
            throw new IllegalArgumentException("Expected array ilk");
        if (d <= 0 || d > 5
            || ((ilk == SlawIlk.ARRAY || ilk == SlawIlk.COMPLEX_ARRAY) && d>1)
            || (ilk == SlawIlk.VECTOR_ARRAY && (d < 2 || d > 4))
            || (ilk == SlawIlk.MULTI_VECTOR_ARRAY && (d < 2 || d > 5)))
            throw new IllegalArgumentException ("Invalid dimension");
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

    @Override public Slaw array(Slaw[] sx) {
        if (sx == null || sx.length == 0)
            throw new IllegalArgumentException("Arg cannot be empty");
        if (!SlawIlk.haveSameIlk(sx))
            throw new IllegalArgumentException(
                "All args must have the same ilk");
        List<Slaw> cmps = new ArrayList<Slaw>(sx.length);
        for (Slaw s : sx) addArrayComponent(s, cmps);
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
