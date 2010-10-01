package com.oblong.jelly.slaw.io;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.slaw.AbstractSlawExternalizer;

import static com.oblong.jelly.slaw.io.BinaryProtocol.*;

import com.oblong.jelly.util.ByteWriter;

@Immutable
public final class BinaryExternalizer extends AbstractSlawExternalizer {

    @Override protected int nilExternSize(Slaw s) { return OCT_LEN; }

    @Override protected void externNil(Slaw s, ByteWriter b)
        throws IOException {
        b.putLong(NIL_HEADING);
    }

    @Override protected int boolExternSize(Slaw b) { return OCT_LEN; }

    @Override protected void externBool(Slaw b, ByteWriter r)
        throws IOException {
        r.putLong(b.emitBoolean() ? TRUE_HEADING : FALSE_HEADING);
    }

    @Override protected int stringExternSize(Slaw s) {
        final int bn = stringBytes(s).length;
        return (bn > STR_WEE_LEN) ? roundUp(bn + OCT_LEN + 1) : OCT_LEN;
    }

    @Override protected void externString(Slaw s, ByteWriter b)
        throws IOException {
        final byte[] bs = stringBytes(s);
        if (bs.length > STR_WEE_LEN) marshallStr(bs, b);
        else marshallWeeStr(bs, b);
    }

    @Override protected int numberExternSize(Slaw n) {
        return numericSize(n);
    }

    @Override protected void externNumber(Slaw n, ByteWriter b)
        throws IOException {
        putNumVal(putNumHeader(numberHeading(n.numericIlk()), b, n), n);
    }

    @Override protected int complexExternSize(Slaw c) {
        return numericSize(c);
    }

    @Override protected void externComplex(Slaw c, ByteWriter b)
        throws IOException {
        putNumVal(putNumHeader(complexHeading(c.numericIlk()), b, c), c);
    }

    @Override protected int vectorExternSize(Slaw v) {
        return numericSize(v);
    }

    @Override protected void externVector(Slaw v, ByteWriter b)
        throws IOException {
        putNumHeader(vectorHeading(v.numericIlk(), v.dimension()), b, v);
        for (Slaw n : v.emitList()) putNumVal(b, n);
    }

    @Override protected int complexVectorExternSize(Slaw v) {
        return numericSize(v);
    }

    @Override protected void externComplexVector(Slaw v, ByteWriter b)
        throws IOException {
        putNumHeader(
            complexVectorHeading(v.numericIlk(), v.dimension()), b, v);
        for (Slaw n : v.emitList()) putNumVal(putNumVal(b, n.car()), n.cdr());
    }

    @Override protected int multivectorExternSize(Slaw v) {
        return numericSize(v);
    }

    @Override protected void externMultivector(Slaw v, ByteWriter b)
        throws IOException {
        putNumHeader(multivectorHeading(v.numericIlk(), v.dimension()), b, v);
        for (Slaw n : v.emitList()) putNumVal(b, n);
    }

    @Override protected int arrayExternSize(Slaw a) { return arraySize(a); }

    @Override protected void externArray(Slaw a, ByteWriter b)
        throws IOException {
        putArray(arrayHeading(a.numericIlk()), b, a);
    }

    @Override protected int complexArrayExternSize(Slaw a) {
        return complexArraySize(a);
    }

    @Override protected void externComplexArray(Slaw a, ByteWriter b)
        throws IOException {
        putArray(complexArrayHeading(a.numericIlk()), b, a);
    }

    @Override protected int vectorArrayExternSize(Slaw a) {
        return arraySize(a);
    }

    @Override protected void externVectorArray(Slaw a, ByteWriter b)
        throws IOException {
        putArray(vectorArrayHeading(a.numericIlk(), a.dimension()), b, a);
    }

    @Override protected int complexVectorArrayExternSize(Slaw a) {
        return complexArraySize(a);
    }

    @Override protected void externComplexVectorArray(Slaw a, ByteWriter b)
        throws IOException {
        putArray(complexVectorArrayHeading(a.numericIlk(), a.dimension()),
                 b, a);
    }

    @Override protected int multivectorArrayExternSize(Slaw a) {
        final int cno = 1 << a.dimension();
        return OCT_LEN
            + roundUp(a.count() * cno * a.numericIlk().bytes());
    }

    @Override protected void externMultivectorArray(Slaw a, ByteWriter b)
        throws IOException {
        putArray(multivectorArrayHeading(a.numericIlk(), a.dimension()),
                 b, a);
    }

    @Override protected int consExternSize(Slaw c) { return listSize(c); }

    @Override protected void externCons(Slaw c, ByteWriter b)
        throws IOException {
        marshallAsList(c, b);
    }

    @Override protected int listExternSize(Slaw c) { return listSize(c); }

    @Override protected void externList(Slaw c, ByteWriter b)
        throws IOException {
        marshallAsList(c, b);
    }

    @Override protected int mapExternSize(Slaw c) { return listSize(c); }

    @Override protected void externMap(Slaw c, ByteWriter b)
        throws IOException {
        marshallAsList(c, b);
    }

    @Override protected void externProtein(Protein p, ByteWriter b)
        throws IOException {
        b.putLong(proteinHeading(octs(proteinExternSize(p))));

        final Slaw descrips = p.descrips();
        final Slaw ingests = p.ingests();
        final int dataLen = p.dataLength();

        final byte sb = proteinSecondHeadingByte(descrips != null,
                                                 ingests != null, dataLen);

        if (dataLen > 0 && dataLen <= WEE_PROTEIN_DATA_LEN) {
            b.put(sb);
            if (dataLen < WEE_PROTEIN_DATA_LEN)
                pad(b, WEE_PROTEIN_DATA_LEN - dataLen);
            b.putProteinData(p);
        } else {
            b.putLong(proteinSecondHeading(sb, dataLen));
        }

        if (descrips != null) extern(descrips, b);
        if (ingests != null) extern(ingests, b);
        if (dataLen > WEE_PROTEIN_DATA_LEN) b.putProteinData(p);
    }

    @Override protected int proteinExternSize(Protein p) {
        int len =
            2 * OCT_LEN + externSize(p.ingests()) + externSize(p.descrips());
        if (p.dataLength() > WEE_PROTEIN_DATA_LEN) len += p.dataLength();
        return roundUp(len);
    }

    @Override protected void prepareBuffer(ByteWriter b, Slaw s) {}

    @Override protected void finishBuffer(ByteWriter b, Slaw s)
        throws IOException {
        pad(b, padding(b.bytesWritten()));
    }

    private static ByteWriter pad(ByteWriter b, long n)
        throws IOException {
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

    private static ByteWriter putNumVal(ByteWriter buffer, Slaw n)
        throws IOException {
        assert n.isNumber() || n.isComplex();
        if (n.isComplex()) {
            putScalar(buffer, n.car());
            putScalar(buffer, n.cdr());
        } else {
            putScalar(buffer, n);
        }
        return buffer;
    }

    private static void putScalar(ByteWriter buffer, Slaw n)
        throws IOException {
        switch (n.numericIlk()) {
        case FLOAT32: buffer.putFloat((float)n.emitDouble()); break;
        case FLOAT64: buffer.putDouble(n.emitDouble()); break;
        case INT8: case UNT8: buffer.put((byte)n.emitLong()); break;
        case INT16: case UNT16: buffer.putShort((short)n.emitLong()); break;
        case INT32: case UNT32: buffer.putInt((int)n.emitLong()); break;
        case INT64: case UNT64: buffer.putLong(n.emitLong()); break;
        }
    }

    private static void marshallWeeStr(byte[] bs, ByteWriter b)
        throws IOException {
        assert bs.length <= STR_WEE_LEN;
        final int fb = WEE_STR_HEADING_BYTE | (bs.length + 1);
        b.put((byte)fb);
        pad(b, STR_WEE_LEN - bs.length).put(bs);
    }

    private static void marshallStr(byte[] bs, ByteWriter b)
        throws IOException {
        final int len = OCT_LEN + bs.length + 1;
        final int p = padding(len);
        final long hb = ((long)(STR_HEADING_BYTE|p))<<56;
        pad(b.putLong(hb|(long)octs(len+p)).put(bs), p);
    }

    private static int numericWidth(Slaw s) {
        final int nb = s.numericIlk().bytes();
        final int w = nb * (s.isMultivector() ? s.count() : s.dimension());
        return s.ilk().isComplexNumeric() ? 2 * w : w;
    }

    private static ByteWriter putNumHeader(long h, ByteWriter b, Slaw s)
        throws IOException {
        final int w = numericWidth(s);
        if (w > NUM_WEE_LEN) return b.putLong(h);
        b.putInt((int)(h>>>32));
        return pad(b, NUM_WEE_LEN - w);
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

    private static void putArray(long h, ByteWriter b, Slaw a)
        throws IOException {
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

    private void marshallAsList(Slaw l, ByteWriter b)
        throws IOException {
        final int count = l.count();
        final long h =
            (long)(compositeHeadingByte(l.ilk()))|Math.min(15, count);
        b.putLong((h<<56)|octs(listSize(l)));
        if (count > WEE_LIST_LEN) b.putLong(count);
        for (int i = 0; i < count; i++) extern(l.nth(i), b);
    }
}
