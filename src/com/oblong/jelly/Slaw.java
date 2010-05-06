// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;


import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.oblong.util.Pair;

import static com.oblong.jelly.SlawIlk.*;
import static com.oblong.jelly.NumericIlk.*;

/**
 * Created: Mon Apr 12 16:46:30 2010
 *
 * @author jao
 */
public abstract class Slaw {

    public abstract SlawIlk ilk();
    public abstract NumericIlk numericIlk();

    public final boolean is(SlawIlk ilk) { return ilk == this.ilk(); }

    public final boolean isAtomic() { return ilk().isAtomic(); }
    public final boolean isComposite() { return !isAtomic(); }
    public final boolean isNil() { return is(NIL); }
    public final boolean isBoolean() { return is(BOOL); }
    public final boolean isString() { return is(STRING); }
    public final boolean isNumeric() { return ilk().isNumeric(); }
    public final boolean isNumber() { return is(NUMBER); }
    public final boolean isComplex() { return is(COMPLEX); }
    public final boolean isVector() { return ilk().isVector(); }
    public final boolean isNumberVector() { return is(VECTOR); }
    public final boolean isComplexVector() { return is(COMPLEX_VECTOR); }
    public final boolean isMultivector() { return is(MULTI_VECTOR); }
    public final boolean isArray() { return ilk().isArray(); }
    public final boolean isNumberArray() { return is(ARRAY); }
    public final boolean isComplexArray() { return is(COMPLEX_ARRAY); }
    public final boolean isNumberVectorArray() { return is(VECTOR_ARRAY); }
    public final boolean isComplexVectorArray() { return is(COMPLEX_ARRAY); }
    public final boolean isMultivectorArray() {
        return is(MULTI_VECTOR_ARRAY);
    }
    public final boolean isCons() { return is(CONS); }
    public final boolean isList() { return is(LIST); }
    public final boolean isMap() { return is(MAP); }
    public final boolean isProtein() { return is(PROTEIN); }

    public boolean emitBoolean() {
        throw new UnsupportedOperationException(ilk() + " as boolean");
    }

    public String emitString() {
        throw new UnsupportedOperationException(ilk() + " as string");
    }

    public long emitLong() {
        throw new UnsupportedOperationException(ilk() + " as long");
    }

    public double emitDouble() {
        throw new UnsupportedOperationException(ilk() + " as double");
    }

    public BigInteger emitBigInteger() {
        throw new UnsupportedOperationException(ilk() + " as big integer");
    }

    public final Pair<Slaw,Slaw> emitPair() {
        return Pair.create(first(), second());
    }

    public Slaw first() {
        throw new UnsupportedOperationException(ilk() + " as pair");
    }

    public Slaw second() {
        throw new UnsupportedOperationException(ilk() + " as pair");
    }

    public abstract int count();

    public List<Slaw> emitList() {
        throw new UnsupportedOperationException(ilk() + " as list");
    }

    public Slaw get(int n) { return emitList().get(n); }

    public int indexOf(Slaw elem) { return emitList().indexOf(elem); }

    public final boolean contains(Slaw elem) { return indexOf(elem) >= 0; }

    public Slaw slice(int begin, int end)  {
        return list(emitList().subList(begin, end));
    }

    public Map<Slaw,Slaw> emitMap() {
        throw new UnsupportedOperationException(ilk() + " as map");
    }

    public Slaw get(Slaw key) { return emitMap().get(key); }

    public final Slaw descrips() {
        if (!isProtein()) throw new UnsupportedOperationException();
        return first();
    }

    public final Slaw ingests() {
        if (!isProtein()) throw new UnsupportedOperationException();
        return second();
    }

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
    public static Slaw list(List<Slaw> s) {
        return factory.list((Slaw[])s.toArray());
    }
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

    Slaw withNumericIlk(NumericIlk ilk) {
        throw new UnsupportedOperationException(ilk() + " not numeric");
    }

    static void setFactory (SlawFactory f) { factory = f; }

    private static SlawFactory factory = new JavaSlawFactory();
}
