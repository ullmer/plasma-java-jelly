// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.NumericIlk;

@Immutable
final class SlawUnt16 extends SlawNumber {
    SlawUnt16(int v) { value = (int)normalize(v, 0xFFFF); }

    @Override public NumericIlk numericIlk() { return NumericIlk.UNT16; }
    @Override public long emitLong() { return value; }
    @Override public int emitInt() { return value; }

    private int value;
}