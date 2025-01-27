// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.java;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.NumericIlk;

@Immutable
final class SlawUnt8 extends SlawNumber {

    SlawUnt8(short v) { value = (short)normalize(v,0xFF); }

    @Override public NumericIlk numericIlk() { return NumericIlk.UNT8; }
    @Override public long emitLong() { return value; }
    @Override public int emitInt() { return value; }
    @Override public short emitShort() { return value; }

    private short value;
}