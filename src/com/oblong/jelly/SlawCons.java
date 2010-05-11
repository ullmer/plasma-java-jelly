// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

final class SlawCons extends SlawPair {

    static Slaw valueOf(Slaw f, Slaw s) { return new SlawCons(f, s); }

    @Override public SlawIlk ilk() { return SlawIlk.CONS; }
    @Override public NumericIlk numericIlk() { return NumericIlk.NAN; }


    @Override String debugString() {
        return "(" + car().debugString() + ", " + cdr().debugString() + ")";
    }

    private SlawCons(Slaw f, Slaw s) { super(f, s); }
}
