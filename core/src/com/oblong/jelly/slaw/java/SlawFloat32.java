// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.java;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.NumericIlk;

@Immutable
final class SlawFloat32 extends SlawNumber {
    SlawFloat32(float v) {
        value = v;
    }

    @Override public NumericIlk numericIlk() { return NumericIlk.FLOAT32; }
    @Override public double emitDouble() { return value; }
    @Override public float emitFloat() { return value; }
    @Override public long emitLong() { return (long)value; }

    private float value;
}