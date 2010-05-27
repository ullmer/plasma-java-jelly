// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.List;

final class SlawMultivector extends CompositeNumericSlaw {

    static Slaw valueOf(Slaw... cs) {
        assert SlawIlk.haveSameIlk(cs) && cs[0].isNumber();
        NumericIlk ni = NumericIlk.dominantIlk(cs);
        return new SlawMultivector(ni, cs);
    }

    @Override public SlawIlk ilk() { return SlawIlk.MULTI_VECTOR; }

    @Override public int dimension() {
        int n = count();
        int d = 0;
        while (n > 0) {
            ++d;
            n = n>>1;
        }
        return d;
    }

    @Override public Slaw withNumericIlk(NumericIlk ilk) {
        return (ilk == numericIlk()) ?
            this : new SlawMultivector(ilk, elements);
    }

    private SlawMultivector(NumericIlk i, Slaw... l) { super(i, l); }
    private SlawMultivector(NumericIlk i, List<Slaw> l) { super(i, l); }
}
