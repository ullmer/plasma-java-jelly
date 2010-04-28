package com.oblong.jelly;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.EnumMap;
import java.util.Map;

import static com.oblong.jelly.NumericIlk.*;
import static com.oblong.jelly.SlawIlk.*;

/**
 * Created: Sun Apr 18 15:07:50 2010
 *
 * @author jao
*/
final class SlawExternalizerV2 extends SlawExternalizer {

    int nilExternSize(Slaw s) { return NIL_OCT.length; }

    void externNil(Slaw s, ByteBuffer b) { b.put(NIL_OCT); }

    int boolExternSize(Slaw b) { return TRUE_OCT.length; }

    void externBool(Slaw b, ByteBuffer r) {
        r.put(b.asBoolean() ? TRUE_OCT : FALSE_OCT);
    }

    int stringExternSize(Slaw s) {
        final int bn = stringBytes(s).length;
        return (bn > 6) ? roundUp(bn + 8 + 1) : 8;
    }

    void externString(Slaw s, ByteBuffer b) {
        final byte[] bs = stringBytes(s);
        if (bs.length > 6) marshallStr(bs, b); else marshallWeeStr(bs, b);
    }

    int numberExternSize(Slaw n) {
        return roundUp(5 + n.numericIlk().bytes());
    }

    void externNumber(Slaw n, ByteBuffer b) {
        if (n.numericIlk().bytes() < 5) marshallSmallNum(n, b);
        else marshallNum(n, b);
    }

    int complexExternSize(Slaw c) { return 8 + 2 * c.numericIlk().bytes(); }

    void externComplex(Slaw c, ByteBuffer b) {
        b.putLong(NUM_OCTS.get(c.numericIlk())|COMPLEX_OCT_MASK);
        putNumVal(putNumVal(b, c.head()), c.tail());
    }

    int vectorExternSize(Slaw v) {
        return roundUp(8 + v.dimension() * v.numericIlk().bytes());
    }

    void externVector(Slaw v, ByteBuffer b) {
        b.putLong(heading(v.numericIlk(), v.dimension() - 1));
        for (Slaw n : v.asList()) putNumVal(b, n);
    }

    int complexVectorExternSize(Slaw v) {
        return roundUp(8 + 2 * v.dimension() * v.numericIlk().bytes());
    }

    void externComplexVector(Slaw v, ByteBuffer b) {
        b.putLong(COMPLEX_OCT_MASK |
                  heading(v.numericIlk(), v.dimension() - 1));
        for (Slaw n : v.asList()) putNumVal(putNumVal(b, n.head()), n.tail());
    }

    int multiVectorExternSize(Slaw v) {
        return roundUp(8 + v.count() * v.numericIlk().bytes());
    }

    void externMultiVector(Slaw v, ByteBuffer b) {
        b.putLong(heading(v.numericIlk(), v.dimension() - 2));
        for (Slaw n : v.asList()) putNumVal(b, n);
    }

    int consExternSize(Slaw c) {
        return listSize(c);
    }

    void externCons(Slaw c, ByteBuffer b) {
        marshallAsList(c, b);
    }

    int listExternSize(Slaw c) {
        return listSize(c);
    }

    void externList(Slaw c, ByteBuffer b) {
        marshallAsList(c, b);
    }

    int mapExternSize(Slaw c) {
        return listSize(c);
    }

    void externMap(Slaw c, ByteBuffer b) {
        marshallAsList(c, b);
    }

    void prepareBuffer(ByteBuffer b, Slaw s) {
        b.order(ByteOrder.nativeOrder());
    }

    void finishBuffer(ByteBuffer b, Slaw s, int begin) {
        int len = b.capacity() - b.position();
        while (len-- > 0) b.put((byte)0);
        Integer tb = COMPOSITE_TBS.get(s.ilk());
        if (tb != null) {
            b.mark().position(begin);
            putHeading(b, tb | (s.count() & 0x0F), octs(len)).reset();
        }
    }

    private static int octs(int len) { return len >>> 3; }

    private static int roundUp(int len) { return (len + 7) & -8; }

    private static byte[] stringBytes(Slaw s) {
        try {
            return s.asString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("externalize: UTF-8 not supported", e);
        }
    }

    private static ByteBuffer putHeading(
        ByteBuffer buffer, long b, long rest) {
        assert rest >= 0 && rest < (1<<56) : "<rest> was " + rest;
        buffer.putLong(rest).put(LE ? 7 : 0, (byte)b);
        return buffer;
    }

    private static ByteBuffer putNumVal(ByteBuffer buffer, Slaw n) {
        assert n.is(SlawIlk.NUMBER);
        NumericIlk i = n.numericIlk();
        if (i == FLOAT32) {
            buffer.putFloat((float)n.asDouble());
        } else if (i == FLOAT64) {
            buffer.putDouble(n.asDouble());
        } else if (i == INT8 || i == UNT8) {
            buffer.put((byte)n.asLong());
        } else if (i == INT16 || i == UNT16) {
            buffer.putShort((short)n.asLong());
        } else if (i == INT32 || i == UNT32) {
            buffer.putInt((int)n.asLong());
        } else if (i == INT64 || i == UNT64) {
            buffer.putLong(n.asLong());
        }
        return buffer;
    }

    private static void marshallWeeStr(byte[] bs, ByteBuffer b) {
        assert bs.length < 7;
        final int fb = (WEE_STR_TB | bs.length + 1);
        if (LE) {
            b.put(bs);
            for (int i = bs.length; i < 7; ++i) b.put(NUL);
            b.put((byte)fb);
        } else {
            b.put((byte)fb);
            for (int i = bs.length; i < 6; ++i) b.put(NUL);
            b.put(bs);
        }
    }

    private static void marshallStr(byte[] bs, ByteBuffer b) {
        final int len = 8 + bs.length + 1;
        final int p = roundUp(len) - len;
        putHeading(b, (STR_TB | p), octs(len + p)).put(bs);
    }

    private static long heading(NumericIlk i, long d) {
        return (d << 54) | NUM_OCTS.get(i);
    }

    private static void marshallSmallNum(Slaw n, ByteBuffer b) {
        final NumericIlk i = n.numericIlk();
        final int h = (int) (NUM_OCTS.get(i)>>32);
        if (LE) {
            putNumVal(b, n);
            for (int k = 0; k < 4 - i.bytes(); k++) b.put((byte)0);
            b.putInt(h);
        }
        else putNumVal(b.putInt(h), n);
    }

    private static void marshallNum(Slaw n, ByteBuffer b) {
        putNumVal(b.putLong(NUM_OCTS.get(n.numericIlk())), n);
    }

    private int listSize(Slaw l) {
        int len = 8;
        for (Slaw s : l.asList()) len = len + externSize(s);
        return roundUp(len);
    }

    private void marshallAsList(Slaw l, ByteBuffer b) {
        b.position(b.position() + 8);
        for (Slaw s : l.asList()) extern(s, b);
    }

    static final boolean LE =
        ByteOrder.LITTLE_ENDIAN == ByteOrder.nativeOrder();

    private static final byte NUL = 0;

    private static final byte[] FALSE_OCT = makeOct(0x20, 0x00);
    private static final byte[] TRUE_OCT = makeOct(0x20, 0x01);
    private static final byte[] NIL_OCT = makeOct(0x20, 0x02);

    private static byte[] makeOct(int fst, int rest) {
        ByteBuffer b = ByteBuffer.allocate(8);
        b.order(ByteOrder.nativeOrder());
        return putHeading(b, fst, rest).array();
    }

    private static final byte STR_TB = 0x70;
    private static final byte WEE_STR_TB = 0x30;
    private static final long COMPLEX_OCT_MASK = 0x2L<<56;

    private static final Map<NumericIlk, Long> NUM_OCTS;
    static {
        NUM_OCTS = new EnumMap<NumericIlk, Long>(NumericIlk.class);
        NUM_OCTS.put(INT8, 0x80L<<56);
        NUM_OCTS.put(UNT8, 0x90L<<56);
        NUM_OCTS.put(INT16, 0x840040L<<40);
        NUM_OCTS.put(UNT16, 0x940040L<<40);
        NUM_OCTS.put(INT32, 0x8800C0L<<40);
        NUM_OCTS.put(UNT32, 0x9800C0L<<40);
        NUM_OCTS.put(INT64, 0x8C01C0L<<40);
        NUM_OCTS.put(UNT64, 0x9C01C0L<<40);
        NUM_OCTS.put(FLOAT32, 0xA800C0L<<40);
        NUM_OCTS.put(FLOAT64, 0xAC01C0L<<40);
    }

    private static final Map<SlawIlk, Integer> COMPOSITE_TBS;
    static {
        COMPOSITE_TBS = new EnumMap<SlawIlk, Integer>(SlawIlk.class);
        COMPOSITE_TBS.put(CONS, 0x60);
        COMPOSITE_TBS.put(LIST, 0x40);
        COMPOSITE_TBS.put(MAP, 0x50);
    }
}
