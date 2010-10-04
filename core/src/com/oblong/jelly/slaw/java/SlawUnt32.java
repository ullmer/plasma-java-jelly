// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.java;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.NumericIlk;

@Immutable
final class SlawUnt32 extends SlawNumber {

    SlawUnt32(long v) { value = normalize(v, 0xFFFFFFFF); }

    @Override public NumericIlk numericIlk() { return NumericIlk.UNT32; }
    @Override public long emitLong() { return value; }

    private long value;
}