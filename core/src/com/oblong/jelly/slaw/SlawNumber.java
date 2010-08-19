// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw;

import java.math.BigInteger;

import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIlk;


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
    @Override public float emitFloat() { return (float)emitDouble(); }
    @Override public int emitInt() { return (int)emitLong(); }
    @Override public short emitShort() { return (short)emitLong(); }
    @Override public byte emitByte() { return (byte)emitLong(); }

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

    @Override public String debugString() { return "" + emitLong(); }

    @Override final public boolean slawEquals(Slaw s) {
        if (numericIlk().isIntegral()) return emitLong() == s.emitLong();
        return emitDouble() == s.emitDouble();
    }

    static final long normalize(long v, long mask) {
        return (v < 0 ? (-v) : v) & mask;
    }

    static final byte[] toBytes(NumericIlk ilk, long value) {
        final int bno = ilk.bytes();
        final byte[] res = new byte[bno];
        for (int i = bno - 1; i >= 0; i--) {
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
