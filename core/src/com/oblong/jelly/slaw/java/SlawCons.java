// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.java;

import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIlk;

import net.jcip.annotations.Immutable;

@Immutable
final class SlawCons extends SlawPair {

    static Slaw valueOf(Slaw f, Slaw s) { return new SlawCons(f, s); }

    @Override public SlawIlk ilk() { return SlawIlk.CONS; }
    @Override public NumericIlk numericIlk() { return null; }

    @Override public int dimension() { return 0; }

    private SlawCons(Slaw f, Slaw s) { super(f, s); }
}
