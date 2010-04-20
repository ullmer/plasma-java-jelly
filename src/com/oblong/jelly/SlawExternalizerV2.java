package com.oblong.jelly;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.oblong.jelly.NumericSlaw.Ilk;
import static com.oblong.jelly.NumericSlaw.Ilk.*;

/**
 * Created: Sun Apr 18 15:07:50 2010
 *
 * @author jao
*/
final class SlawExternalizerV2 extends SlawExternalizer {

    byte[] externalize(Slaw s) {
        assert s.isNil();
        return s.isNil() ? Arrays.copyOf(NIL_OCT, 8) : null;
    }

    byte[] externalize(SlawBool b) {
        return Arrays.copyOf(b.value() ? TRUE_OCT : FALSE_OCT, 8);
    }

    byte[] externalize(SlawString s) {
        try {
            byte[] bs = s.value().getBytes("UTF-8");
            return (bs.length > 6) ? marshallStr(bs) : marshallWeeStr(bs);
        } catch (UnsupportedEncodingException e) {
            throw new SlawError("externalize: UTF-8 not supported", e);
        }
    }

    byte[] externalize(SlawNumber n) {
        return (n.ilk().bytes() < 5) ? marshallSmallNum(n) : marshallNum(n);
    }

    byte[] externalize(SlawComplex c) {
        Ilk ilk = c.ilk();
        SlawBuffer r = new SlawBuffer(8 + 2 * ilk.bytes());
        r.putFirstOct(leadingNumByte(c), (ilk.bytes() - 1) << 54);
        return r.putNumVal(c.re()).putNumVal(c.im()).bytes();
    }

    byte[] externalize(SlawNumberVector v) {
        Ilk ilk = v.ilk();
        SlawBuffer r = new SlawBuffer(8 + v.dimension() * ilk.bytes());
        firstOct(r, v);
        for (SlawNumber n : v.asList()) r.putNumVal(n);
        return r.bytes();
    }

    byte[] externalize(SlawComplexVector v) {
        Ilk ilk = v.ilk();
        SlawBuffer r = new SlawBuffer(8 + 2 * v.dimension() * ilk.bytes());
        firstOct(r, v);
        for (SlawComplex n : v.asList())
            r.putNumVal(n.re()).putNumVal(n.im());
        return r.bytes();
    }

    byte[] externalize(SlawMultiVector v) {
        Ilk ilk = v.ilk();
        List<SlawNumber> ls = v.asList();
        SlawBuffer r = new SlawBuffer(8 + ls.size() * ilk.bytes());
        firstOct(r, v);
        for (SlawNumber n : ls) r.putNumVal(n);
        return r.bytes();
    }

    // byte[] externalize(SlawArray<SlawNumber> a);
    // byte[] externalize(SlawArray<SlawComplex> a);
    // byte[] externalize(SlawArray<SlawVector<SlawNumber>> a);
    // byte[] externalize(SlawArray<SlawComplexVector> a);
    // byte[] externalize(SlawArray<SlawMultiVector> a);
    // byte[] externalize(SlawCons m);
    // byte[] externalize(SlawList l);
    // byte[] externalize(SlawMap m);

    private static byte[] marshallWeeStr(byte[] bs) {
        assert bs.length < 7;
        final SlawBuffer b = new SlawBuffer(8);
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
        return b.bytes();
    }

    private static byte[] marshallStr(byte[] bs) {
        final int len = 8 + bs.length + 1;
        final SlawBuffer r = new SlawBuffer(len);
        final int p = r.capacity() - len;
        return r.putFirstOct((STR_TB | p), r.octs()).put(bs).bytes();
    }

    private static byte leadingNumByte(Ilk ilk, boolean cpx, boolean mv) {
        int r = NUM_TB.get(ilk);
        if (cpx) r = r | 2;
        if (mv) r = r | 1;
        return (byte)r;
    }

    private static byte leadingNumByte(SlawNumber n) {
        return leadingNumByte(n.ilk(), false, false);
    }

    private static byte leadingNumByte(SlawComplex n) {
        return leadingNumByte(n.ilk(), true, false);
    }

    private static long numTrail(Ilk i, int d, boolean mv) {
        return (d - (mv ? 2 : 1)) << 54 | (i.bytes() - 1) << 46;
    }

    private static long numTrail(Ilk i) { return numTrail(i, 1, false); }

    private static SlawBuffer firstOct(SlawBuffer b, SlawNumberVector v) {
        return b.putFirstOct(leadingNumByte(v.ilk(), false, false),
                             numTrail(v.ilk(), v.dimension(), false));
    }

    private static SlawBuffer firstOct(SlawBuffer b, SlawComplexVector v) {
        return b.putFirstOct(leadingNumByte(v.ilk(), true, false),
                             numTrail(v.ilk(), v.dimension(), false));
    }

    private static SlawBuffer firstOct(SlawBuffer b, SlawMultiVector v) {
        return b.putFirstOct(leadingNumByte(v.ilk(), false, true),
                             numTrail(v.ilk(), v.dimension(), true));
    }

    private static byte[] marshallSmallNum(SlawNumber n) {
        final Ilk i = n.ilk();
        assert i.bytes() < 5;
        final int fb = leadingNumByte(n);
        final int h = (int) ((fb << 24) | (numTrail(i) >> 32));
        final SlawBuffer r = new SlawBuffer(8);
        if (LE)
            r.putNumVal(n).pad(4 - i.bytes()).putInt(h);
        else
            r.putInt(h).putNumVal(n);
        return r.bytes();
    }

    private static byte[] marshallNum(SlawNumber n) {
        final SlawBuffer r = new SlawBuffer(16);
        return r.putFirstOct(leadingNumByte(n), numTrail(n.ilk()))
                .putNumVal(n).bytes();
    }

    static final boolean LE =
        ByteOrder.LITTLE_ENDIAN == ByteOrder.nativeOrder();

    private static final byte NUL = 0;

    private static final byte[] FALSE_OCT;
    private static final byte[] TRUE_OCT;
    private static final byte[] NIL_OCT;

    static {
        FALSE_OCT = new SlawBuffer(8).putFirstOct(0x20, 0x00).bytes();
        TRUE_OCT = new SlawBuffer(8).putFirstOct(0x20, 0x01).bytes();
        NIL_OCT = new SlawBuffer(8).putFirstOct(0x20, 0x02).bytes();
    }

    private static final byte STR_TB = 0x70;
    private static final byte WEE_STR_TB = 0x30;

    private static final Map<Ilk, Integer> NUM_TB;
    static {
        NUM_TB = new EnumMap<Ilk, Integer>(Ilk.class);
        for (Ilk i : Ilk.values())
            NUM_TB.put(i,
                       0x80 + (i.bytes() - 1)
                       + (i.isSigned() ? 0 : 0x10)
                       + (i.isIntegral() ? 0 : 0x20));
    }

    private static class SlawBuffer {

        SlawBuffer(int len) {
            assert len > 0;
            this.buffer = ByteBuffer.allocate(roundUp(len));
            this.buffer.order(ByteOrder.nativeOrder());
        }

        static int roundUp(int len) { return (len + 7) & -8; }

        int capacity () { return this.buffer.capacity(); }

        int octs() { return capacity() >>> 3; }

        byte[] bytes() {
            padBuffer();
            return this.buffer.array();
        }

        void padBuffer() {
            pad(this.buffer.capacity() - this.buffer.position());
        }

        SlawBuffer pad(int len) {
            while (len-- > 0) this.buffer.put((byte)0);
            return this;
        }

        SlawBuffer put(byte b) { this.buffer.put(b); return this; }
        SlawBuffer put(byte b[]) { this.buffer.put(b); return this; }
        SlawBuffer putInt(int i) { this.buffer.putInt(i); return this; }

        SlawBuffer putFirstOct(int b, long rest) {
            assert rest >= 0 && rest < (1<<56) : "<rest> was " + rest;
            this.buffer.putLong(rest).put(LE ? 7 : 0, (byte)b);
            return this;
        }

        SlawBuffer putNumVal(SlawNumber n) {
            Ilk i = n.ilk();
            if (i == FLOAT32) {
                this.buffer.putFloat(n.floatValue());
            } else if (i == FLOAT64) {
                this.buffer.putDouble(n.doubleValue());
            } else if (i == INT8 || i == UNT8) {
                this.buffer.put(n.byteValue());
            } else if (i == INT16 || i == UNT16) {
                this.buffer.putShort(n.shortValue());
            } else if (i == INT32 || i == UNT32) {
                this.buffer.putInt(n.intValue());
            } else if (i == INT64 || i == UNT64) {
                this.buffer.putLong(n.longValue());
            }
            return this;
        }

        public String toString() {
            StringBuilder buf = new StringBuilder ("{ ");
            for (byte b : this.buffer.array()) buf.append(b + " ");
            buf.append("}");
            return buf.toString();
        }

        private ByteBuffer buffer;
    } // SlawBuffer
}
