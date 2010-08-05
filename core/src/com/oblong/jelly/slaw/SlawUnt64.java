// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw;

import java.math.BigInteger;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.NumericIlk;

@Immutable
final class SlawUnt64 extends SlawNumber {

    SlawUnt64(long v) { value = v; }

    SlawUnt64(BigInteger v) { value = v.longValue(); }

    @Override public NumericIlk numericIlk() { return NumericIlk.UNT64; }
    @Override public long emitLong() { return value; }

    @Override public BigInteger emitBigInteger() {
        return new BigInteger(1, toBytes(numericIlk(), value));
    }

    @Override public String debugString() {
        return emitBigInteger().toString();
    }

    private long value;
}