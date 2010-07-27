// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import net.jcip.annotations.Immutable;

import static com.oblong.jelly.NumericIlk.*;
import static com.oblong.jelly.SlawIlk.*;

/**
 * Slaw instances constitute the components of Proteins and, as such,
 * represent a value serializable to and from pools and having one of
 * the types types enumerated by SlawIlk. The class interface includes
 * a family of predicates to check for the concrete type (or, as we'll
 * call it, ilk) of the Slaw instance at hand. Once you know its ilk,
 * you can access the actual value wrapped by a Slaw (in the form of a
 * Java native value) using one of the emit methods in this class.
 * Each of these methods is applicable, in general, only to a subset
 * of the possible Slaw ilks (e.g. emitBoolean() makes no sense when
 * applied to a Slaw with ilk MAP), and will throw an (unchecked)
 * UnsupportedOperationException if called on the wrong Slaw instance;
 * thus, the recommended pattern is either to guard their usage by a
 * call to the corresponding type predicate, as in
 *
 * <pre>
 *   if (s.isNumber()) x = s.emitLong();
 *   // ...
 *   if (!s2.isBoolean()) return x;
 *   // ...
 *   foo(s2.emitBoolean());
 * </pre>
 *
 * or wrap the call inside a try/catch clause. The method
 * documentation below details, for each of the potentially illegal
 * operations, the safe ilk subsets, as well as the type tests that
 * can be used as guards.
 *
 * Several of the available Slaw ilks represent composite types. You
 * can access the components of a Slaw instance in a traditional index
 * based fashion by means of nth and count. But Slaw also also
 * implements Iterable<Slaw>, so that you can iterate over the
 * sub-slawx using new-style for loops or the Iterator<Slaw>
 * interface. Since Slaw instances are immutable, the latter does not
 * support the remove operation.
 *
 * As a convenience, using the sub-slaw traversal APIs on atomic Slaw
 * instances is supported: with regard to those methods, an atomic
 * Slaw is considered as a container with one element, which is
 * itself. This convenience comes with the risk of introducing
 * infinite loops in your programs if you happen to recursively
 * iterate over sub-slawx: such iterations should always be guarded by
 * a call to isComposite().
 *
 * Since this is an abstract class, a host of factory methods is
 * provided to let you construct new Slaw instances out of native Java
 * values and/or other Slawx. In the case of non-atomic numeric Slaw
 * (complex numbers, vectors and multivectors) constructors taking
 * generic Slaw arguments, the latter must be of a numeric ilk;
 * otherwise, a non-checked exception will be thrown. So it pays to
 * make sure that arguments passed to those methods are of the correct
 * ilk. Note that all atomic numeric Slaw share a common ilk, NUMBER,
 * which is further refined (in terms of signedness and width) by an
 * associated NumericIlk: there's a well defined notion of coercion
 * between Slaw numbers, given by an ordering of their numeric ilk
 * from INT8 up to FLOAT64, and that coercion is applied by the
 * factory methods of composite numeric slawx.
 *
 * So, strictly speaking, the real type of a Slaw instance is
 * determined by its ilk <i>and</i> its numeric ilk. Composite numeric
 * Slawx inherit their numeric ilk from that of their components
 * (which will always be the same: the factory methods will take care
 * of that). Non-numeric Slawx have <code>null</code> as their
 * NumericIlk.
 *
 * The use of Slaw instead of native Java data types will be
 * conductive to a dynamic style in your programs, with the
 * flexibility (and drawbacks) offered by run-time checks and late
 * binding. Alternatively, you can use Slaw simply as a serialization
 * format to communicate and exchange data with pools, quickly
 * de-marshal the proteins you obtain, and stick to a more
 * conventional, statically typed style in the rest of your program.
 *
 * Created: Mon Apr 12 16:46:30 2010
 *
 * @author jao
 */
@Immutable
public abstract class Slaw implements Iterable<Slaw> {

    /**
     * The ilk (or kind) of this slaw. The return value of this method
     * is guaranteed to be non-null.
     */
    public abstract SlawIlk ilk();

    /**
     * For Slaw instances whose ilk is numeric (i.e., those for which
     * isNumeric returns <code>true</code>), this method will return
     * its numeric ilk. The real type of a numeric Slaw is determined
     * by both its ilk and its numeric ilk.
     *
     * For non-numeric Slawx, this method returns <code>null</code>.
     *
     * @see Slaw#isNumeric
     */
    public abstract NumericIlk numericIlk();

    /**
     * Convenience method that checks this Slaw's ilk against the
     * provided one. If <code>ilk</code> is <code>null</code>, this
     * method will return <code>false</code>.
     */
    public final boolean is(SlawIlk ilk) { return ilk == this.ilk(); }

    /**
     * Checks whether this Slaw's ilk is atomic, using
     * SlawIlk#isAtomic.
     */
    public final boolean isAtomic() { return ilk().isAtomic(); }

    /** Just the negation of isAtomic() */
    public final boolean isComposite() { return !isAtomic(); }

    public final boolean isNil() { return is(NIL); }
    public final boolean isBoolean() { return is(BOOL); }
    public final boolean isString() { return is(STRING); }

    /**
     * Checks whether this Slaw belongs to one of the numeric ilks.
     * Numeric ilks are defined by the predicate SlawIlk#isNumeric.
     *
     * Numeric Slawx can be either atomic (SlawIlk#NUMBER) or
     * composite (SlawIlk#COMPLEX, SlawIlk#NUMBER_VECTOR,
     * SlawIlk#COMPLEX_VECTOR, SlawIlk#MULTI_VECTOR,
     * SlawIlk#NUMBER_ARRAY, SlawIlk#COMPLEX_ARRAY,
     * SlawIlk#VECTOR_ARRAY, SlawIlk#COMPLEX_VECTOR_ARRAY and
     * SlawIlk#MULTI_VECTOR_ARRAY). For the latter, the numeric ilk
     * refers, transitively, to the numeric ilk of their components,
     * which will always be of the same ilk (all numbers, or all
     * vectors, and so on) and have the same numeric ilk
     * (NumericIlk#INT8, NumericIlk#UNT32, etc.).
     */
    public final boolean isNumeric() { return ilk().isNumeric(); }

    /**
     * Checks whether this Slaw's ilk is equal to NUMBER. Note that
     * isNumber() implies isNumeric(), but that not all numeric slawx
     * are numbers.
     */
    public final boolean isNumber() { return is(NUMBER); }

    /**
     * Checks whether this Slaw is a number (using isNumber) and that
     * its numeric ilk equals <code>ni</code>.
     */
    public final boolean isNumber(NumericIlk ni) {
        return isNumber() && numericIlk() == ni;
    }

    public final boolean isComplex() { return is(COMPLEX); }

    /**
     * Checks whether this Slaw's ilk corresponds to a vector,
     * regardless of its components' kind. Multivectors are also
     * included in this family. The components of a vector can be
     * either numbers or complex numbers, while multivectors can have
     * only numbers. In all cases, components are homogeneous: they
     * have the same ilk and the same numeric ilk.
     *
     * @see SlawIlk#isVector
     */
    public final boolean isVector() { return ilk().isVector(); }

    public final boolean isNumberVector() { return is(NUMBER_VECTOR); }
    public final boolean isComplexVector() { return is(COMPLEX_VECTOR); }
    public final boolean isMultivector() { return is(MULTI_VECTOR); }

    /**
     * Checks whether this Slaw's ilk corresponds to an array,
     * regardless of its components' kind. Components are numeric
     * (number, complex or vectorial) and homogeneous, both in ilk and
     * numeric ilk.
     *
     * @see SlawIlk#isArray
     */
    public final boolean isArray() { return ilk().isArray(); }

    public final boolean isNumberArray() { return is(NUMBER_ARRAY); }
    public final boolean isComplexArray() { return is(COMPLEX_ARRAY); }
    public final boolean isNumberVectorArray() { return is(VECTOR_ARRAY); }
    public final boolean isComplexVectorArray() {
        return is(COMPLEX_VECTOR_ARRAY);
    }
    public final boolean isMultivectorArray() {
        return is(MULTI_VECTOR_ARRAY);
    }

    public final boolean isCons() { return is(CONS); }
    public final boolean isList() { return is(LIST); }
    public final boolean isMap() { return is(MAP); }
    public final boolean isProtein() { return is(PROTEIN); }

    /**
     * When this Slaw has ilk SlawIlk#BOOL, emits the corresponding
     * boolean value it encapsulates. Otherwise, an
     * UnsupportedOperationException is thrown.
     */
    public abstract boolean emitBoolean();

    /**
     * When this Slaw has ilk SlawIlk#STRING, emits the corresponding
     * String value it encapsulates. Otherwise, an
     * UnsupportedOperationException is thrown.
     */
    public abstract String emitString();

    /**
     * When this Slaw has ilk SlawIlk#NUMBER, emits the corresponding
     * value it encapsulates, cast to a long. Otherwise, an
     * UnsupportedOperationException is thrown.
     *
     * If the numeric ilk of this Slaw is either NumericIlk#FLOAT32 or
     * NumericIlk#FLOAT64, a loss of precision may occurr, equivalent
     * to the one implied in a conversion from float or double to
     * long. Therefore, for those numeric ilks, it's safer to use
     * Slaw#emitDouble instead.
     *
     * Likewise, for slaw numbers of numeric ilk NumericIlk#UNT64, the
     * eight bytes representing its value will be interpreted as the
     * 2-complement representation of the returned long; thus, unless
     * you're sure that this Slaw's value is below Long#MAX_VALUE, it
     * is safer to use Slaw#emitBigInteger for UNT64 numbers.
     */
    public abstract long emitLong();

    /**
     * When this Slaw has ilk SlawIlk#NUMBER, emits the corresponding
     * value it encapsulates, cast to a double. Otherwise, an
     * UnsupportedOperationException is thrown.
     *
     * If the numeric ilk of this Slaw corresponds to an integral
     * numeric type, a loss of precision may occurr, equivalent to the
     * one implied in a conversion from an Java integer type to
     * double. Therefore, for those numeric ilks, it's safer to use
     * Slaw#emitLong or Slaw#emitBigInteger instead.
     */
    public abstract double emitDouble();

    /**
     * When this Slaw has ilk SlawIlk#NUMBER, emits the corresponding
     * value it encapsulates, cast to a BigInteger. Otherwise, an
     * UnsupportedOperationException is thrown.
     *
     * If the numeric ilk of this Slaw corresponds to an integral
     * type, this method is guaranteed to return the actual value,
     * without losing precision. But, if the numeric ilk of this Slaw
     * is either NumericIlk#FLOAT32 or NumericIlk#FLOAT64, a loss of
     * precision may occurr, equivalent to the one implied in a
     * conversion from float or double to long. Therefore, for those
     * numeric ilks, it's safer to use Slaw#emitDouble instead.
     */
    public abstract BigInteger emitBigInteger();

    /**
     * When this Slaw has ilk SlawIlk#CONS, SlawIlk#COMPLEX or
     * SlawIlk#LIST (and, in the latter case, corresponds to a
     * non-empty list), the first component of the Slaw is returned.
     * Otherwise, an UnsupportedOperationException is thrown.
     *
     * When supported, this operation returns the same value as nth(0).
     *
     * @see Slaw#cdr
     */
    public abstract Slaw car();

    /**
     * When this Slaw has ilk SlawIlk#CONS, SlawIlk#COMPLEX or
     * SlawIlk#LIST (and, in the latter case, corresponds to a
     * non-empty list), the second component of the Slaw is returned.
     * Otherwise, an UnsupportedOperationException is thrown.
     *
     * When supported, this operation returns the same value as nth(1)
     * for conses and complex numbers, and a list consisting of all
     * elements but the first when this Slaw is a list.
     *
     * @see Slaw#car
     */
    public abstract Slaw cdr();

    /**
     * When this Slaw is a vector or multivector, its dimension will
     * be returned. Numbers have dimension 1. For other ilks, this
     * method returns 0.
     */
    public abstract int dimension();

    /**
     * Number of sub-slaw contained in this Slaw instance. As a
     * degenerate case, this method returns 1 for atomic ilks. For
     * complex numbers and conses, it returns 2; for vectors, a number
     * between 2 and 4 (the same as the dimension), and one between 4
     * and 32 for multivectors (i.e., 2 to the dimension of the
     * multivector). For lists, this is the number of elements and,
     * for maps, the number of keys. Finally, if this Slaw has as ilk
     * SlawIlk#PROTEIN, this method will return 0.
     */
    public abstract int count();

    /**
     * Access to the nth sub-slaw of this Slaw instance, where
     * <code>n</code> is a zero-based index. This method is guaranteed
     * to succeed and return a non-null value provided <code>n</code>
     * is less than the value returned by count(). If that's not the
     * case, an (unchecked) IndexOutOfBoundsException is thrown.
     *
     * For complex numbers and conses, nth(0) and nth(1) are
     * equivalent to car() and cdr(), respectively. For vectors and
     * multivectors, it returns the corresponding component, while for
     * lists it acessess the nth element. If this Slaw is a map, the
     * returning Slaw is a cons composed of the nth key and value.
     *
     * Proteins have no sub-slaw.
     *
     * This method, together with Slaw#count, is used to implement all
     * the list-related methods in the Slaw interface. Therefore, its
     * semantics constitute the way Slawx of any kind are viewed as
     * lists of subslawx.
     */
    public abstract Slaw nth(int n);

    /**
     * Looks for <code>elem</code> among the sub-slawx of this Slaw
     * instance, using Slaw#equals as equality test. It returns the
     * index of <code>elem</code>, when found, or -1 otherwise. Thus,
     * it is always the case that the return value is less that
     * count(), and that, whenever this methods returns a non-negative
     * value, <code>e.equals(indexOf(e))</code> is true.
     */
    public final int indexOf(Slaw elem) {
        for (int i = 0, c = count(); i < c; i++)
            if (elem.equals(nth(i))) return i;
        return -1;
    }

    /**
     * Looks for <code>elem</code> among this Slaw's sub-slawx. This
     * method just checks whether Slaw#indexOf returns an index
     * greater than 0.
     */
    public final boolean contains(Slaw elem) { return indexOf(elem) >= 0; }

    /**
     * Creates a list with all of the sub-slawx of this Slaw. This
     * method uses Slaw#nth to construct the list, so a non-null value
     * will always be returned (albeit in some occasions it may be an
     * empty List), even if this Slaw is not of ilk SlawIlk#LIST.
     */
    public final List<Slaw> emitList() { return emitList(0, count()); }

    /**
     * Returns a slice of the list constructed by Slaw#emitList(). The
     * slice contains elements beginning at index <code>begin</code>
     * and ending at either <code>end-1</code> or
     * <code>count()-1</code> if <code>end>count()</code>. If
     * <code>end<=begin</code>, an empty list is returned. Negative
     * end or begin values are transformed to positive values by
     * substracting them from count().
     */
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

    /**
     * An iterator traversing this Slaw's subslawx sequentially, using
     * nth(). This method makes Slaw an implementation of
     * Iterable<Slaw>, and allows you to rewrite old-style loops of
     * the form:
     *
     * <pre>
     *   for (int i = 0; i < s.count(); ++i) f(s.nth(i));
     * </pre>
     *
     * as
     *
     * <pre>
     *   for (Slaw ss : s) f(ss);
     * </pre>
     *
     * And of course you can also use Iterator's interface on the the
     * returned value, which, however, does not support the
     * Iterator#remove operation (because Slaw instances are
     * immutable).
     *
     */
    public final Iterator<Slaw> iterator() {
        class SlawIterator implements Iterator<Slaw> {
            public SlawIterator(Slaw s) { slaw = s; }
            public boolean hasNext() { return n < slaw.count(); }
            public Slaw next() {
                if (n >= slaw.count()) throw new NoSuchElementException();
                return slaw.nth(n++);
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }
            private int n = 0;
            private final Slaw slaw;
        };
        return new SlawIterator(this);
    }

    /**
     * Converts a Slaw of ilk SlawIlk#MAP to a Java native map. If
     * this Slaw is a list and all its elements are conses, they're
     * taken as key/value pairs; otherwise, its elements are
     * interpreted as alternating keys and values (discarding the last
     * one if the number of elements is odd), and a corresponding map
     * is constructed. Pairs are taken as representing a key (the car)
     * and its value (the cdr). For all other Slaw ilks, an empty map
     * is returned.
     */
    public abstract Map<Slaw,Slaw> emitMap();

    /**
     * This method is equivalent to calling <code>get(key)</code> on the
     * map returned by Slaw#emitMap(), which see.
     */
    public abstract Slaw find(Slaw key);

    /**
     * Convenience method down-casting this Slaw to a Protein
     * instance. If this slaw's ilk is not SlawIlk#PROTEIN, an
     * UnsupportedOperationException is thrown.
     */
    public final Protein toProtein() {
        if (!isProtein())
            throw new UnsupportedOperationException(ilk() + " as protein");
        return (Protein)this;
    }

    /**
     * Equality predicate. This Slaw will only be equal to another
     * Slaw instance of the same ilk and numeric ilk that, moreover,
     * encapsulates the same underlying value, recursively. Note that
     * this notion of equality is stronger than the one that could be
     * induced by, say, just comparing emitted values, or the subslawx
     * of the two Slaw instances being tested. It is thus possible for
     * two different Slaw to have, for instance, the same sub-slawx
     * and yet be different (one could be a list of pairs and the
     * other one a map), or to return the same value in
     * Slaw#emitLong() and yet have different numeric ilks (and,
     * hence, classified as distinct Slawx by this predicate).
     */
    @Override public final boolean equals(Object o) {
        if (!(o instanceof Slaw)) return false;
        Slaw s = (Slaw)o;
        return s.ilk() == ilk() && s.numericIlk() == numericIlk()
            && slawEquals(s);
    }

    /**
     * String representation of this Slaw, strictly for debugging
     * purposes. No guarantees are made about the format of this
     * representation, except that it will be human-readable (FSVO
     * human). Please, don't rely on any of its other properties,
     * because all of them may change in future versions without
     * warning.
     */
    @Override public final String toString() {
        StringBuilder buff = new StringBuilder("Slaw<").append(ilk());
        if (isNumeric()) buff.append("/").append(numericIlk());
        buff.append(" = ").append(debugString()).append(">");
        return buff.toString();
    }

    /**
     * If this Slaw is numeric, returns a copy of it with all its
     * components coerced to the provided numeric ilk. For non-number
     * Slaw instances (i.e., those that answer false to the predicate
     * Slaw#isNumeric), this method throws an
     * UnsupportedOperationException.
     */
    public abstract Slaw withNumericIlk(NumericIlk ilk);

    // Auxiliarly method used by equals.
    public abstract boolean slawEquals(Slaw s);

    // Auxiliarly method used by toString.
    public abstract String debugString();

    /**
     * Factory method constructing a Slaw with ilk SlawIlk#CONS out of
     * its two components. Both arguments must be non-null; otherwise,
     * an IllegalArgumentException is thrown.
     */
    public static Slaw cons(Slaw car, Slaw cdr) {
        return factory.cons(car, cdr);
    }

    /**
     * Factory method constructing a Slaw with ilk SlawIlk#LIST out of
     * its components. Null arguments are discarded. Thus,
     * <code>Slaw.list(null)</code> returns an empty Slaw list, as
     * does <code>Slaw.list()</code>.
     */
    public static Slaw list(Slaw... s) { return factory.list(s); }

    /**
     * Factory method constructing a Slaw with ilk SlawIlk#LIST from a
     * native Java list. Null elements of <code>s</code> are
     * discarded. If its argument is null, it returns an empty Slaw
     * list.
     */
    public static Slaw list(List<Slaw> s) { return factory.list(s); }

    /**
     * Factory method constructing a Slaw with ilk SlawIlk#MAP from a
     * a explicit list of Slawx. The arguments are interpreted as
     * alternating keys and values. If any of the Slawx in a key/value
     * pair is null, the whole pair is skipped.
     */
    public static Slaw map(Slaw... kvs) { return factory.map(kvs); }

    /**
     * Factory method constructing a Slaw with ilk SlawIlk#LIST from a
     * native Java map. If any of the Slawx in a key/value pair is
     * null, the whole pair is skipped. When <code>m</code> is null,
     * an empty Slaw map is returned.
     */
    public static Slaw map(Map<Slaw,Slaw> m) { return factory.map(m); }

    /**
     * Factory method creating a Protein. Calling this method is
     * equivalent to calling <code>protein(descrips, ingests,
     * null)</code> (i.e., it constructs a protein without raw data).
     */
    public static Protein protein(Slaw descrips, Slaw ingests) {
        return factory.protein(descrips, ingests, null);
    }

    /**
     * Factory method constructing a Slaw with ilk SlawIlk#PROTEIN out
     * of its three components: descrips, ingests and raw data. Any
     * argument can be null, denoting, respectively, a protein without
     * descrips, ingests or raw data. There are no restrictions on the
     * ilk of descrips and ingests.
     */
    public static Protein protein(Slaw descrips, Slaw ingests, byte[] data) {
        return factory.protein(descrips, ingests, data);
    }

    /**
     * Factory method creating a Slaw with ilk SlawIlk#NIL. All nil
     * slawx are equal under the Slaw#equals predicate, but no
     * guarantees are made regarding the equality, under
     * <code>==</code>, of the instances returned by different calls
     * to this method.
     */
    public static Slaw nil() { return factory.nil(); }

    /**
     * Factory method creating a Slaw with ilk SlawIlk#BOOL. All true
     * slawx are equal under the Slaw#equals predicate, as are all
     * false ones, but no guarantees are made regarding the equality,
     * under <code>==</code>, of the instances returned by different
     * calls to this method.
     */
    public static Slaw bool(boolean v) { return factory.bool(v); }

    /**
     * Factory method creating a Slaw with ilk SlawIlk#STRING. When
     * serialized, Slaw strings are always encoded using UTF-8.
     */
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
