// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.java;

import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIlk;

import net.jcip.annotations.Immutable;

@Immutable
final class SlawComplex extends SlawPair {

    static Slaw valueOf(Slaw r, Slaw i) {
        assert r.isNumber() && i.isNumber();
        NumericIlk ilk =
            NumericIlk.dominantIlk(r.numericIlk(), i.numericIlk());
        return new SlawComplex(ilk, r, i);
    }

    @Override public int dimension() { return 1; }

    @Override public SlawIlk ilk() { return SlawIlk.COMPLEX; }
    @Override public NumericIlk numericIlk() { return car().numericIlk(); }

    @Override public Slaw withNumericIlk(NumericIlk ilk) {
        if (numericIlk() == ilk) return this;
        return new SlawComplex(ilk, car(), cdr());
    }

    private SlawComplex(NumericIlk ilk, Slaw r, Slaw i) {
        super(r.withNumericIlk(ilk), i.withNumericIlk(ilk));
    }
}
