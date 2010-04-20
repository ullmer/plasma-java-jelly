// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.math.BigInteger;

/**
 * Created: Sun Apr 18 19:12:34 2010
 *
 * @author jao
 */
abstract class NativeSlawNumber extends NativeAtomicSlaw
    implements SlawNumber {

    static SlawNumber valueOf(Ilk i, long value) {
        if (!i.isIntegral()) return valueOf(i, (double)value);
        if (i == Ilk.INT8) return new NativeSlawInt8((byte)value);
        if (i == Ilk.INT16) return new NativeSlawInt16((short)value);
        if (i == Ilk.INT32) return new NativeSlawInt32((int)value);
        if (i == Ilk.INT64) return new NativeSlawInt64(value);
        if (i == Ilk.UNT8) return new NativeSlawUnt8((short)value);
        if (i == Ilk.UNT16) return new NativeSlawUnt16((int)value);
        if (i == Ilk.UNT32) return new NativeSlawUnt32(value);
        if (i == Ilk.UNT64)
            return new NativeSlawUnt64(BigInteger.valueOf(value));
        assert false : "Unknown ilk: " + i;
        return null;
    }

    static SlawNumber valueOf(Ilk i, double value) {
        if (i.isIntegral()) return valueOf(i, (long)value);
        if (i == Ilk.FLOAT32) return new NativeSlawFloat32((float)value);
        if (i == Ilk.FLOAT64) return new NativeSlawFloat64(value);
        assert false : "Unknown ilk: " + i;
        return null;
    }

    static SlawNumber valueOf(BigInteger value) {
        return new NativeSlawUnt64(value);
    }

    // Implementation of com.oblong.jelly.SlawNumber

    @Override public byte byteValue() { return (byte)longValue(); }
    @Override public short shortValue() { return (short)longValue(); }
    @Override public int intValue() { return (int)longValue(); }
    @Override public float floatValue() { return (float)doubleValue(); }
    @Override public BigInteger bigIntegerValue() {
        return BigInteger.valueOf(longValue());
    }

    @Override public final SlawNumber withIlk(Ilk i) {
        if (i == Ilk.UNT64) return valueOf(bigIntegerValue());
        if (i.isIntegral()) return valueOf(i, longValue());
        return valueOf(i, doubleValue());
    }

    // Implementation of com.oblong.jelly.ExternalizableSlaw

    @Override public final byte[] externalize(SlawExternalizer e) {
        return e.externalize(this);
    }

    // Implementation of com.oblong.jelly.SlawComplex

    @Override public final SlawNumber re() { return this; }

    @Override public final SlawNumber im() {
        return ilk().isIntegral() ? valueOf(ilk(), 0L) : valueOf(ilk(), 0.0);
    }

    // Implementation of com.oblong.jelly.Slaw

    @Override public final boolean equals(Slaw slaw) {
        if (!(slaw instanceof SlawNumber)) return false;
        SlawNumber o = (SlawNumber) slaw;
        if (o.ilk() != ilk()) return false;
        return doubleValue() == o.doubleValue();
    }

    @Override public final int hashCode() { return 41 + intValue(); }

    @Override public final SlawNumber number() { return this; }

    @Override public final boolean isNumber() { return true; }
    @Override public final boolean isNumeric() { return true; }

    private Ilk ilk_;
}

final class NativeSlawInt8 extends NativeSlawNumber {
    NativeSlawInt8(byte v) {
        this.value = v;
    }

    @Override public Ilk ilk() { return Ilk.INT8; }
    @Override public byte byteValue() { return this.value; }
    @Override public long longValue() { return this.value; }
    @Override public double doubleValue() { return (double)this.value; }

    private byte value;
}

final class NativeSlawInt16 extends NativeSlawNumber {
    NativeSlawInt16(short v) {
        this.value = v;
    }

    @Override public Ilk ilk() { return Ilk.INT16; }
    @Override public short shortValue() { return this.value; }
    @Override public long longValue() { return this.value; }
    @Override public double doubleValue() { return (double)this.value; }

    private short value;
}

final class NativeSlawInt32 extends NativeSlawNumber {
    NativeSlawInt32(int v) {
        this.value = v;
    }

    @Override public Ilk ilk() { return Ilk.INT32; }
    @Override public int intValue() { return this.value; }
    @Override public long longValue() { return this.value; }
    @Override public double doubleValue() { return (double)this.value; }

    private int value;
}

final class NativeSlawInt64 extends NativeSlawNumber {
    NativeSlawInt64(long v) {
        this.value = v;
    }

    @Override public Ilk ilk() { return Ilk.INT64; }
    @Override public long longValue() { return this.value; }
    @Override public double doubleValue() { return (double)this.value; }

    private long value;
}

final class NativeSlawUnt8 extends NativeSlawNumber {
    NativeSlawUnt8(short v) {
        if (v < 0) v = (short)-v;
        v = (short)(v & 0x00FF);
        this.value = v;
    }

    @Override public Ilk ilk() { return Ilk.UNT8; }
    @Override public short shortValue() { return this.value; }
    @Override public long longValue() { return this.value; }
    @Override public double doubleValue() { return (double)this.value; }

    private short value;
}

final class NativeSlawUnt16 extends NativeSlawNumber {
    NativeSlawUnt16(int v) {
        if (v < 0) v = -v;
        v = v & 0x0000FFFF;
        this.value = v;
    }

    @Override public Ilk ilk() { return Ilk.UNT16; }
    @Override public int intValue() { return this.value; }
    @Override public long longValue() { return this.value; }
    @Override public double doubleValue() { return (double)this.value; }

    private int value;
}

final class NativeSlawUnt32 extends NativeSlawNumber {
    NativeSlawUnt32(long v) {
        if (v < 0) v = -v;
        v = v & 0xFFFFFFFF;
        this.value = v;
    }

    @Override public Ilk ilk() { return Ilk.UNT32; }
    @Override public long longValue() { return this.value; }
    @Override public double doubleValue() { return (double)this.value; }

    private long value;
}

final class NativeSlawUnt64 extends NativeSlawNumber {
    NativeSlawUnt64(BigInteger v) {
        if (v.signum() < 0) v = v.negate();
        v = MAX_VAL.min(v);
        this.value = v;
    }

    @Override public Ilk ilk() { return Ilk.UNT64; }
    @Override public long longValue() { return this.value.longValue(); }
    @Override public double doubleValue() {
        return this.value.doubleValue();
    }

    private static final BigInteger MAX_VAL =
        BigInteger.ONE.shiftLeft(64).subtract(BigInteger.ONE);

    private BigInteger value;
}

final class NativeSlawFloat32 extends NativeSlawNumber {
    NativeSlawFloat32(float v) {
        this.value = v;
    }

    @Override public Ilk ilk() { return Ilk.FLOAT32; }
    @Override public long longValue() { return (long)this.value; }
    @Override public float floatValue() { return this.value; }
    @Override public double doubleValue() { return this.value; }

    private float value;
}

final class NativeSlawFloat64 extends NativeSlawNumber {
    NativeSlawFloat64(double v) {
        this.value = v;
    }

    @Override public Ilk ilk() { return Ilk.FLOAT64; }
    @Override public long longValue() { return (long)this.value; }
    @Override public double doubleValue() { return this.value; }

    private double value;
}

