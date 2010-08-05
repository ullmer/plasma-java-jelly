package com.oblong.jelly.slaw;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.NumericIlk;

@Immutable
final class SlawFloat32 extends SlawNumber {
    SlawFloat32(float v) {
        value = v;
    }

    @Override public NumericIlk numericIlk() { return NumericIlk.FLOAT32; }
    @Override public long emitLong() { return (long)value; }
    @Override public double emitDouble() { return value; }
    @Override public String debugString() { return "" + emitDouble(); }

    private float value;
}