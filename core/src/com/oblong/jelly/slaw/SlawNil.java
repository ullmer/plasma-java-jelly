// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIlk;

@Immutable
final class SlawNil extends AtomicSlaw {

    static final SlawNil INSTANCE = new SlawNil();

    @Override public int dimension() { return 0; }

    @Override public SlawIlk ilk() { return SlawIlk.NIL; }
    @Override public NumericIlk numericIlk() { return null; }

    @Override public boolean slawEquals(Slaw o) { return true; }

    @Override public String debugString() { return "nil"; }

    private SlawNil () {}
}