package com.oblong.jelly.slaw.v2;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.oblong.jelly.NumericIlk;
import static com.oblong.jelly.NumericIlk.*;
import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIlk;
import static com.oblong.jelly.SlawIlk.*;

import com.oblong.jelly.slaw.AbstractSlawExternalizer;
import static com.oblong.jelly.slaw.v2.Protocol.*;

import net.jcip.annotations.Immutable;

@Immutable
public final class Externalizer extends AbstractSlawExternalizer {

    @Override protected int nilExternSize(Slaw s) { return OCT_LEN; }

    @Override protected void externNil(Slaw s, ByteBuffer b) {
        b.putLong(NIL_HEADING);
    }

    @Override protected int boolExternSize(Slaw b) { return OCT_LEN; }

    @Override protected void externBool(Slaw b, ByteBuffer r) {
        r.putLong(b.emitBoolean() ? TRUE_HEADING : FALSE_HEADING);
    }

    @Override protected int stringExternSize(Slaw s) {
        final int bn = stringBytes(s).length;
        return (bn > STR_WEE_LEN) ? roundUp(bn + OCT_LEN + 1) : OCT_LEN;
    }

    @Override protected void externString(Slaw s, ByteBuffer b) {
        final byte[] bs = stringBytes(s);
        if (bs.length > STR_WEE_LEN) marshallStr(bs, b);
        else marshallWeeStr(bs, b);
    }

    @Override protected int numberExternSize(Slaw n) {
        return numericSize(n);
    }

    @Override protected void externNumber(Slaw n, ByteBuffer b) {
        b.putLong(numberHeading(n.numericIlk()));
        adjustBufferForNumeric(b, n);
        putNumVal(b, n);
    }

    @Override protected int complexExternSize(Slaw c) {
        return numericSize(c);
    }

    @Override protected void externComplex(Slaw c, ByteBuffer b) {
        b.putLong(complexHeading(c.numericIlk()));
        adjustBufferForNumeric(b, c);
        putNumVal(b, c);
    }

    @Override protected int vectorExternSize(Slaw v) {
        return numericSize(v);
    }

    @Override protected void externVector(Slaw v, ByteBuffer b) {
        b.putLong(vectorHeading(v.numericIlk(), v.dimension()));
        adjustBufferForNumeric(b, v);
        for (Slaw n : v.emitList()) putNumVal(b, n);
    }

    @Override protected int complexVectorExternSize(Slaw v) {
        return numericSize(v);
    }

    @Override protected void externComplexVector(Slaw v, ByteBuffer b) {
        b.putLong(complexVectorHeading(v.numericIlk(), v.dimension()));
        adjustBufferForNumeric(b, v);
        for (Slaw n : v.emitList()) putNumVal(putNumVal(b, n.car()), n.cdr());
    }

    @Override protected int multivectorExternSize(Slaw v) {
        return numericSize(v);
    }

    @Override protected void externMultivector(Slaw v, ByteBuffer b) {
        b.putLong(multivectorHeading(v.numericIlk(), v.dimension()));
        adjustBufferForNumeric(b, v);
        for (Slaw n : v.emitList()) putNumVal(b, n);
    }

    @Override protected int arrayExternSize(Slaw a) { return arraySize(a); }

    @Override protected void externArray(Slaw a, ByteBuffer b) {
        putArray(arrayHeading(a.numericIlk()), b, a);
    }

    @Override protected int complexArrayExternSize(Slaw a) {
        return complexArraySize(a);
    }

    @Override protected void externComplexArray(Slaw a, ByteBuffer b) {
        putArray(complexArrayHeading(a.numericIlk()), b, a);
    }

    @Override protected int vectorArrayExternSize(Slaw a) {
        return arraySize(a);
    }

    @Override protected void externVectorArray(Slaw a, ByteBuffer b) {
        putArray(vectorArrayHeading(a.numericIlk(), a.dimension()), b, a);
    }

    @Override protected int complexVectorArrayExternSize(Slaw a) {
        return complexArraySize(a);
    }

    @Override protected void externComplexVectorArray(Slaw a, ByteBuffer b) {
        putArray(complexVectorArrayHeading(a.numericIlk(), a.dimension()),
                 b, a);
    }

    @Override protected int multivectorArrayExternSize(Slaw a) {
        final int cno = 1 << a.dimension();
        return OCT_LEN + roundUp(a.count() * cno * a.numericIlk().bytes());
    }

    @Override protected void externMultivectorArray(Slaw a, ByteBuffer b) {
        putArray(multivectorArrayHeading(a.numericIlk(), a.dimension()),
                 b, a);
    }

    @Override protected int consExternSize(Slaw c) { return listSize(c); }

    @Override protected void externCons(Slaw c, ByteBuffer b) {
        marshallAsList(c, b);
    }

    @Override protected int listExternSize(Slaw c) { return listSize(c); }

    @Override protected void externList(Slaw c, ByteBuffer b) {
        marshallAsList(c, b);
    }

    @Override protected int mapExternSize(Slaw c) { return listSize(c); }

    @Override protected void externMap(Slaw c, ByteBuffer b) {
        marshallAsList(c, b);
    }

    @Override protected void externProtein(Protein p, ByteBuffer b) {
        final int begin = b.position();
        b.putLong(0).putLong(0);
        final Slaw descrips = p.descrips();
        final Slaw ingests = p.ingests();
        if (descrips != null) extern(descrips, b);
        if (ingests != null) extern(ingests, b);
        final int dataLen = p.dataLength();
        if (dataLen > WEE_PROTEIN_DATA_LEN) p.putData(b);
        final int end = b.position();
        final byte sb = proteinSecondHeadingByte(descrips != null,
                                                 ingests != null, dataLen);
        b.position(begin);
        b.putLong(proteinHeading(octs(roundUp(end - begin))));
        if (dataLen > 0 && dataLen <= WEE_PROTEIN_DATA_LEN)
            p.putData(pad(b.put(sb), WEE_PROTEIN_DATA_LEN - dataLen));
        else
            b.putLong(proteinSecondHeading(sb, dataLen));
        b.position(end);
    }

    @Override protected int proteinExternSize(Protein p) {
        int len = 2 * OCT_LEN;
        final Slaw ingests = p.ingests();
        if (ingests != null) len += externSize(ingests);
        final Slaw descrips = p.descrips();
        if (descrips != null) len += externSize(descrips);
        if (p.dataLength() > WEE_PROTEIN_DATA_LEN) len += p.dataLength();
        return roundUp(len);
    }

    @Override protected void prepareBuffer(ByteBuffer b, Slaw s) {}

    @Override protected void finishBuffer(ByteBuffer b, Slaw s, int begin) {
        pad(b, roundUp(b.position()) - b.position());
    }

    private static ByteBuffer pad(ByteBuffer b, int n) {
        while (n-- > 0) b.put(NUL);
        return b;
    }

    private static byte[] stringBytes(Slaw s) {
        try {
            return s.emitString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 not supported", e);
        }
    }

    private static ByteBuffer putNumVal(ByteBuffer buffer, Slaw n) {
        assert n.isNumber() || n.isComplex();
        if (n.isComplex()) {
            putScalar(buffer, n.car());
            putScalar(buffer, n.cdr());
        } else {
            putScalar(buffer, n);
        }
        return buffer;
    }

    private static void putScalar(ByteBuffer buffer, Slaw n) {
        switch (n.numericIlk()) {
        case FLOAT32: buffer.putFloat((float)n.emitDouble()); break;
        case FLOAT64: buffer.putDouble(n.emitDouble()); break;
        case INT8: case UNT8: buffer.put((byte)n.emitLong()); break;
        case INT16: case UNT16: buffer.putShort((short)n.emitLong()); break;
        case INT32: case UNT32: buffer.putInt((int)n.emitLong()); break;
        case INT64: case UNT64: buffer.putLong(n.emitLong()); break;
        }
    }

    private static void marshallWeeStr(byte[] bs, ByteBuffer b) {
        assert bs.length <= STR_WEE_LEN;
        final int fb = WEE_STR_HEADING_BYTE | (bs.length + 1);
        b.put((byte)fb);
        pad(b, STR_WEE_LEN - bs.length).put(bs);
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
        if (count > WEE_LIST_LEN) len += OCT_LEN;
        for (int i = 0; i < count; i++) len += externSize(l.nth(i));
        return roundUp(len);
    }

    private void marshallAsList(Slaw l, ByteBuffer b) {
        final int begin = b.position();
        final int count = l.count();
        b.position(b.position() + OCT_LEN);
        if (count > WEE_LIST_LEN) b.putLong(count);
        for (int i = 0; i < count; i++) extern(l.nth(i), b);
        final long h =
            (long)(compositeHeadingByte(l.ilk()))|Math.min(15, count);
        final int end = b.position();
        final long octs = octs(roundUp(end - begin));
        b.position(begin);
        b.putLong((h<<56)|octs).position(end);
    }
}
