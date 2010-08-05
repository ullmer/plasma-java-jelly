// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.NumericIlk;

@Immutable
final class SlawInt8 extends SlawNumber {

    SlawInt8(byte v) { value = v; }

    @Override public NumericIlk numericIlk() { return NumericIlk.INT8; }
    @Override public long emitLong() { return value; }

    private byte value;
}