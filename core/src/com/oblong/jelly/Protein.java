// Copyright (c) 2010 Oblong Industries
// Created: Mon May 17 14:25:51 2010

package com.oblong.jelly;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.jcip.annotations.Immutable;

/**
 * Proteins constitute the units of information exchange between pools
 * and user programs.
 *
 * <p> Besides other Slawx (as ingests and descrips), the can contain
 * raw data (in the form of a byte array), and have associated
 * metadata related to the pool they came from. The latter consists of
 * a time stamp (when they were deposited in the pool) and an index
 * (where in the pool they're to be found, pools being essentially a
 * sequential arrangement of proteins).
 *
 * <p> This class specializes {@link Slaw}'s interface for proteins,
 * and extends it to provide access to that additional data and
 * metadata.
 *
 * <p> Protein instances are immutable, and they come into being
 * either by retrieval from a pool (via the {@link Hose} interface),
 * or by explicit creation using the factory method {@link
 * Slaw#protein(Slaw,Slaw)}. Proteins created used the latter lack
 * deposit metadata.
 *
 * @see Slaw
 *
 * @author jao
 */
@Immutable
public abstract class Protein extends Slaw {
    /**
     * Proteins that have not yet been deposited in a pool will return
     * this value as their index.
     */
    public static final long NO_INDEX = -1;

    /**
     * Proteins that have not yet been deposited in a pool will return
     * this value as their timestamp.
     */
    public static final long NO_TIMESTAMP = -1;

    /**
     * As a composite Slaw, proteins are made of ingests and descrips.
     * This methods gives access to the former. Since both of those
     * components are optional, the return value of this method can be
     * null.
     */
    public abstract Slaw ingests();

    /**
     * As a composite Slaw, proteins are made of ingests and descrips.
     * This methods gives access to the latter. Since both of those
     * components are optional, this method will return null for
     * proteins without descrips.
     */
    public abstract Slaw descrips();

    /** Number of bytes of raw data that this protein contains */
    public abstract int dataLength();

    /**
     * Accessor to individual bytes of raw data in this Protein.
     * Throws an <code>IndexOutOfBoundsException</code> if n is not in
     * the range <code>[0, dataLenth())</code>.
     */
    public abstract byte datum(int n);

    /**
     * Writes this protein's raw data to the given output stream, and
     * returns the number of bytes written, which should be equal to
     * the value returned by {@link #dataLength}. This method does not
     * return until all the raw data has been accepted by the stream,
     * or an I/O error occurs, in which case an
     * <code>IOException</code> (possibly thrown by the given output
     * stream) is thrown.
     */
    public abstract int putData(OutputStream os) throws IOException;

    /**
     * Returns a copy of this protein's raw data as a byte array.
     */
    public final byte[] copyData() throws IOException {
        final ByteArrayOutputStream s =
            new ByteArrayOutputStream(dataLength());
        putData(s);
        return s.toByteArray();
    }

    /**
     * The index of this protein in the pool it was retrieved from, or
     * <code>NO_INDEX</code> if this protein did not come from a pool.
     */
    public abstract long index();

    /**
     * The time, in the given units, when this Protein was deposited
     * in its source pool, or <code>NO_TIMESTAMP</code> if this
     * protein does not come from a pool.
     */
    public abstract long timestamp(TimeUnit unit);

    /**
     * The time, in seconds, when this Protein was deposited in its
     * source pool, or <code>NO_TIMESTAMP</code> (cast to a double) if
     * this protein does not come from a pool.
     */
    public abstract double timestamp();

    /**
     * For proteins obtained by means of a Hose, this method returns a
     * reference to the source Hose instance. For proteins created by
     * other means, this method returns null.
     */
    public abstract Hose source();

    /** Returns {@link SlawIlk#PROTEIN}. */
    @Override public final SlawIlk ilk() { return SlawIlk.PROTEIN; }

    /** Returns null. */
    @Override public final NumericIlk numericIlk() { return null; }

    /** Throws an <code>UnsupportedOperationException</code>. */
    @Override public final boolean emitBoolean() {
        throw new UnsupportedOperationException(ilk() + " as boolean");
    }

    /** Throws an <code>UnsupportedOperationException</code>. */
    @Override public final String emitString() {
        throw new UnsupportedOperationException(ilk() + " as string");
    }

    /** Throws an <code>UnsupportedOperationException</code>. */
    @Override public final long emitLong() {
        throw new UnsupportedOperationException(ilk() + " as long");
    }

    /** Throws an <code>UnsupportedOperationException</code>. */
    @Override public final int emitInt() {
        throw new UnsupportedOperationException(ilk() + " as int");
    }

    /** Throws an <code>UnsupportedOperationException</code>. */
    @Override public final short emitShort() {
        throw new UnsupportedOperationException(ilk() + " as short");
    }

    /** Throws an <code>UnsupportedOperationException</code>. */
    @Override public final byte emitByte() {
        throw new UnsupportedOperationException(ilk() + " as byte");
    }

    /** Throws an <code>UnsupportedOperationException</code>. */
    @Override public final double emitDouble() {
        throw new UnsupportedOperationException(ilk() + " as double");
    }

    /** Throws an <code>UnsupportedOperationException</code>. */
    @Override public final float emitFloat() {
        throw new UnsupportedOperationException(ilk() + " as float");
    }

    /** Throws an <code>UnsupportedOperationException</code>. */
    @Override public final BigInteger emitBigInteger() {
        throw new UnsupportedOperationException(ilk() + " as big integer");
    }

    @Override public final Map<Slaw,Slaw> emitMap() {
        return new HashMap<Slaw,Slaw>();
    }

    /** Throws an <code>UnsupportedOperationException</code>. */
    @Override public final Slaw withNumericIlk(NumericIlk ilk) {
        throw new UnsupportedOperationException(ilk() + " not numeric");
    }

    /** Throws an <code>UnsupportedOperationException</code>. */
    @Override public final Slaw car() {
        throw new UnsupportedOperationException(ilk() + " not a pair");
    }

    /** Throws an <code>UnsupportedOperationException</code>. */
    @Override public final Slaw cdr() {
        throw new UnsupportedOperationException(ilk() + " not a pair");
    }

    /** Returns 0. */
    @Override public final int dimension() { return 0; }

    /** Returns 0 (proteins are not traversable as lists). */
    @Override public final int count() { return 0; }

    /** Throws an <code>UnsupportedOperationException</code>. */
    @Override public final Slaw nth(int n) {
        throw new UnsupportedOperationException("Not a list");
    }

    /** Returns null (proteins are not translatable to maps). */
    @Override public final Slaw find(Slaw e) { return null; }

    @Override public final boolean slawEquals(Slaw o) {
        Protein op = o.toProtein();

        boolean eqs = index() == op.index()
            && timestamp() == timestamp()
            && op.dataLength() == dataLength()
            && eqRefs(descrips(), op.descrips())
            && eqRefs(ingests(), op.ingests());

        for (int i = 0, c = dataLength(); eqs && i < c; ++i)
            eqs = datum(i) == op.datum(i);
        return eqs;
    }

    @Override public final int hashCode() {
        int h = 5;
        if (descrips() != null) h += 13 * descrips().hashCode();
        if (ingests() != null) h += 17 * ingests().hashCode();
        for (int i = 0, c = dataLength(); i < c; ++i) h += datum(i);
        h += index();
        h += (int)timestamp(TimeUnit.NANOSECONDS);
        return h;
    }

    protected Protein() {}

    private static boolean eqRefs(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }
}
