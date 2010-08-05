// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.NumericIlk;

@Immutable
final class SlawFloat64 extends SlawNumber {
    SlawFloat64(double v) {
        value = v;
    }

    @Override public NumericIlk numericIlk() { return NumericIlk.FLOAT64; }
    @Override public long emitLong() { return (long)value; }
    @Override public double emitDouble() { return value; }
    @Override public String debugString() { return "" + emitDouble(); }

    private double value;
}