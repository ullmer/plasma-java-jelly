// Copyright (c) 2010 Oblong Industries
// Created: Mon Apr 12 16:46:30 2010

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
 * Slaw instances constitute the components of Proteins and wrap
 * strongly typed data values.
 *
 * <p> As we'll see, the type of the value hold by a Slaw is fully
 * determined by its ilk (as enumerated by {@link SlawIlk} and, when
 * applicable, its numeric ilk (enumerated by {@link NumericIlk}. The
 * class interface includes a family of predicates to check for the
 * concrete type (or, as we'll call it, ilk) of the Slaw instance at
 * hand. Once you know its ilk, you can access the actual value
 * wrapped by a Slaw (in the form of a Java native value) using one of
 * the emit methods in this class. Each of these methods is
 * applicable, in general, only to a subset of the possible Slaw ilks
 * (e.g. <code>emitBoolean()</code> makes no sense when applied to a
 * Slaw with ilk <code>MAP</code>), and will throw an (unchecked)
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
 * <p> Several of the available Slaw ilks represent composite types.
 * You can access the components of a Slaw instance in a traditional
 * index based fashion by means of nth and count. But Slaw also also
 * implements <code>Iterable<Slaw></code>, so that you can iterate
 * over the sub-slawx using new-style for loops or the
 * <code>Iterator<Slaw></code> interface. Since Slaw instances are
 * immutable, the latter does not support the remove operation.
 *
 * <p> As a convenience, using the sub-slaw traversal APIs on atomic
 * Slaw instances is supported: with regard to those methods, an
 * atomic Slaw is considered as a container with one element, which is
 * itself. This convenience comes with the risk of introducing
 * infinite loops in your programs if you happen to recursively
 * iterate over sub-slawx: such iterations should always be guarded by
 * a call to {@link #isComposite()}.
 *
 * <p> Since this is an abstract class, a host of factory methods is
 * provided to let you construct new Slaw instances out of native Java
 * values and/or other slawx. In the case of non-atomic numeric Slaw
 * (complex numbers, vectors and multivectors) constructors taking
 * generic Slaw arguments, the latter must be of a numeric ilk;
 * otherwise, a non-checked exception will be thrown. So it pays to
 * make sure that arguments passed to those methods are of the correct
 * ilk. Note that all atomic numeric Slaw share a common ilk,
 * <code>NUMBER</code>, which is further refined (in terms of
 * signedness and width) by an associated NumericIlk: there's a well
 * defined notion of coercion between Slaw numbers, given by an
 * ordering of their numeric ilk from <code>INT8</code> up to
 * <code>FLOAT64</code>, and that coercion is applied by the factory
 * methods of composite numeric slawx.
 *
 * <p> So, strictly speaking, the real type of a Slaw instance is
 * determined by its ilk <i>and</i> its numeric ilk. Composite numeric
 * slawx inherit their numeric ilk from that of their components
 * (which will always be the same: the factory methods will take care
 * of that). Non-numeric slawx have <code>null</code> as their
 * NumericIlk.
 *
 * <p> The use of Slaw instead of native Java data
 * types will be conductive to a dynamic style in your programs, with
 * the flexibility (and drawbacks) offered by run-time checks and late
 * binding. Alternatively, you can use Slaw simply as a serialization
 * format to communicate and exchange data with pools, quickly
 * de-marshal the proteins you obtain, and stick to a more
 * conventional, statically typed style in the rest of your program.
 *
 * @see SlawIlk
 * @see NumericIlk
 * @see Protein
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
     * {@link SlawIlk#isNumeric} returns <code>true</code>), this
     * method will return its numeric ilk. The real type of a numeric
     * Slaw is determined by both its ilk and its numeric ilk. <p> For
     * non-numeric slawx, this method returns <code>null</code>.
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
     * Checks whether this Slaw's ilk is atomic, using {@link
     * SlawIlk#isAtomic}.
     */
    public final boolean isAtomic() { return ilk().isAtomic(); }

    /** Just the negation of {@link #isAtomic()} */
    public final boolean isComposite() { return !isAtomic(); }

    /** Checks whether this Slaw's ilk is {@link SlawIlk#NIL} */
    public final boolean isNil() { return is(NIL); }

    /** Checks whether this Slaw's ilk is {@link SlawIlk#BOOL} */
    public final boolean isBoolean() { return is(BOOL); }

    /** Checks whether this Slaw's ilk is {@link SlawIlk#STRING} */
    public final boolean isString() { return is(STRING); }

    /**
     * Checks whether this Slaw belongs to one of the numeric ilks.
     * Numeric ilks are defined by the predicate {@link
     * SlawIlk#isNumeric}.
     * <p>
     * Numeric slawx can be either atomic ({@link SlawIlk#NUMBER}) or
     * composite ({@link SlawIlk#COMPLEX}, {@link
     * SlawIlk#NUMBER_VECTOR}, {@link SlawIlk#COMPLEX_VECTOR}, {@link
     * SlawIlk#MULTI_VECTOR}, {@link SlawIlk#NUMBER_ARRAY}, {@link
     * SlawIlk#COMPLEX_ARRAY}, {@link SlawIlk#VECTOR_ARRAY}, {@link
     * SlawIlk#COMPLEX_VECTOR_ARRAY} and {@link
     * SlawIlk#MULTI_VECTOR_ARRAY}). For the latter, the numeric ilk
     * refers, transitively, to the numeric ilk of their components,
     * which will always be of the same ilk (all numbers, or all
     * vectors, and so on) and have the same numeric ilk
     * ({@link NumericIlk#INT8}, {@link NumericIlk#UNT32}, etc.).
     */
    public final boolean isNumeric() { return ilk().isNumeric(); }

    /**
     * Checks whether this Slaw's ilk is equal to <code>NUMBER</code>.
     * Note that <code>isNumber()</code> implies {@link #isNumeric()},
     * but that not all numeric slawx are numbers.
     */
    public final boolean isNumber() { return is(NUMBER); }

    /**
     * Checks whether this Slaw is a number (using
     * <code>isNumber()</code>) and that its numeric ilk equals
     * <code>ni</code>.
     */
    public final boolean isNumber(NumericIlk ni) {
        return isNumber() && numericIlk() == ni;
    }

    /** Checks whether this Slaw's ilk is {@link SlawIlk#COMPLEX} */
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

    /** Checks whether this Slaw's ilk is {@link SlawIlk#NUMBER_VECTOR} */
    public final boolean isNumberVector() { return is(NUMBER_VECTOR); }

    /** Checks whether this Slaw's ilk is {@link SlawIlk#COMPLEX_VECTOR} */
    public final boolean isComplexVector() { return is(COMPLEX_VECTOR); }

    /** Checks whether this Slaw's ilk is {@link SlawIlk#MULTI_VECTOR} */
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

    /** Checks whether this Slaw's ilk is {@link SlawIlk#NUMBER_ARRAY} */
    public final boolean isNumberArray() { return is(NUMBER_ARRAY); }

    /** Checks whether this Slaw's ilk is {@link SlawIlk#COMPLEX_ARRAY} */
    public final boolean isComplexArray() { return is(COMPLEX_ARRAY); }

    /** Checks whether this Slaw's ilk is {@link SlawIlk#VECTOR_ARRAY} */
    public final boolean isNumberVectorArray() { return is(VECTOR_ARRAY); }

    /**
     * Checks whether this Slaw's ilk is {@link
     * SlawIlk#COMPLEX_VECTOR_ARRAY}
     */
    public final boolean isComplexVectorArray() {
        return is(COMPLEX_VECTOR_ARRAY);
    }

    /**
     * Checks whether this Slaw's ilk is {@link
     * SlawIlk#MULTI_VECTOR_ARRAY}
     */
    public final boolean isMultivectorArray() {
        return is(MULTI_VECTOR_ARRAY);
    }

    /** Checks whether this Slaw's ilk is {@link SlawIlk#CONS} */
    public final boolean isCons() { return is(CONS); }

    /** Checks whether this Slaw's ilk is {@link SlawIlk#LIST} */
    public final boolean isList() { return is(LIST); }

    /** Checks whether this Slaw's ilk is {@link SlawIlk#MAP} */
    public final boolean isMap() { return is(MAP); }

    /**
     * Checks whether this Slaw's ilk is {@link SlawIlk#PROTEIN}.
     * Slawx with this ilk can be down-casted to Protein using {@link
     * #toProtein}.
     */
    public final boolean isProtein() { return is(PROTEIN); }

    /**
     * When this Slaw has ilk {@link SlawIlk#BOOL}, emits the
     * corresponding boolean value it encapsulates. Otherwise, an
     * <code>UnsupportedOperationException</code> is thrown.
     */
    public abstract boolean emitBoolean();

    /**
     * When this Slaw has ilk {@link SlawIlk#STRING}, emits the
     * corresponding String value it encapsulates. Otherwise, an
     * UnsupportedOperationException is thrown.
     */
    public abstract String emitString();

    /**
     * When this Slaw has ilk {@link SlawIlk#NUMBER}, emits the
     * corresponding value it encapsulates, cast to a long. Otherwise,
     * an <code>UnsupportedOperationException</code> is thrown.
     *
     * <p> If the numeric ilk of this Slaw is either {@link
     * NumericIlk#FLOAT32} or {@link NumericIlk#FLOAT64}, a loss of
     * precision may occurr, equivalent to the one implied in a
     * conversion from float or double to long. Therefore, for those
     * numeric ilks, it's safer to use {@link #emitDouble} instead.
     *
     * <p> Likewise, for slaw numbers of numeric ilk {@link
     * NumericIlk#UNT64}, the eight bytes representing its value will
     * be interpreted as the 2-complement representation of the
     * returned long; thus, unless you're sure that this Slaw's value
     * is below <code>Long.MAX_VALUE</code>, it is safer to use {@link
     * #emitBigInteger} for <code>UNT64</code> numbers.
     */
    public abstract long emitLong();

    /**
     * Equivalent to downcasting the result of {@link #emitLong} to
     * <code>int</code>. If the numerick ilk of this Slaw corresponds
     * to an integral 64-bit type, or a floating point, there's a risk
     * of losing precision due to the coercion. There's also a
     * potential coercion problem for {@link NumericIlk#UNT32}, in
     * that the bit pattern of the unsigned int will be reinterpreted
     * as a signed value.
     *
     * <p> If this Slaw is not a number, an
     * <code>UnsupportedOperationException</code> is thrown.
     */
    public abstract int emitInt();

    /**
     * Equivalent to downcasting the result of {@link #emitLong} to
     * <code>short</code>. If the numerick ilk of this Slaw
     * corresponds to an integral 64-bit or 32-bit type, or a floating
     * point, there's a risk of losing precision due to the coercion.
     * There's also a potential coercion problem for {@link
     * NumericIlk#UNT16}, in that the bit pattern of the unsigned int
     * will be reinterpreted as a signed value.
     *
     * <p> If this Slaw is not a number, an
     * <code>UnsupportedOperationException</code> is thrown.
     */
    public abstract short emitShort();

    /**
     * Equivalent to downcasting the result of {@link #emitLong} to
     * <code>byte</code>. If the numerick ilk of this Slaw corresponds
     * to an integer wider than 8 bits, or a floating point, there's a
     * risk of losing precision due to the coercion. There's also a
     * potential coercion problem for {@link NumericIlk#UNT8}, in that
     * the bit pattern of the unsigned int will be reinterpreted as a
     * signed value.
     *
     * <p> If this Slaw is not a number, an
     * <code>UnsupportedOperationException</code> is thrown.
     */
    public abstract byte emitByte();

    /**
     * When this Slaw has ilk {@link SlawIlk#NUMBER}, emits the
     * corresponding value it encapsulates, cast to a double.
     * Otherwise, an <code>UnsupportedOperationException</code> is
     * thrown.
     *
     * <p> If the numeric ilk of this Slaw corresponds to an integral
     * numeric type, a loss of precision may occurr, equivalent to the
     * one implied in a conversion from an Java integer type to
     * double. Therefore, for those numeric ilks, it's safer to use
     * {@link #emitLong} or {@link #emitBigInteger} instead.
     */
    public abstract double emitDouble();

    /**
     * Equivalent to downcasting the result of {@link #emitDouble} to a
     * <code>float</code>. There's a risk of losing precision if this
     * Slaw's numeric ilk is {@link NumericIlk#FLOAT64} or any
     * integral ilk.
     *
     * <p> If this Slaw is not a number, an
     * <code>UnsupportedOperationException</code> is thrown.
     */
    public abstract float emitFloat();

    /**
     * When this Slaw has ilk {@link SlawIlk#NUMBER}, emits the
     * corresponding value it encapsulates, cast to a
     * <code>BigInteger</code>. Otherwise, an {@link
     * UnsupportedOperationException} is thrown.
     *
     * <p> If the numeric ilk of this Slaw corresponds to an integral
     * type, this method is guaranteed to return the actual value,
     * without losing precision. But, if the numeric ilk of this Slaw
     * is either {@link NumericIlk#FLOAT32} or {@link
     * NumericIlk#FLOAT64}, a loss of precision may occurr, equivalent
     * to the one implied in a conversion from float or double to
     * long. Therefore, for those numeric ilks, it's safer to use
     * {@link #emitDouble} instead.
     */
    public abstract BigInteger emitBigInteger();

    /**
     * When this Slaw has ilk {@link SlawIlk#CONS}, {@link
     * SlawIlk#COMPLEX} or {@link SlawIlk#LIST} (and, in the latter
     * case, corresponds to a non-empty list), the first component of
     * the Slaw is returned. Otherwise, an
     * <code>UnsupportedOperationException</code> is thrown.
     *
     * <p> When supported, this operation returns the same value as
     * <code>nth(0)</code>.
     *
     * @see Slaw#cdr
     */
    public abstract Slaw car();

    /**
     * When this Slaw has ilk {@link SlawIlk#CONS}, {@link
     * SlawIlk#COMPLEX} or {@link SlawIlk#LIST} (and, in the latter
     * case, corresponds to a non-empty list), the second component of
     * the Slaw is returned (where, in the case of lists, the 'second
     * component' here means the list of all elements except the first
     * one). Otherwise, an <code>UnsupportedOperationException</code>
     * is thrown.
     *
     * <p> When supported, this operation returns the same value as
     * <code>nth(1)</code> for conses and complex numbers, and a list
     * consisting of all elements but the first when this Slaw is a
     * list.
     *
     * @see Slaw#car
     */
    public abstract Slaw cdr();

    /**
     * When this Slaw is a vector or multivector, its dimension will
     * be returned. Arrays have the dimension of their components. For
     * other ilks, this method returns 0.
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
     * {@link SlawIlk#PROTEIN}, this method will return 0.
     */
    public abstract int count();

    /**
     * Access to the nth sub-slaw of this Slaw instance, where
     * <code>n</code> is a zero-based index. This method is guaranteed
     * to succeed and return a non-null value provided <code>n</code>
     * is less than the value returned by count(). If that's not the
     * case, an (unchecked) <code>IndexOutOfBoundsException</code> is
     * thrown.
     *
     * <p> For complex numbers and conses, <code>nth(0)</code> and
     * <code>nth(1)</code> are equivalent to {@link #car()} and {@link
     * #cdr()}, respectively. For vectors and multivectors, it returns
     * the corresponding component, while for lists it acessess the
     * nth element. If this Slaw is a map, the returning Slaw is a
     * cons composed of the nth key and value.
     *
     * <p> Proteins have no sub-slaw.
     *
     * <p> This method, together with {@link #count}, is used to
     * implement all the list-related methods in the Slaw interface.
     * Therefore, its semantics constitute the way slawx of any kind
     * are viewed as lists of subslawx.
     */
    public abstract Slaw nth(int n);

    /**
     * Looks for <code>elem</code> among the sub-slawx of this Slaw
     * instance, using {@link #equals} as equality test. It returns
     * the index of <code>elem</code>, when found, or -1 otherwise.
     * Thus, it is always the case that the return value is less that
     * <code>count()</code>, and that, whenever this methods returns a
     * non-negative value, <code>e.equals(indexOf(e))</code> is true.
     */
    public final int indexOf(Slaw elem) {
        for (int i = 0, c = count(); i < c; i++)
            if (elem.equals(nth(i))) return i;
        return -1;
    }

    /**
     * Looks for <code>elem</code> among this Slaw's sub-slawx. This
     * method just checks whether {@link #indexOf} returns an index
     * greater than 0.
     */
    public final boolean contains(Slaw elem) { return indexOf(elem) >= 0; }

    /**
     * Creates a list with all of the sub-slawx of this Slaw. This
     * method uses {@link #nth} to construct the list, so a non-null
     * value will always be returned (albeit in some occasions it may
     * be an empty List), even if this Slaw is not of ilk {@link
     * SlawIlk#LIST}.
     */
    public final List<Slaw> emitList() { return emitList(0, count()); }

    /**
     * Returns a slice of the list constructed by {@link #emitList()}.
     *
     * <p> The slice contains elements beginning at index
     * <code>begin</code> and ending at either <code>end-1</code> or
     * <code>count()-1</code> if <code>end>count()</code>. If
     * <code>end<=begin</code>, an empty list is returned. Negative
     * end or begin values are transformed to positive values by
     * substracting them from <code>count()</code>.
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
     * {@link #nth}. This method makes Slaw an implementation of
     * <code>Iterable<Slaw></code>, and allows you to rewrite
     * old-style loops of the form:
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
     * <code>Iterator.remove</code> operation (because Slaw instances
     * are immutable).
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
     * Converts a Slaw of ilk {@link SlawIlk#MAP} to a Java native map. If
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
     * This method is equivalent to calling <code>get(key)</code> on
     * the map returned by {@link #emitMap()}, which see.
     */
    public abstract Slaw find(Slaw key);

    /**
     * Convenience method down-casting this Slaw to a Protein
     * instance. If this slaw's ilk is not {@link SlawIlk#PROTEIN}, an
     * <code>UnsupportedOperationException</code> is thrown.
     */
    public final Protein toProtein() {
        if (!isProtein())
            throw new UnsupportedOperationException(ilk() + " as protein");
        return (Protein)this;
    }

    /**
     * Equality predicate. This Slaw will only be equal to another
     * Slaw instance of the same ilk and numeric ilk that, moreover,
     * encapsulates the same underlying value, recursively.
     *
     * <p> Note that this notion of equality is stronger than the one
     * that could be induced by, say, just comparing emitted values,
     * or the subslawx of the two Slaw instances being tested. It is
     * thus possible for two different Slaw to have, for instance, the
     * same sub-slawx and yet be different (one could be a list of
     * pairs and the other one a map), or to return the same value in
     * {@link #emitLong()} and yet have different numeric ilks (and,
     * hence, classified as distinct slawx by this predicate).
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
     * {@link #isNumeric}), this method throws an
     * <code>UnsupportedOperationException</code>.
     */
    public abstract Slaw withNumericIlk(NumericIlk ilk);

    /** Auxiliarly method used by equals. */
    public abstract boolean slawEquals(Slaw s);

    /** Auxiliarly method used by toString. */
    public abstract String debugString();

    /**
     * A factory method constructing a Slaw with ilk {@link
     * SlawIlk#CONS} out of its two components. Both arguments must be
     * non-null; otherwise, an <code>IllegalArgumentException</code>
     * is thrown.
     */
    public static Slaw cons(Slaw car, Slaw cdr) {
        return factory.cons(car, cdr);
    }

    /**
     * A factory method constructing a Slaw with ilk {@link
     * SlawIlk#LIST} out of its components. Null arguments are
     * discarded. Thus, <code>Slaw.list(null)</code> returns an empty
     * Slaw list, as does <code>Slaw.list()</code>.
     */
    public static Slaw list(Slaw... s) { return factory.list(s); }

    /**
     * A factory method constructing a Slaw with ilk {@link
     * SlawIlk#LIST} from a native Java list. Null elements of
     * <code>s</code> are discarded. If its argument is null, it
     * returns an empty Slaw list.
     */
    public static Slaw list(List<Slaw> s) { return factory.list(s); }

    /**
     * A factory method constructing a Slaw with ilk {@link SlawIlk#MAP}
     * from a a explicit list of slawx. The arguments are interpreted
     * as alternating keys and values. If any of the slawx in a
     * key/value pair is null, the whole pair is skipped.
     */
    public static Slaw map(Slaw... kvs) { return factory.map(kvs); }

    /**
     * A factory method constructing a Slaw with ilk {@link
     * SlawIlk#LIST} from a native Java map. If any of the slawx in a
     * key/value pair is null, the whole pair is skipped. When
     * <code>m</code> is null, an empty Slaw map is returned.
     */
    public static Slaw map(Map<Slaw,Slaw> m) { return factory.map(m); }

    /**
     * A factory method creating a Protein. Calling this method is
     * equivalent to calling <code>protein(descrips, ingests, null)</code>
     * (i.e., it constructs a protein without raw data).
     */
    public static Protein protein(Slaw descrips, Slaw ingests) {
        return factory.protein(descrips, ingests, null);
    }

    /**
     * A factory method constructing a Slaw with ilk {@link
     * SlawIlk#PROTEIN} out of its three components: descrips, ingests
     * and raw data. Any argument can be null, denoting, respectively,
     * a protein without descrips, ingests or raw data. There are no
     * restrictions on the ilk of descrips and ingests.
     */
    public static Protein protein(Slaw descrips, Slaw ingests, byte[] data) {
        return factory.protein(descrips, ingests, data);
    }

    /**
     * A factory method creating a Slaw with ilk {@link SlawIlk#NIL}.
     * All nil slawx are equal under the {@link #equals} predicate,
     * but no guarantees are made regarding the equality, under
     * <code>==</code>, of the instances returned by different calls
     * to this method.
     */
    public static Slaw nil() { return factory.nil(); }

    /**
     * A factory method creating a Slaw with ilk {@link SlawIlk#BOOL}.
     * All true slawx are equal under the {@link #equals} predicate,
     * as are all false ones, but no guarantees are made regarding the
     * equality, under <code>==</code>, of the instances returned by
     * different calls to this method.
     */
    public static Slaw bool(boolean v) { return factory.bool(v); }

    /**
     * A factory method creating a Slaw with ilk {@link SlawIlk#STRING}.
     * When serialized, Slaw strings are always encoded using UTF-8.
     */
    public static Slaw string(String s) { return factory.string(s); }

    /**
     * Equivalent to calling <code>Slaw.number(SlawIlk.INT8, n)</code>.
     */
    public static Slaw int8(int n) { return number(INT8, n); }

    /**
     * Equivalent to calling <code>Slaw.number(SlawIlk.INT16, n)</code>.
     */
    public static Slaw int16(int n) { return number(INT16, n); }

    /**
     * Equivalent to calling <code>Slaw.number(SlawIlk.INT32, n)</code>.
     */
    public static Slaw int32(int n) { return number(INT32, n); }

    /**
     * Equivalent to calling <code>Slaw.number(SlawIlk.INT64, n)</code>.
     */
    public static Slaw int64(long n) { return number(INT64, n); }

    /**
     * Equivalent to calling <code>Slaw.number(SlawIlk.UNT8, n)</code>.
     */
    public static Slaw unt8(int n) { return number(UNT8, n); }

    /**
     * Equivalent to calling <code>Slaw.number(SlawIlk.UNT16, n)</code>.
     */
    public static Slaw unt16(int n) { return number(UNT16, n); }

    /**
     * Equivalent to calling <code>Slaw.number(SlawIlk.UNT32, n)</code>.
     */
    public static Slaw unt32(long n) { return number(UNT32, n); }

    /**
     * Equivalent to calling <code>Slaw.number(SlawIlk.UNT64, n)</code>.
     */
    public static Slaw unt64(long n) { return number(UNT64, n); }

    /**
     * A factory method creating a Slaw with ilk {@link SlawIlk#NUMBER}
     * and numeric ilk {@link NumericIlk#UNT64}. Only the 8 less
     * significant bytes of <code>n</code> are considered, and they're
     * interpreted as representing a positive number. When your Slaw's
     * value is in the lower half of UNT64's domain, you can just use
     * {@link #unt64(long)} instead.
     */
    public static Slaw unt64(BigInteger n) { return factory.number(n);}

    /**
     * Equivalent to calling <code>Slaw.number(SlawIlk.FLOAT32, n)</code>.
     * Note that that means the overload of <code>Slaw.number</code> taking
     * a double as second argument.
     */
    public static Slaw float32(float n) { return number(FLOAT32, n); }

    /**
     * Equivalent to calling <code>Slaw.number(SlawIlk.FLOAT64, n)</code>.
     * Note that that means the overload of <code>Slaw.number</code> taking
     * a double as second argument.
     */
    public static Slaw float64(double n) { return number(FLOAT64, n);}

    /**
     * Factoring method constructing a Slaw with ilk {@link
     * SlawIlk#NUMBER} and the given numeric ilk and value. If n is
     * the width, in bytes, of the requested numeric ilk (as returned
     * by <code>ni.width()</code>), only the n less significant bytes
     * of <code>value</code> are used. When <code>ni</code> is signed,
     * those bytes' bits are interpreted as a 2-complement bit
     * pattern, and the other way around: passing a negative value
     * when constructing an unsigned ilk will reinterpret the value's
     * 2-complement bits as denoting a positive value. All that just
     * means that, as long as you pass an integer value to this method
     * which is within the range of the desired numeric ilk, things
     * will work as expected.
     *
     * <p> The numeric ilk {@link NumericIlk#UNT64} is a corner case,
     * because to represent the upper half of its domanin using Java
     * long values you need to use negative values and keep in mind
     * their actual bit representation. An alternative is to use
     * {@link #unt64(BigInteger)}, which will be less error-prone in
     * exchange of a bit of performance.
     *
     * <p> If you want to construct a number of a non-integral numeric
     * ilk, you should be using the overload of this method that takes
     * a double as second argument, to avoid the loss of precision
     * involved in converting from long to double.
     */
    public static Slaw number(NumericIlk ni, long value) {
        return factory.number(ni, value);
    }

    /**
     * Factoring method constructing a Slaw with ilk {@link
     * SlawIlk#NUMBER} and the given numeric ilk and value. Althought
     * you can call this method with any numeric ilk, the common case
     * will be to use it when creating Slaw number with numeric ilk
     * {@link NumericIlk#FLOAT32} or {@link NumericIlk#FLOAT64}. When
     * called with an integral ilk as its first argument, this method
     * behaves as <code>number(ni, (long)value)</code>, and you'll
     * need to care about the possible rounding errors that the cast
     * can produce.
     */
    public static Slaw number(NumericIlk ni, double value) {
        return factory.number(ni, value);
    }

    /**
     * A factory method constructing a Slaw with ilk {@link
     * SlawIlk#COMPLEX} out of its real and imaginary parts. Both
     * arguments must be non-null and have ilk {@link SlawIlk#NUMBER};
     * otherwise, an <code>IllegalArgumentException</code> is thrown.
     * They don't need to have the same numeric ilk, though. When the
     * numeric ilks of the real and imaginary parts differ, the
     * numeric ilk of the result is the one returned by {@link
     * NumericIlk#dominantIlk}, which see (basically, a widening
     * numerical conversion one is used).
     *
     * <p> For instance, the result of calling
     * <code>complex(int8(1), float32(2))</code> will be a complex Slaw
     * with numeric ilk <code>FLOAT32</code>.
     */
    public static Slaw complex(Slaw re, Slaw im) {
        return factory.complex(re, im);
    }

    /**
     * A factory method constructing a 2-dimensional vector Slaw out of
     * its components. Both arguments must be non-null and have ilk
     * the same ilk, which must be either {@link SlawIlk#NUMBER} or
     * {@link SlawIlk#COMPLEX}; otherwise, an
     * <code>IllegalArgumentException</code> is thrown. The ilk of the
     * resulting vector will be either {@link SlawIlk#NUMBER_VECTOR}
     * or {@link SlawIlk#COMPLEX_VECTOR}.
     *
     * <p> They arguments don't need to have the same numeric ilk,
     * though. When the numeric ilks of the given components differ,
     * the numeric ilk of the result is the one returned by {@link
     * NumericIlk#dominantIlk}, which see (basically, a widening
     * numerical conversion one is used).
     *
     * For instance, the result of calling
     * <pre>
     *   vector(complex(int8(1), float32(2)),
     *          complex(int16(3), unt16(4)))
     * </pre>
     *
     * will be a vector Slaw with ilk {@link SlawIlk#COMPLEX_VECTOR}
     * and numeric ilk {@link NumericIlk#FLOAT32}.
     */
    public static Slaw vector(Slaw x, Slaw y) {
        return factory.vector(x, y);
    }

    /**
     * A factory method constructing a 3-dimensional vector Slaw out of
     * its components. The semantics and constraints on its arguments
     * as the same as those of {@link #vector(Slaw,Slaw)}, which see.
     */
    public static Slaw vector(Slaw x, Slaw y, Slaw z) {
        return factory.vector(x, y, z);
    }

    /**
     * A factory method constructing a 4-dimensional vector Slaw out of
     * its components. The semantics and constraints on its arguments
     * as the same as those of {@link #vector(Slaw,Slaw)}, which see.
     */
    public static Slaw vector(Slaw x, Slaw y, Slaw z, Slaw w) {
        return factory.vector(x, y, z, w);
    }

    /**
     * A factory method constructing a Slaw multivector out of its
     * components. The number of arguments must be 4, 8, 16 or 32 (for
     * a multivector of dimension 2, 3, 4 or 5), and all must be
     * non-null and have ilk {@link SlawIlk#NUMBER}; otherwise an
     * <code>IllegalArgumentException</code> is thrown.
     *
     * <p> The arguments don't need to have the same numeric ilk,
     * though. When the numeric ilks of the given components differ,
     * the numeric ilk of the result is the one returned by {@link
     * NumericIlk#dominantIlk}, which see (basically, a widening
     * numerical conversion one is used).
     */
    public static Slaw multivector(Slaw... cs) {
        return factory.multivector(cs);
    }

    /**
     * A factory method constructing a non-empty numeric Slaw array out
     * of its components. The given array must have at least one
     * non-null element; otherwise, an
     * <code>IllegalArgumentException</code> is thrown.
     *
     * <p> Calling this method is equivalent to calling
     * <code>array(sx[0],sx[1],...sx[sx.length - 1])</code>. See
     * {@link #array(Slaw,Slaw...)} for details.
     */
    public static Slaw array(Slaw[] sx) { return factory.array(sx); }

    /**
     * A factory method constructing a non-empty numeric Slaw array out
     * of its components. At least one of the arguments must be
     * non-null (nulls are skipped), and all non-null arguments must
     * have the same ilk (otherwise an
     * <code>IllegalArgumentException</code> is thrown). That common
     * ilk must be numeric (i.e., return true for {@link
     * SlawIlk#isNumeric}) but not an array ilk (i.e. return false for
     * {@link SlawIlk#isArray}). The ilk of the resulting array is
     * derived from that that of its components in the natural way,
     * and can therefore be any of the array ilks (as defined by the
     * predicate {@link SlawIlk#isArray}).
     *
     * <p> As is the case with all other composite numeric Slaw
     * constructors, the arguments don't need to have the same numeric
     * ilk, though. When the numeric ilks of the given components
     * differ, the numeric ilk of the result is the one returned by
     * {@link NumericIlk#dominantIlk}, which see (basically, a
     * widening numerical conversion one is used).
     */
    public static Slaw array(Slaw n, Slaw... ns) {
        return factory.array(n, ns);
    }

    /**
     * A factory method constructing a non-empty numeric Slaw array out
     * of its components. At least one of the elements of the given
     * list must be non-null (nulls are skipped), and all non-null
     * arguments must have the same ilk (otherwise an
     * <code>IllegalArgumentException</code> is thrown). See the two
     * other array constructors for further restrictions on those
     * elements, if any.
     */
     public static Slaw array(List<Slaw> sx) {
        Slaw[] ss = new Slaw[sx.size()];
        for (int i = 0; i < ss.length; ++i) ss[i] = sx.get(i);
        return array(ss);
    }

    /**
     * Returns a new empty array with ilk {@link SlawIlk#NUMBER_ARRAY}
     * and the given numeric ilk.
     */
    public static Slaw emptyNumberArray(NumericIlk ni) {
        return factory.array(NUMBER_ARRAY, ni, 1);
    }

    /**
     * Returns a new empty array with ilk {@link
     * SlawIlk#COMPLEX_ARRAY} and the given numeric ilk.
     */
    public static Slaw emptyComplexArray(NumericIlk ni) {
        return factory.array(COMPLEX_ARRAY, ni, 1);
    }

    /**
     * Returns a new empty array with ilk {@link SlawIlk#VECTOR_ARRAY}
     * and the given numeric ilk and dimension, which must be between
     * 2 and 4.
     */
    public static Slaw emptyVectorArray(NumericIlk ni, int dim) {
        return factory.array(VECTOR_ARRAY, ni, dim);
    }

    /**
     * Returns a new empty array with ilk {@link
     * SlawIlk#COMPLEX_VECTOR_ARRAY} and the given numeric ilk and
     * dimension, which must be between 2 and 4.
     */
    public static Slaw emptyComplexVectorArray(NumericIlk ni, int dim) {
        return factory.array(COMPLEX_VECTOR_ARRAY, ni, dim);
    }

    /**
     * Returns a new empty array with ilk {@link
     * SlawIlk#MULTI_VECTOR_ARRAY} and the given numeric ilk and
     * dimension, which must be between 2 and 5.
     */
    public static Slaw emptyMultiVectoryArray(NumericIlk ni, int dim) {
        return factory.array(MULTI_VECTOR_ARRAY, ni, dim);
    }

    /**
     * Generic empty array constructor. The ilk must be of kind array,
     * and the dimension one of the accepted values for the given ilk.
     */
    public static Slaw array(SlawIlk ilk, NumericIlk ni, int dimension) {
        return factory.array(ilk, ni, dimension);
    }

    protected Slaw() {}

    private static final com.oblong.jelly.slaw.SlawFactory factory =
        new com.oblong.jelly.slaw.java.JavaSlawFactory();

}
