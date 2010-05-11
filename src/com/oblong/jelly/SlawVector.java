// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.List;

final class SlawVector extends CompositeNumericSlaw {

    static Slaw valueOf(Slaw... cmps) {
        assert cmps.length > 2 && cmps.length < 5;
        NumericIlk ni = NumericIlk.dominantIlk(cmps);
        assert ni != NumericIlk.NAN;
        return new SlawVector(ni, cmps);
    }

    @Override public SlawIlk ilk() {
        return nth(0).isNumber() ? SlawIlk.VECTOR : SlawIlk.COMPLEX;
    }

    @Override public Slaw withNumericIlk(NumericIlk ilk) {
        if (ilk == numericIlk()) return this;
        return new SlawVector(ilk, elements);
    }

    SlawVector(NumericIlk i, Slaw... l) { super(i, l); }
    SlawVector(NumericIlk i, List<Slaw> l) { super(i, l); }
}
