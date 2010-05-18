package com.oblong.jelly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created: Fri Apr 23 13:34:13 2010
 *
 * @author jao
 */
public enum NumericIlk {

    NAN(false, false, 0),
        FLOAT64(true, false, 64), FLOAT32(true, false, 32),
        UNT64(false, true, 64), INT64(true, true, 64),
        UNT32(false, true, 32), INT32(true, true, 32),
        UNT16(false, true, 16), INT16(true, true, 16),
        UNT8(false, true, 8), INT8(true,true, 8);

    public int width() { return width; }
    public int bytes() { return bsize; }
    public boolean isSigned() { return signed; }
    public boolean isIntegral() { return integral; }
    public long max() {
        return (1L << (width - (signed ? 1 : 0))) - 1;
    }
    public long min() { return signed ? 1 - max() : 0; }
    public double fmax() {
        if (integral) return (double)max();
        if (this == FLOAT32) return (double)Float.MAX_VALUE;
        return Double.MAX_VALUE;
    }
    public double fmin() {
        if (integral) return (double)min();
        if (this == FLOAT32) return (double)Float.MIN_VALUE;
        return Double.MIN_VALUE;
    }

    public static NumericIlk dominantIlk(List<NumericIlk> ilks) {
        try {
            return Collections.min(ilks);
        } catch (NoSuchElementException e) {
            return INT8;
        }
    }

    public static NumericIlk dominantIlk(NumericIlk... ilks) {
        return dominantIlk(Arrays.asList(ilks));
    }

    public static NumericIlk dominantIlk(Slaw... nss) {
        List<NumericIlk> is = new ArrayList<NumericIlk>();
        for (Slaw s : nss) is.add(s.numericIlk());
        return dominantIlk(is);
    }

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
