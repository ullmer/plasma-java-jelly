// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.math.BigInteger;

final class SlawComplex extends SlawPair {

    static Slaw valueOf(Slaw r, Slaw i) {
        assert r.isNumber() && i.isNumber();
        NumericIlk ilk =
            NumericIlk.dominantIlk(r.numericIlk(), i.numericIlk());
        return new SlawComplex(ilk, r, i);
    }

    @Override public SlawIlk ilk() { return SlawIlk.COMPLEX; }
    @Override public NumericIlk numericIlk() { return car().numericIlk(); }

    @Override Slaw withNumericIlk(NumericIlk ilk) {
        if (numericIlk() == ilk) return this;
        return new SlawComplex(ilk, car(), cdr());
    }

    @Override String debugString() {
        return car().debugString() + "+" + cdr().debugString() + "i";
    }

    private SlawComplex(NumericIlk ilk, Slaw r, Slaw i) {
        super(r.withNumericIlk(ilk), i.withNumericIlk(ilk));
    }
}
