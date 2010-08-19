// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.NumericIlk;

@Immutable
final class SlawInt16 extends SlawNumber {

    SlawInt16(short v) { value = v; }

    @Override public NumericIlk numericIlk() { return NumericIlk.INT16; }
    @Override public long emitLong() { return value; }
    @Override public int emitInt() { return value; }
    @Override public short emitShort() { return value; }

    private short value;
}