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
        return roundUp(5 + n.numericIlk().bytes());
    }

    @Override void externNumber(Slaw n, ByteBuffer b) {
        final NumericIlk i = n.numericIlk();
        if (i.bytes() < 5)
            b.putLong(numberHeading(i)
                      | (n.emitLong() & ((1L<<i.width()) - 1L)));
        else
            putNumVal(b.putLong(numberHeading(i)), n);
    }

    @Override int complexExternSize(Slaw c) {
        return 8 + 2 * c.numericIlk().bytes();
    }

    @Override void externComplex(Slaw c, ByteBuffer b) {
        b.putLong(complexHeading(c.numericIlk()));
        putNumVal(putNumVal(b, c.car()), c.cdr());
    }

    @Override int vectorExternSize(Slaw v) {
        return roundUp(8 + v.count() * v.numericIlk().bytes());
    }

    @Override void externVector(Slaw v, ByteBuffer b) {
        b.putLong(vectorHeading(v.numericIlk(), v.count()));
        for (Slaw n : v.emitList()) putNumVal(b, n);
    }

    @Override int complexVectorExternSize(Slaw v) {
        return roundUp(8 + 2 * v.count() * v.numericIlk().bytes());
    }

    @Override void externComplexVector(Slaw v, ByteBuffer b) {
        // TODO: wee complexes
        b.putLong(complexVectorHeading(v.numericIlk(), v.count()));
        for (Slaw n : v.emitList()) putNumVal(putNumVal(b, n.car()), n.cdr());
    }

    @Override int multivectorExternSize(Slaw v) {
        return roundUp(8 + v.count() * v.numericIlk().bytes());
    }

    @Override void externMultivector(Slaw v, ByteBuffer b) {
        b.putLong(multivectorHeading(v.numericIlk(), v.count()));
        for (Slaw n : v.emitList()) putNumVal(b, n);
    }

    @Override void externArray(Slaw a, ByteBuffer b) {}
    @Override int arrayExternSize(Slaw a) { return -1; }

    @Override void externComplexArray(Slaw a, ByteBuffer b) {}
    @Override int complexArrayExternSize(Slaw a) { return -1; }

    @Override void externVectorArray(Slaw a, ByteBuffer b) {}
    @Override int vectorArrayExternSize(Slaw a) { return -1; }

    @Override void externComplexVectorArray(Slaw a, ByteBuffer b) {}
    @Override int complexVectorArrayExternSize(Slaw a) { return -1; }

    @Override void externMultivectorArray(Slaw v, ByteBuffer b) {}
    @Override int multivectorArrayExternSize(Slaw v) { return -1; }

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
        Integer tb = COMPOSITE_TBS.get(s.ilk());
        if (tb != null) {
            b.mark().position(begin);
            b.putLong(octs(len)).put((byte)(tb | (s.count() & 0x0F))).reset();
        }
    }

    private static byte[] stringBytes(Slaw s) {
        try {
            return s.emitString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("externalize: UTF-8 not supported", e);
        }
    }

    private static ByteBuffer putNumVal(ByteBuffer buffer, Slaw n) {
        assert n.is(SlawIlk.NUMBER);
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

    private int listSize(Slaw l) {
        int len = 8;
        for (Slaw s : l.emitList()) len = len + externSize(s);
        return roundUp(len);
    }

    private void marshallAsList(Slaw l, ByteBuffer b) {
        b.position(b.position() + 8);
        for (Slaw s : l.emitList()) extern(s, b);
    }

    private static final byte NUL = 0;

    private static final Map<SlawIlk, Integer> COMPOSITE_TBS;
    static {
        COMPOSITE_TBS = new EnumMap<SlawIlk, Integer>(SlawIlk.class);
        COMPOSITE_TBS.put(CONS, 0x60);
        COMPOSITE_TBS.put(LIST, 0x40);
        COMPOSITE_TBS.put(MAP, 0x50);
    }
}
