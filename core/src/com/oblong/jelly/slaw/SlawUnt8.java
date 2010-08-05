package com.oblong.jelly.slaw;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.NumericIlk;

@Immutable
final class SlawUnt8 extends SlawNumber {

    SlawUnt8(short v) { value = (short)normalize(v,0xFF); }

    @Override public NumericIlk numericIlk() { return NumericIlk.UNT8; }
    @Override public long emitLong() { return value; }

    private short value;
}