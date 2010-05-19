package com.oblong.jelly;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import static com.oblong.jelly.NumericIlk.*;
import static com.oblong.jelly.SlawIlk.*;
import static com.oblong.jelly.PlasmaProtocolV2.*;

final class PlasmaExternalizerV2 extends SlawExternalizer {

    @Override int nilExternSize(Slaw s) { return OCT_LEN; }

    @Override void externNil(Slaw s, ByteBuffer b) { b.putLong(NIL_HEADING); }

    @Override int boolExternSize(Slaw b) { return OCT_LEN; }

    @Override void externBool(Slaw b, ByteBuffer r) {
        r.putLong(b.emitBoolean() ? TRUE_HEADING : FALSE_HEADING);
    }

    @Override int stringExternSize(Slaw s) {
        final int bn = stringBytes(s).length;
        return (bn > STR_WEE_LEN) ? roundUp(bn + OCT_LEN + 1) : OCT_LEN;
    }

    @Override void externString(Slaw s, ByteBuffer b) {
        final byte[] bs = stringBytes(s);
        if (bs.length > STR_WEE_LEN) marshallStr(bs, b);
        else marshallWeeStr(bs, b);
    }

    @Override int numberExternSize(Slaw n) { return numericSize(n); }

    @Override void externNumber(Slaw n, ByteBuffer b) {
        b.putLong(numberHeading(n.numericIlk()));
        adjustBufferForNumeric(b, n);
        putNumVal(b, n);
    }

    @Override int complexExternSize(Slaw c) { return numericSize(c); }

    @Override void externComplex(Slaw c, ByteBuffer b) {
        b.putLong(complexHeading(c.numericIlk()));
        adjustBufferForNumeric(b, c);
        putNumVal(b, c);
    }

    @Override int vectorExternSize(Slaw v) { return numericSize(v); }

    @Override void externVector(Slaw v, ByteBuffer b) {
        b.putLong(vectorHeading(v.numericIlk(), v.dimension()));
        adjustBufferForNumeric(b, v);
        for (Slaw n : v.emitList()) putNumVal(b, n);
    }

    @Override int complexVectorExternSize(Slaw v) { return numericSize(v); }

    @Override void externComplexVector(Slaw v, ByteBuffer b) {
        b.putLong(complexVectorHeading(v.numericIlk(), v.dimension()));
        adjustBufferForNumeric(b, v);
        for (Slaw n : v.emitList()) putNumVal(putNumVal(b, n.car()), n.cdr());
    }

    @Override int multivectorExternSize(Slaw v) { return numericSize(v); }

    @Override void externMultivector(Slaw v, ByteBuffer b) {
        b.putLong(multivectorHeading(v.numericIlk(), v.dimension()));
        adjustBufferForNumeric(b, v);
        for (Slaw n : v.emitList()) putNumVal(b, n);
    }

    @Override int arrayExternSize(Slaw a) { return arraySize(a); }

    @Override void externArray(Slaw a, ByteBuffer b) {
        putArray(arrayHeading(a.numericIlk()), b, a);
    }

    @Override int complexArrayExternSize(Slaw a) {
        return complexArraySize(a);
    }

    @Override void externComplexArray(Slaw a, ByteBuffer b) {
        putArray(complexArrayHeading(a.numericIlk()), b, a);
    }

    @Override int vectorArrayExternSize(Slaw a) { return arraySize(a); }

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
        return OCT_LEN + roundUp(a.count() * cno * a.numericIlk().bytes());
    }

    @Override void externMultivectorArray(Slaw a, ByteBuffer b) {
        putArray(multivectorArrayHeading(a.numericIlk(), a.dimension()),
                 b, a);
    }

    @Override int consExternSize(Slaw c) { return listSize(c); }

    @Override void externCons(Slaw c, ByteBuffer b) { marshallAsList(c, b); }

    @Override int listExternSize(Slaw c) { return listSize(c); }

    @Override void externList(Slaw c, ByteBuffer b) { marshallAsList(c, b); }

    @Override int mapExternSize(Slaw c) { return listSize(c); }

    @Override void externMap(Slaw c, ByteBuffer b) { marshallAsList(c, b); }

    @Override void externProtein(Protein p, ByteBuffer b) {
        final int begin = b.position();
        b.putLong(0).putLong(0);
        final Slaw descrips = p.descrips();
        final Slaw ingests = p.ingests();
        final byte[] data = p.data();
        if (descrips != null) extern(descrips, b);
        if (ingests != null) extern(ingests, b);
        final int dataLen = (data == null) ? 0 : data.length;
        if (data != null && dataLen < OCT_LEN) b.put(data);
        final int octs = octs(roundUp(begin - b.position()));
        b.mark().position(begin);
        b.putLong(PROTEIN_HEADING_BYTE << 56 | octs);
        b.put(proteinSecondHeadingByte(descrips != null,
                                       ingests != null,
                                       dataLen));
        if (dataLen < OCT_LEN) {
            for (int i = 0, l = OCT_LEN - 1 - dataLen; i < l; i++) b.put(NUL);
            b.put(data);
        }
        b.reset();
    }

    @Override int proteinExternSize(Protein p) {
        int len = 2 * OCT_LEN;
        final Slaw ingests = p.ingests();
        if (ingests != null) len += externSize(ingests);
        final Slaw descrips = p.descrips();
        if (descrips != null) len += externSize(descrips);
        final byte[] data = p.data();
        if (data != null && data.length < OCT_LEN) len += data.length;
        return roundUp(len);
    }

    @Override void prepareBuffer(ByteBuffer b, Slaw s) {}

    @Override void finishBuffer(ByteBuffer b, Slaw s, int begin) {
        int pad = roundUp(b.position()) - b.position();
        while (pad-- > 0) b.put(NUL);
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
        assert bs.length <= STR_WEE_LEN;
        final int fb = WEE_STR_HEADING_BYTE | (bs.length + 1);
        b.put((byte)fb);
        for (int i = bs.length; i < STR_WEE_LEN; ++i) b.put(NUL);
        b.put(bs);
    }

    private static void marshallStr(byte[] bs, ByteBuffer b) {
        final int len = OCT_LEN + bs.length + 1;
        final int p = roundUp(len) - len;
        b.putLong(octs(len + p)).put(0, (byte)(STR_HEADING_BYTE|p))
         .put(bs);
    }

    private static int numericWidth(Slaw s) {
        final int nb = s.numericIlk().bytes();
        final int w = nb * (s.isMultivector() ? s.count() : s.dimension());
        return s.ilk().isComplexNumeric() ? 2 * w : w;
    }

    private static void adjustBufferForNumeric(ByteBuffer b, Slaw s) {
        final int w = numericWidth(s);
        if (w <= NUM_WEE_LEN) b.position(b.position() - w);
    }

    private static int numericSize(Slaw s) {
        final int w = numericWidth(s);
        return w > NUM_WEE_LEN ? OCT_LEN + roundUp(w) : OCT_LEN;
    }

    private static int arraySize(Slaw a) {
        return OCT_LEN
            + roundUp(a.count() * a.dimension() * a.numericIlk().bytes());
    }

    private static int complexArraySize(Slaw a) {
        return OCT_LEN
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
        int len = OCT_LEN;
        if (count > WEE_PROTEIN_DATA_LEN) len += OCT_LEN;
        for (int i = 0; i < count; i++) len += externSize(l.nth(i));
        return roundUp(len);
    }

    private void marshallAsList(Slaw l, ByteBuffer b) {
        final int begin = b.position();
        final int count = l.count();
        b.position(b.position() + OCT_LEN);
        if (count > WEE_PROTEIN_DATA_LEN) b.putLong(count);
        for (int i = 0; i < count; i++) extern(l.nth(i), b);
        final long h =
            (long)(compositeHeadingByte(l.ilk()))|Math.min(15, count);
        final long octs = octs(roundUp(b.position() - begin));
        b.mark().position(begin);
        b.putLong((h<<56)|octs).reset();
    }
}
