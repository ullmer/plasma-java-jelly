// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.List;

final class SlawMultivector extends CompositeNumericSlaw {

    static Slaw valueOf(Slaw v00, Slaw v01, Slaw v10, Slaw v11) {
        assert SlawIlk.haveSameIlk(v00, v01, v10, v11) && v00.isNumber();
        NumericIlk ni = NumericIlk.dominantIlk(v00, v01, v10, v11);
        return new SlawMultivector(ni, v00, v01, v10, v11);
    }

    static Slaw valueOf(Slaw u, Slaw d) {
        assert u.isMultivector() && d.isMultivector()
            && u.count() == d.count();
        List<Slaw> us = u.emitList();
        us.addAll(d.emitList());
        return new SlawMultivector(NumericIlk.dominantIlk(u, d), us);
    }

    @Override public SlawIlk ilk() { return SlawIlk.MULTI_VECTOR; }

    @Override public Slaw withNumericIlk(NumericIlk ilk) {
        return (ilk == numericIlk()) ?
            this : new SlawMultivector(ilk, elements);
    }

    private SlawMultivector(NumericIlk i, Slaw... l) { super(i, l); }
    private SlawMultivector(NumericIlk i, List<Slaw> l) { super(i, l); }
}
