// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.java;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.NumericIlk;

@Immutable
final class SlawInt8 extends SlawNumber {

    SlawInt8(byte v) { value = v; }

    @Override public NumericIlk numericIlk() { return NumericIlk.INT8; }
    @Override public long emitLong() { return value; }
    @Override public int emitInt() { return value; }
    @Override public short emitShort() { return value; }
    @Override public byte emitByte() { return value; }

    private byte value;
}