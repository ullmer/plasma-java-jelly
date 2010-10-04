// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.java;

import java.util.List;

import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIlk;

import net.jcip.annotations.Immutable;

@Immutable
final class SlawVector extends CompositeNumericSlaw {

    static Slaw valueOf(Slaw... cmps) {
        assert cmps.length > 1 && cmps.length < 5;
        NumericIlk ni = NumericIlk.dominantIlk(cmps);
        return new SlawVector(ni, cmps);
    }

    @Override public SlawIlk ilk() {
        return nth(0).isNumber() ? SlawIlk.NUMBER_VECTOR : SlawIlk.COMPLEX_VECTOR;
    }


    @Override public int dimension() { return count(); }

    @Override public Slaw withNumericIlk(NumericIlk ilk) {
        return (ilk == numericIlk()) ? this : new SlawVector(ilk, elements);
    }

    private SlawVector(NumericIlk i, Slaw... l) { super(i, l); }
    private SlawVector(NumericIlk i, List<Slaw> l) { super(i, l); }
}
