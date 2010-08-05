// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.NumericIlk;

@Immutable
final class SlawInt32 extends SlawNumber {

    SlawInt32(int v) { value = v; }

    @Override public NumericIlk numericIlk() { return NumericIlk.INT32; }
    @Override public long emitLong() { return value; }

    static final SlawInt32 ZERO = new SlawInt32(0);

    private int value;
}