// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * Created: Mon Apr 19 02:26:18 2010
 *
 * @author jao
 */
abstract class NativeSlawVector extends NativeCompositeNumericSlaw {

    static Slaw valueOf(Slaw... cmps) {
        NumericIlk ni = commonIlk(cmps);
        if (ni == NumericIlk.NAN) return zero(cmps.length);
        if (cmps[0].is(SlawIlk.NUMBER))
            return new NativeNumberSlawVector(ni, cmps);
        if (cmps[0].is(SlawIlk.COMPLEX))
            return new NativeComplexSlawVector(ni, cmps);
        return zero(cmps.length);
    }

    NativeSlawVector(NumericIlk ilk, Slaw[] elems) {
        super(ilk, elems);
    }

    @Override public final int dimension() { return count(); }

    private static final Slaw zero(int len) {
        return zeroes[Math.min(2, Math.max(0, len - 2))];
    }

    private static final Slaw[] zeroes = new Slaw[3];
    static {
        Slaw z = NativeSlawInt32.ZERO;
        zeroes[0] = new NativeNumberSlawVector(NumericIlk.INT32, z, z);
        zeroes[1] = new NativeNumberSlawVector(NumericIlk.INT32, z, z, z);
        zeroes[2] = new NativeNumberSlawVector(NumericIlk.INT32, z, z, z, z);
    }
}

final class NativeNumberSlawVector extends NativeSlawVector {

    @Override public SlawIlk ilk() { return SlawIlk.VECTOR; }

    @Override public Slaw withNumericIlk(NumericIlk ilk) {
        if (ilk == numericIlk()) return this;
        return new NativeNumberSlawVector(ilk, (Slaw[])asList().toArray());
    }

    NativeNumberSlawVector(NumericIlk i, Slaw... l) {
        super(i, l);
    }
}

final class NativeComplexSlawVector extends NativeSlawVector {

    @Override public SlawIlk ilk() { return SlawIlk.COMPLEX_VECTOR; }

    @Override public Slaw withNumericIlk(NumericIlk ilk) {
        if (ilk == numericIlk()) return this;
        return new NativeComplexSlawVector(ilk, (Slaw[])asList().toArray());
    }

    NativeComplexSlawVector(NumericIlk i, Slaw... l) {
        super(i, l);
    }
}
