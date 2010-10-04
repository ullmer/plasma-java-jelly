// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.java;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.NumericIlk;

@Immutable
final class SlawInt64 extends SlawNumber {

    SlawInt64(long v) { value = v; }

    @Override public NumericIlk numericIlk() { return NumericIlk.INT64; }
    @Override public long emitLong() { return value; }

    private long value;
}