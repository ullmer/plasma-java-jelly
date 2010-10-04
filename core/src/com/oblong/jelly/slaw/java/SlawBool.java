// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.java;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIlk;

@Immutable
final class SlawBool extends AtomicSlaw {

    static Slaw valueOf(boolean b) { return b ? TRUE : FALSE; }

    @Override public SlawIlk ilk() { return SlawIlk.BOOL; }
    @Override public NumericIlk numericIlk() { return null; }

    @Override public boolean emitBoolean() { return this == TRUE; }

    @Override public boolean slawEquals(Slaw o) {
        return o.emitBoolean() == emitBoolean();
    }

    @Override public String debugString() {
        return (this == TRUE) ? "true" : "false";
    }

    private SlawBool () {}

    private static final SlawBool TRUE = new SlawBool();
    private static final SlawBool FALSE = new SlawBool();
}