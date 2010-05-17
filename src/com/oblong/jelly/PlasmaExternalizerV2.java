package com.oblong.jelly;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.EnumMap;
import java.util.Map;

import static com.oblong.jelly.NumericIlk.*;
import static com.oblong.jelly.SlawIlk.*;
import static com.oblong.jelly.PlasmaProtocolV2.*;

final class PlasmaExternalizerV2 extends SlawExternalizer {

    @Override int nilExternSize(Slaw s) { return 8; }

    @Override void externNil(Slaw s, ByteBuffer b) {
        b.putLong(nilHeading());
    }

    @Override int boolExternSize(Slaw b) { return 8; }

    @Override void externBool(Slaw b, ByteBuffer r) {
        r.putLong(boolHeading(b.emitBoolean()));
    }

    @Override int stringExternSize(Slaw s) {
        final int bn = stringBytes(s).length;
        return (bn > 6) ? roundUp(bn + 8 + 1) : 8;
    }

    @Override void externString(Slaw s, ByteBuffer b) {
        final byte[] bs = stringBytes(s);
        if (bs.length > 6) marshallStr(bs, b); else marshallWeeStr(bs, b);
    }

    @Override int numberExternSize(Slaw n) {
        return numericSize(n, false);
    }

    @Override void externNumber(Slaw n, ByteBuffer b) {
        final NumericIlk i = n.numericIlk();
        b.putLong(numberHeading(i));
        adjustBufferForNumeric(b, n, false);
        putNumVal(b, n);
    }

    @Override int complexExternSize(Slaw c) {
        return numericSize(c, true);
    }

    @Override void externComplex(Slaw c, ByteBuffer b) {
        b.putLong(complexHeading(c.numericIlk()));
        adjustBufferForNumeric(b, c, true);
        putNumVal(b, c);
    }

    @Override int vectorExternSize(Slaw v) {
        return numericSize(v, false);
    }

    @Override void externVector(Slaw v, ByteBuffer b) {
        b.putLong(vectorHeading(v.numericIlk(), v.dimension()));
        adjustBufferForNumeric(b, v, false);
        for (Slaw n : v.emitList()) putNumVal(b, n);
    }

    @Override int complexVectorExternSize(Slaw v) {
        return numericSize(v, true);
    }

    @Override void externComplexVector(Slaw v, ByteBuffer b) {
        b.putLong(complexVectorHeading(v.numericIlk(), v.dimension()));
        adjustBufferForNumeric(b, v, false);
        for (Slaw n : v.emitList()) putNumVal(putNumVal(b, n.car()), n.cdr());
    }

    @Override int multivectorExternSize(Slaw v) {
        return numericSize(v, false);
    }

    @Override void externMultivector(Slaw v, ByteBuffer b) {
        b.putLong(multivectorHeading(v.numericIlk(), v.dimension()));
        adjustBufferForNumeric(b, v, false);
        for (Slaw n : v.emitList()) putNumVal(b, n);
    }

    @Override int arrayExternSize(Slaw a) {
        return arraySize(a);
    }

    @Override void externArray(Slaw a, ByteBuffer b) {
        putArray(arrayHeading(a.numericIlk()), b, a);
    }

    @Override int complexArrayExternSize(Slaw a) {
        return complexArraySize(a);
    }

    @Override void externComplexArray(Slaw a, ByteBuffer b) {
        putArray(complexArrayHeading(a.numericIlk()), b, a);
    }

    @Override int vectorArrayExternSize(Slaw a) {
        return arraySize(a);
    }

    @Override void externVectorArray(Slaw a, ByteBuffer b) {
        putArray(vectorArrayHeading(a.numericIlk(), a.dimension()), b, a);
    }

    @Override int complexVectorArrayExternSize(Slaw a) {
        return complexArraySize(a);
    }

    @Override void externComplexVectorArray(Slaw a, ByteBuffer b) {
        putArray(complexVectorArrayHeading(a.numericIlk(), a.dimension()),
                 b, a);
    }

    @Override int multivectorArrayExternSize(Slaw a) {
        final int cno = 1 << a.dimension();
        return 8 + roundUp(a.count() * cno * a.numericIlk().bytes());
    }

    @Override void externMultivectorArray(Slaw a, ByteBuffer b) {
        putArray(multivectorArrayHeading(a.numericIlk(), a.dimension()),
                 b, a);
    }

    @Override int consExternSize(Slaw c) {
        return listSize(c);
    }

    @Override void externCons(Slaw c, ByteBuffer b) {
        marshallAsList(c, b);
    }

    @Override int listExternSize(Slaw c) {
        return listSize(c);
    }

    @Override void externList(Slaw c, ByteBuffer b) {
        marshallAsList(c, b);
    }

    @Override int mapExternSize(Slaw c) {
        return listSize(c);
    }

    @Override void externMap(Slaw c, ByteBuffer b) {
        marshallAsList(c, b);
    }

    @Override void prepareBuffer(ByteBuffer b, Slaw s) {}

    @Override void finishBuffer(ByteBuffer b, Slaw s, int begin) {
        int len = b.capacity() - b.position();
        while (len-- > 0) b.put((byte)0);
    }

    private static byte[] stringBytes(Slaw s) {
        try {
            return s.emitString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("externalize: UTF-8 not supported", e);
        }
    }

    private static ByteBuffer putNumVal(ByteBuffer buffer, Slaw n) {
        assert n.isNumber() || n.isComplex();
        if (n.isComplex()) {
            putNumVal(buffer, n.car());
            putNumVal(buffer, n.cdr());
        } else {
            NumericIlk i = n.numericIlk();
            if (i == FLOAT32) {
                buffer.putFloat((float)n.emitDouble());
            } else if (i == FLOAT64) {
                buffer.putDouble(n.emitDouble());
            } else if (i == INT8 || i == UNT8) {
                buffer.put((byte)n.emitLong());
            } else if (i == INT16 || i == UNT16) {
                buffer.putShort((short)n.emitLong());
            } else if (i == INT32 || i == UNT32) {
                buffer.putInt((int)n.emitLong());
            } else if (i == INT64 || i == UNT64) {
                buffer.putLong(n.emitLong());
            }
        }
        return buffer;
    }

    private static void marshallWeeStr(byte[] bs, ByteBuffer b) {
        assert bs.length < 7;
        final int fb = weeStringHeadingByte() | (bs.length + 1);
        b.put((byte)fb);
        for (int i = bs.length; i < 6; ++i) b.put(NUL);
        b.put(bs);
    }

    private static void marshallStr(byte[] bs, ByteBuffer b) {
        final int len = 8 + bs.length + 1;
        final int p = roundUp(len) - len;
        b.putLong(octs(len + p)).put(0, (byte)(stringHeadingByte()|p))
         .put(bs);
    }

    private static void adjustBufferForNumeric(ByteBuffer b,
                                               Slaw s,
                                               boolean complex) {
        int w = s.numericIlk().bytes() * s.count();
        if (complex) w = w << 1;
        if (w < 5) b.position(b.position() - w);
    }

    private static int numericSize(Slaw s, boolean complex) {
        return roundUp(
            5 + (complex ? 2 : 1) * s.count() * s.numericIlk().bytes());
    }

    private static int arraySize(Slaw a) {
        return
            8 + roundUp(a.count() * a.dimension() * a.numericIlk().bytes());
    }

    private static int complexArraySize(Slaw a) {
        return 8
            + roundUp(2 * a.count() * a.dimension() * a.numericIlk().bytes());
    }

    private static void putArray(long h, ByteBuffer b, Slaw a) {
        final int c = a.count();
        b.putLong(h|c);
        for (int i = 0; i < c; i++) {
            final Slaw n = a.nth(i);
            for (int j = 0, d = n.count(); j < d; j++) putNumVal(b, n.nth(j));
        }
    }

    private int listSize(Slaw l) {
        final int count = l.count();
        int len = 8;
        if (count > 14) len += 8;
        for (int i = 0; i < count; i++) len += externSize(l.nth(i));
        return roundUp(len);
    }

    private void marshallAsList(Slaw l, ByteBuffer b) {
        final int begin = b.position();
        final int count = l.count();
        b.position(b.position() + 8);
        if (count > 14) b.putLong(count);
        for (int i = 0; i < count; i++) extern(l.nth(i), b);
        final long h =
            (long)(compositeHeadingByte(l.ilk()))|Math.min(15, count);
        final long octs = octs(b.position() - begin);
        b.mark().position(begin);
        b.putLong((h<<56)|octs).reset();
    }

    private static final byte NUL = 0;
}
