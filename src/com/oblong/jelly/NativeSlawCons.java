// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.math.BigInteger;

final class NativeSlawCons extends NativeSlawPair {

    static Slaw valueOf(Slaw f, Slaw s) { return new NativeSlawCons(f, s); }

    @Override public SlawIlk ilk() { return SlawIlk.CONS; }
    @Override public NumericIlk numericIlk() { return NumericIlk.NAN; }

    @Override public long asLong() { return 0; }
    @Override public double asDouble() { return 0; }
    @Override public BigInteger asBigInteger() { return BigInteger.ZERO; }

    @Override Slaw withNumericIlk(NumericIlk ilk) { return this; }

    private NativeSlawCons(Slaw f, Slaw s) { super(f, s); }
}
