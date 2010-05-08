// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.math.BigInteger;

final class SlawComplex extends SlawPair {

    static Slaw valueOf(Slaw r, Slaw i) {
        if (r.is(SlawIlk.NUMBER) && i.is(SlawIlk.NUMBER)) {
            NumericIlk ilk =
                NumericIlk.dominantIlk(r.numericIlk(), i.numericIlk());
            return new SlawComplex(ilk, r, i);
        }
        throw new UnsupportedOperationException(
            "Non-numeric complex components");
    }

    @Override public SlawIlk ilk() { return SlawIlk.COMPLEX; }
    @Override public NumericIlk numericIlk() { return car().numericIlk(); }

    @Override Slaw withNumericIlk(NumericIlk ilk) {
        if (numericIlk() == ilk) return this;
        return new SlawComplex(ilk, car(), cdr());
    }

    private SlawComplex(NumericIlk ilk, Slaw r, Slaw i) {
        super(r.withNumericIlk(ilk), i.withNumericIlk(ilk));
    }
}
