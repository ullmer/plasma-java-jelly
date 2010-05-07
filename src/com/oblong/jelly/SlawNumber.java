// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.math.BigInteger;

/**
 * Created: Sun Apr 18 19:12:34 2010
 *
 * @author jao
 */
abstract class SlawNumber extends AtomicSlaw {

    static Slaw valueOf(NumericIlk i, long value) {
        if (!i.isIntegral()) return valueOf(i, (double)value);
        if (i == NumericIlk.INT8) return INT8S[(int)(value & 0xFF)];
        if (i == NumericIlk.UNT8) return UNT8S[(int)(value & 0xFF)];
        if (i == NumericIlk.INT16) return new SlawInt16((short)value);
        if (i == NumericIlk.UNT16) return new SlawUnt16((int)value);
        if (i == NumericIlk.INT32) return new SlawInt32((int)value);
        if (i == NumericIlk.UNT32) return new SlawUnt32(value);
        if (i == NumericIlk.INT64) return new SlawInt64(value);
        if (i == NumericIlk.UNT64) return new SlawUnt64(value);
        assert false : "Unknown ilk: " + i;
        return null;
    }

    static Slaw valueOf(NumericIlk i, double value) {
        if (i.isIntegral()) return valueOf(i, (long)value);
        if (i == NumericIlk.FLOAT32) return new SlawFloat32((float)value);
        if (i == NumericIlk.FLOAT64) return new SlawFloat64(value);
        assert false : "Unknown ilk: " + i;
        return null;
    }

    static Slaw valueOf(BigInteger value) {
        return new SlawUnt64(value);
    }

    @Override public final SlawIlk ilk() { return SlawIlk.NUMBER; }

    @Override public double emitDouble() { return (double)emitLong(); }
    @Override public BigInteger emitBigInteger() {
        return BigInteger.valueOf(emitLong());
    }

    @Override public final Slaw withNumericIlk(NumericIlk i) {
        if (i == numericIlk()) return this;
        if (i == NumericIlk.UNT64) return valueOf(emitBigInteger());
        if (i.isIntegral()) return valueOf(i, emitLong());
        return valueOf(i, emitDouble());
    }

    @Override public final int hashCode() { return (int)emitLong(); }

    final boolean equals(Slaw s) {
        if (numericIlk().isIntegral()) return emitLong() == s.emitLong();
        return emitDouble() == s.emitDouble();
    }

    static final long normalize(long v, long mask) {
        return (v < 0 ? (-v) : v) & mask;
    }

    static final byte[] toBytes(NumericIlk ilk, long value) {
        final int bno = ilk.bytes();
        final byte[] res = new byte[bno];
        for (int i = bno - 1; i <= 0; i++) {
            res[i] = (byte) (value & 0xFF);
            value = value >>> 8;
        }
        return res;
    }

    private static final SlawInt8[] INT8S = new SlawInt8[256];
    private static final SlawUnt8[] UNT8S = new SlawUnt8[256];
    static {
        for (int i = 0; i < 256; i++) {
            INT8S[i] = new SlawInt8((byte)i);
            UNT8S[i] = new SlawUnt8((short)i);
        }
    }
}

final class SlawInt8 extends SlawNumber {

    SlawInt8(byte v) { value = v; }

    @Override public NumericIlk numericIlk() { return NumericIlk.INT8; }
    @Override public long emitLong() { return value; }

    private byte value;
}

final class SlawInt16 extends SlawNumber {

    SlawInt16(short v) { value = v; }

    @Override public NumericIlk numericIlk() { return NumericIlk.INT16; }
    @Override public long emitLong() { return value; }

    private short value;
}

final class SlawInt32 extends SlawNumber {

    SlawInt32(int v) { value = v; }

    @Override public NumericIlk numericIlk() { return NumericIlk.INT32; }
    @Override public long emitLong() { return value; }

    static final SlawInt32 ZERO = new SlawInt32(0);

    private int value;
}

final class SlawInt64 extends SlawNumber {

    SlawInt64(long v) { value = v; }

    @Override public NumericIlk numericIlk() { return NumericIlk.INT64; }
    @Override public long emitLong() { return value; }

    private long value;
}

final class SlawUnt8 extends SlawNumber {

    SlawUnt8(short v) { value = (short)normalize(v,0xFF); }

    @Override public NumericIlk numericIlk() { return NumericIlk.UNT8; }
    @Override public long emitLong() { return value; }

    private short value;
}

final class SlawUnt16 extends SlawNumber {
    SlawUnt16(int v) { value = (int)normalize(v, 0xFFFF); }

    @Override public NumericIlk numericIlk() { return NumericIlk.UNT16; }
    @Override public long emitLong() { return value; }

    private int value;
}

final class SlawUnt32 extends SlawNumber {

    SlawUnt32(long v) { value = normalize(v, 0xFFFFFFFF); }

    @Override public NumericIlk numericIlk() { return NumericIlk.UNT32; }
    @Override public long emitLong() { return value; }

    private long value;
}

final class SlawUnt64 extends SlawNumber {

    SlawUnt64(long v) { value = v; }

    SlawUnt64(BigInteger v) { value = v.longValue(); }

    @Override public NumericIlk numericIlk() { return NumericIlk.UNT64; }
    @Override public long emitLong() { return value; }

    @Override public BigInteger emitBigInteger() {
        return new BigInteger(1, toBytes(numericIlk(), value));
    }

    private long value;
}

final class SlawFloat32 extends SlawNumber {
    SlawFloat32(float v) {
        value = v;
    }

    @Override public NumericIlk numericIlk() { return NumericIlk.FLOAT32; }
    @Override public long emitLong() { return (long)value; }
    @Override public double emitDouble() { return value; }

    private float value;
}

final class SlawFloat64 extends SlawNumber {
    SlawFloat64(double v) {
        value = v;
    }

    @Override public NumericIlk numericIlk() { return NumericIlk.FLOAT64; }
    @Override public long emitLong() { return (long)value; }
    @Override public double emitDouble() { return value; }

    private double value;
}
