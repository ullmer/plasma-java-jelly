// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.oblong.util.Pair;

import static com.oblong.jelly.NumericIlk.*;
import static com.oblong.jelly.SlawIlk.*;

/**
 * Created: Mon Apr 12 16:46:30 2010
 *
 * @author jao
 */
public abstract class Slaw {

    public abstract SlawIlk ilk();
    public abstract NumericIlk numericIlk();
    public final boolean is(SlawIlk ilk) { return ilk == this.ilk(); }

    public abstract boolean asBoolean();
    public abstract String asString();

    public abstract long asLong();
    public abstract double asDouble();
    public abstract BigInteger asBigInteger();

    public abstract int dimension();
    public abstract int count();

    public abstract Slaw head();
    public abstract Slaw tail();
    public abstract List<Slaw> asList();
    public abstract Map<Slaw,Slaw> asMap();

    public SlawNil toNil() { return null; }
    public SlawNumber toNumber() { return null; }
    public SlawComplex toComplex() { return null; }
    public SlawString toStringSlaw() { return null; }
    public NumberSlawVector toNumberVector() { return null; }
    public ComplexSlawVector toComplexVector() { return null; }
    // public SlawMultivector toMultivector() { return null; }
    // public SlawArray toArray() { return null; }
    public SlawCons toCons() { return null; }
    public SlawList toList() { return null; }
    public SlawMap toMap() { return null; }

    public final boolean equals(Object o) {
        if (!(o instanceof Slaw)) return false;
        Slaw s = (Slaw)o;
        return s.ilk() == ilk() && s.numericIlk() == numericIlk() &&
            equals(s);
    }

    public static Slaw cons(Slaw car, Slaw cdr) {
        return factory.cons(car, cdr);
    }
    public static Slaw list(Slaw... s) { return factory.list(s); }
    public static Slaw map(Map<Slaw,Slaw> m) { return factory.map(m); }

    public static Slaw nil() { return factory.nil(); }
    public static Slaw bool(boolean v) { return factory.bool(v); }
    public static Slaw string(String s) { return factory.string(s); }

    public static Slaw int8(int n) { return factory.number(INT8, n); }
    public static Slaw int16(int n) { return factory.number(INT16, n); }
    public static Slaw int32(int n) { return factory.number(INT32, n); }
    public static Slaw int64(long n) { return factory.number(INT64, n); }
    public static Slaw unt8(int n) { return factory.number(UNT8, n); }
    public static Slaw unt16(int n) { return factory.number(UNT16, n); }
    public static Slaw unt32(long n) { return factory.number(UNT32, n); }
    public static Slaw unt64(long n) { return factory.number(UNT64, n); }
    public static Slaw unt64(BigInteger n) { return factory.number(n);}
    public static Slaw float32(float n) { return factory.number(FLOAT32, n); }
    public static Slaw float64(double n) { return factory.number(FLOAT64, n);}

    public static Slaw complex(Slaw re, Slaw im) {
        return factory.complex(re, im);
    }

    public static Slaw vector(Slaw x, Slaw y) {
        return factory.vector(x, y);
    }
    public static Slaw vector(Slaw x, Slaw y, Slaw z) {
        return factory.vector(x, y, z);
    }
    public static Slaw vector(Slaw x, Slaw y, Slaw z, Slaw w) {
        return factory.vector(x, y, z, w);
    }

    public static Slaw multivector(Slaw v00, Slaw v01, Slaw v10, Slaw v11) {
        return factory.multivector(v00, v01, v10, v11);
    }
    public static Slaw multivector(Slaw v0, Slaw v1) {
        return factory.multivector(v0, v1);
    }

    public static Slaw array(Slaw... ns) {
        return factory.array(ns);
    }

    // we don't really want external clients to extend this class
    Slaw() {}

    abstract boolean equals(Slaw s);
    abstract Slaw withNumericIlk(NumericIlk ilk);

    static void setFactory (SlawFactory f) { factory = f; }

    private static SlawFactory factory = new NativeSlawFactory();
}
