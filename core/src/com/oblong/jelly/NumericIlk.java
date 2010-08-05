// Copyright (c) 2010 Oblong Industries
// Created: Fri Apr 23 13:34:13 2010

package com.oblong.jelly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Enumeration describing the type of values encapsulated by numeric
 * Slaw.
 * <p>
 * When a Slaw's ilk is numeric, we need need to further specify
 * whether it's an integral or floating point value, as well as its
 * width and sign. Instances of this enumeration encapsulate that
 * information and serve to fully specify (when combined with {@link
 * SlawIlk}) the type of values encapsulated by a Slaw instance.
 * <p>
 * The order of the members of this enum also serves to define
 * widening conversions from one numeric ilk to another. This ordering
 * is used by composite numeric Slaw constructors to allow composition
 * of {@link Slaw} instances with heterogeneous numeric ilks.
 *
 * @author jao
 */
public enum NumericIlk {

    FLOAT64(true, false, 64), FLOAT32(true, false, 32),
    UNT64(false, true, 64), INT64(true, true, 64),
    UNT32(false, true, 32), INT32(true, true, 32),
    UNT16(false, true, 16), INT16(true, true, 16),
    UNT8(false, true, 8), INT8(true,true, 8);

    /** The number of bits used to represented values of this ilk. */
    public int width() { return width; }

    /** The number of bytes used to represented values of this ilk. */
    public int bytes() { return bsize; }

    /** Whether this ilk represents signed numbers. */
    public boolean isSigned() { return signed; }

    /**
     * Whether this ilk represents integral (as opposed to
     * floating-point) numbers.
     */
    public boolean isIntegral() { return integral; }

    /**
     * The maximum value representable by values of this ilk.
     * Makes sense only for integral ilks.
     */
    public long max() {
        return (1L << (width - (signed ? 1 : 0))) - 1;
    }

    /**
     * The minimum value representable by values of this ilk.
     * Makes sense only for integral ilks.
     */
    public long min() { return signed ? 1 - max() : 0; }

    /**
     * The maximum value representable by values of this ilk, cast to
     * a double. For integral ilks, {@link #max} is probably what
     * you want.
     */
    public double fmax() {
        if (integral) return (double)max();
        if (this == FLOAT32) return (double)Float.MAX_VALUE;
        return Double.MAX_VALUE;
    }

    /**
     * The minimum value representable by values of this ilk, cast to
     * a double. For integral ilks, {@link #min} will avoid casting
     * errors.
     */
    public double fmin() {
        if (integral) return (double)min();
        if (this == FLOAT32) return (double)Float.MIN_VALUE;
        return Double.MIN_VALUE;
    }

    /**
     * Chooses the ilk able to represent the highest positive value,
     * among those given. If any of those is null, returns null. If
     * the list is empty, returns INT8.
     */
    public static NumericIlk dominantIlk(List<NumericIlk> ilks) {
        try {
            return Collections.min(ilks);
        } catch (NoSuchElementException e) {
            return INT8;
        } catch (NullPointerException e) {
            return null;
        }
    }

    /** Equivalent to {@code dominantIlk(Arrays.asList(ilks))}. */
    public static NumericIlk dominantIlk(NumericIlk... ilks) {
        return dominantIlk(Arrays.asList(ilks));
    }

    /**
     * Calls {@link #dominantIlk(List)} on the list of
     * numeric ilks of the given Slawx.
     */
    public static NumericIlk dominantIlk(Slaw... nss) {
        List<NumericIlk> is = new ArrayList<NumericIlk>();
        for (Slaw s : nss) is.add(s.numericIlk());
        return dominantIlk(is);
    }

    /**
     * Calls {@link #dominantIlk(List)} on the list of
     * numeric ilks of the given Slawx.
     */
    public static NumericIlk dominantIlkForList(List<Slaw> nss) {
        List<NumericIlk> is = new ArrayList<NumericIlk>();
        for (Slaw s : nss) is.add(s.numericIlk());
        return dominantIlk(is);
    }

    private NumericIlk(boolean s, boolean i, int w) {
        signed = s;
        integral = i;
        width = w;
        bsize = w >> 3;
    }

    private final boolean signed;
    private final boolean integral;
    private final int width;
    private final int bsize;
}
