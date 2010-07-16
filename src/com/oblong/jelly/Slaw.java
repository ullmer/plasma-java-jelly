// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.jcip.annotations.Immutable;

import static com.oblong.jelly.NumericIlk.*;
import static com.oblong.jelly.SlawIlk.*;

/**
 * Created: Mon Apr 12 16:46:30 2010
 *
 * @author jao
 */
@Immutable
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
    public final boolean isNumber(NumericIlk ni) {
        return isNumber() && numericIlk() == ni;
    }
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

    public abstract boolean emitBoolean();
    public abstract String emitString();
    public abstract long emitLong();
    public abstract double emitDouble();
    public abstract BigInteger emitBigInteger();

    public abstract Slaw car();
    public abstract Slaw cdr();

    public abstract int dimension();
    public abstract int count();
    public abstract Slaw nth(int n);

    public final int indexOf(Slaw elem) {
        for (int i = 0, c = count(); i < c; i++)
            if (elem.equals(nth(i))) return i;
        return -1;
    }

    public final boolean contains(Slaw elem) { return indexOf(elem) >= 0; }

    public final List<Slaw> emitList() { return emitList(0, count()); }

    public final List<Slaw> emitList(int begin, int end) {
        final int c = count();
        if (begin < 0) begin += c;
        if (end < 0) end += c;
        final List<Slaw> ls = new ArrayList<Slaw>();
        if (begin >= 0 && end > begin) {
            final int t = Math.min(c, end);
            for (int i = begin; i < t; i++) ls.add(nth(i));
        }
        return ls;
    }

    public abstract Map<Slaw,Slaw> emitMap();
    public abstract Slaw find(Slaw key);

    public final Protein toProtein() {
        if (!isProtein())
            throw new UnsupportedOperationException(ilk() + " as protein");
        return (Protein)this;
    }

    @Override public final boolean equals(Object o) {
        if (!(o instanceof Slaw)) return false;
        Slaw s = (Slaw)o;
        return s.ilk() == ilk() && s.numericIlk() == numericIlk()
            && slawEquals(s);
    }

    @Override public final String toString() {
        StringBuilder buff = new StringBuilder("Slaw<").append(ilk());
        if (isNumeric()) buff.append("/").append(numericIlk());
        buff.append(" = ").append(debugString()).append(">");
        return buff.toString();
    }

    public abstract Slaw withNumericIlk(NumericIlk ilk);

    public abstract boolean slawEquals(Slaw s);
    public abstract String debugString();

    public static Slaw cons(Slaw car, Slaw cdr) {
        return factory.cons(car, cdr);
    }

    public static Slaw list(Slaw... s) { return factory.list(s); }
    public static Slaw list(List<Slaw> s) { return factory.list(s); }
    public static Slaw map(Slaw... kvs) { return factory.map(kvs); }
    public static Slaw map(List<Slaw> s) { return factory.map(s); }
    public static Slaw map(Map<Slaw,Slaw> m) { return factory.map(m); }

    public static Protein protein(Slaw descrips, Slaw ingests) {
        return factory.protein(descrips, ingests, null);
    }
    public static Protein protein(Slaw descrips, Slaw ingests, byte[] data) {
        return factory.protein(descrips, ingests, data);
    }

    public static Slaw nil() { return factory.nil(); }
    public static Slaw bool(boolean v) { return factory.bool(v); }
    public static Slaw string(String s) { return factory.string(s); }

    public static Slaw int8(int n) { return number(INT8, n); }
    public static Slaw int16(int n) { return number(INT16, n); }
    public static Slaw int32(int n) { return number(INT32, n); }
    public static Slaw int64(long n) { return number(INT64, n); }
    public static Slaw unt8(int n) { return number(UNT8, n); }
    public static Slaw unt16(int n) { return number(UNT16, n); }
    public static Slaw unt32(long n) { return number(UNT32, n); }
    public static Slaw unt64(long n) { return number(UNT64, n); }
    public static Slaw unt64(BigInteger n) { return factory.number(n);}
    public static Slaw float32(float n) { return number(FLOAT32, n); }
    public static Slaw float64(double n) { return number(FLOAT64, n);}
    public static Slaw number(NumericIlk ilk, long value) {
        return factory.number(ilk, value);
    }
    public static Slaw number(NumericIlk ilk, double value) {
        return factory.number(ilk, value);
    }

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

    public static Slaw multivector(Slaw... cs) {
        return factory.multivector(cs);
    }

    public static Slaw array(Slaw[] sx) { return factory.array(sx); }

    public static Slaw array(Slaw n, Slaw... ns) {
        return factory.array(n, ns);
    }

    public static Slaw array(SlawIlk i, NumericIlk n, int d) {
        return factory.array(i, n, d);
    }

    public static Slaw array(List<Slaw> sx) {
        Slaw[] ss = new Slaw[sx.size()];
        for (int i = 0; i < ss.length; ++i) ss[i] = sx.get(i);
        return array(ss);
    }

    private static final com.oblong.jelly.slaw.SlawFactory factory =
        new com.oblong.jelly.slaw.JavaSlawFactory();

}
