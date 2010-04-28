// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.math.BigInteger;

final class NativeSlawComplex extends NativeSlawPair {

    static Slaw valueOf(Slaw r, Slaw i) {
        if (r.is(SlawIlk.NUMBER) && i.is(SlawIlk.NUMBER)) {
            NumericIlk ilk =
                NumericIlk.dominantIlk(r.numericIlk(), i.numericIlk());
            return new NativeSlawComplex(ilk, r, i);
        }
        return ZERO;
    }

    @Override public SlawIlk ilk() { return SlawIlk.COMPLEX; }
    @Override public NumericIlk numericIlk() { return first.numericIlk(); }

    @Override public long asLong() { return first.asLong(); }
    @Override public double asDouble() { return first.asDouble(); }
    @Override public BigInteger asBigInteger() {
        return first.asBigInteger();
    }

    @Override Slaw withNumericIlk(NumericIlk ilk) {
        if (numericIlk() == ilk) return this;
        return new NativeSlawComplex(ilk, first, second);
    }

    private NativeSlawComplex(NumericIlk ilk, Slaw r, Slaw i) {
        super(r.withNumericIlk(ilk), i.withNumericIlk(ilk));
    }

    private static final Slaw ZERO =
        valueOf(NativeSlawInt32.ZERO, NativeSlawInt32.ZERO);
}
