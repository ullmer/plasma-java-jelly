// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created: Sun Apr 18 19:12:34 2010
 *
 * @author jao
 */
abstract class NativeSlawNumber extends NativeAtomicSlaw {

    static Slaw valueOf(NumericIlk i, long value) {
        if (!i.isIntegral()) return valueOf(i, (double)value);
        if (i == NumericIlk.INT8) return new NativeSlawInt8((byte)value);
        if (i == NumericIlk.INT16) return new NativeSlawInt16((short)value);
        if (i == NumericIlk.INT32) return new NativeSlawInt32((int)value);
        if (i == NumericIlk.INT64) return new NativeSlawInt64(value);
        if (i == NumericIlk.UNT8) return new NativeSlawUnt8((short)value);
        if (i == NumericIlk.UNT16) return new NativeSlawUnt16((int)value);
        if (i == NumericIlk.UNT32) return new NativeSlawUnt32(value);
        if (i == NumericIlk.UNT64) return new NativeSlawUnt64(value);
        assert false : "Unknown ilk: " + i;
        return null;
    }

    static Slaw valueOf(NumericIlk i, double value) {
        if (i.isIntegral()) return valueOf(i, (long)value);
        if (i == NumericIlk.FLOAT32)
            return new NativeSlawFloat32((float)value);
        if (i == NumericIlk.FLOAT64) return new NativeSlawFloat64(value);
        assert false : "Unknown ilk: " + i;
        return null;
    }

    static Slaw valueOf(BigInteger value) {
        return new NativeSlawUnt64(value);
    }

    @Override public final SlawIlk ilk() { return SlawIlk.NUMBER; }

    @Override public double asDouble() { return (double)asLong(); }
    @Override public BigInteger asBigInteger() {
        return BigInteger.valueOf(asLong());
    }

    @Override public final Slaw withNumericIlk(NumericIlk i) {
        if (i == numericIlk()) return this;
        if (i == NumericIlk.UNT64) return valueOf(asBigInteger());
        if (i.isIntegral()) return valueOf(i, asLong());
        return valueOf(i, asDouble());
    }

    @Override public final int hashCode() { return (int)asLong(); }

    final boolean equals(Slaw s) {
        if (numericIlk().isIntegral()) return asLong() == s.asLong();
        return asDouble() == s.asDouble();
    }

    static final long normalize(long v, long mask) {
        return (v < 0 ? (-v) : v) & mask;
    }

    static final byte[] toBytes(NumericIlk ilk, long value) {
        final int bno = ilk.bytes();
        final byte[] res = new byte[bno];
        for (int i = 0; i < bno; i++) {
            res[LE ? i : bno - i - 1] = (byte) (value & 0xFF);
            value = value >>> 8;
        }
        return res;
    }

    static final boolean LE =
        ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN;
}

final class NativeSlawInt8 extends NativeSlawNumber {

    NativeSlawInt8(byte v) { value = v; }

    @Override public NumericIlk numericIlk() { return NumericIlk.INT8; }
    @Override public long asLong() { return value; }

    private byte value;
}

final class NativeSlawInt16 extends NativeSlawNumber {

    NativeSlawInt16(short v) { value = v; }

    @Override public NumericIlk numericIlk() { return NumericIlk.INT16; }
    @Override public long asLong() { return value; }

    private short value;
}

final class NativeSlawInt32 extends NativeSlawNumber {

    NativeSlawInt32(int v) { value = v; }

    @Override public NumericIlk numericIlk() { return NumericIlk.INT32; }
    @Override public long asLong() { return value; }

    static final NativeSlawInt32 ZERO = new NativeSlawInt32(0);

    private int value;
}

final class NativeSlawInt64 extends NativeSlawNumber {

    NativeSlawInt64(long v) { value = v; }

    @Override public NumericIlk numericIlk() { return NumericIlk.INT64; }
    @Override public long asLong() { return value; }

    private long value;
}

final class NativeSlawUnt8 extends NativeSlawNumber {

    NativeSlawUnt8(short v) { value = (short)normalize(v,0xFF); }

    @Override public NumericIlk numericIlk() { return NumericIlk.UNT8; }
    @Override public long asLong() { return value; }

    private short value;
}

final class NativeSlawUnt16 extends NativeSlawNumber {
    NativeSlawUnt16(int v) { value = (int)normalize(v, 0xFFFF); }

    @Override public NumericIlk numericIlk() { return NumericIlk.UNT16; }
    @Override public long asLong() { return value; }

    private int value;
}

final class NativeSlawUnt32 extends NativeSlawNumber {

    NativeSlawUnt32(long v) { value = normalize(v, 0xFFFFFFFF); }

    @Override public NumericIlk numericIlk() { return NumericIlk.UNT32; }
    @Override public long asLong() { return value; }

    private long value;
}

final class NativeSlawUnt64 extends NativeSlawNumber {

    NativeSlawUnt64(long v) { value = v; }

    NativeSlawUnt64(BigInteger v) { value = v.longValue(); }

    @Override public NumericIlk numericIlk() { return NumericIlk.UNT64; }
    @Override public long asLong() { return value; }

    @Override public BigInteger asBigInteger() {
        return new BigInteger(1, toBytes(numericIlk(), value));
    }

    private long value;
}

final class NativeSlawFloat32 extends NativeSlawNumber {
    NativeSlawFloat32(float v) {
        value = v;
    }

    @Override public NumericIlk numericIlk() { return NumericIlk.FLOAT32; }
    @Override public long asLong() { return (long)value; }
    @Override public double asDouble() { return value; }

    private float value;
}

final class NativeSlawFloat64 extends NativeSlawNumber {
    NativeSlawFloat64(double v) {
        value = v;
    }

    @Override public NumericIlk numericIlk() { return NumericIlk.FLOAT64; }
    @Override public long asLong() { return (long)value; }
    @Override public double asDouble() { return value; }

    private double value;
}
