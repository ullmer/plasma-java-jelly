// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

final class SlawCons extends SlawPair {

    static Slaw valueOf(Slaw f, Slaw s) { return new SlawCons(f, s); }

    @Override public SlawIlk ilk() { return SlawIlk.CONS; }
    @Override public NumericIlk numericIlk() { return null; }

    @Override public int dimension() { return 0; }

    @Override String debugString() {
        return "(" + car().toString() + ", " + cdr().toString() + ")";
    }

    private SlawCons(Slaw f, Slaw s) { super(f, s); }
}
