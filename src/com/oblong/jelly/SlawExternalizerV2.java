package com.oblong.jelly;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import static com.oblong.jelly.NumericIlk.*;
import com.oblong.util.Pair;

/**
 * Created: Sun Apr 18 15:07:50 2010
 *
 * @author jao
*/
final class SlawExternalizerV2 extends SlawExternalizer {

    int nilExternSize(Slaw s) { return NIL_OCT.length; }

    byte[] externNil(Slaw s) { return Arrays.copyOf(NIL_OCT, 8); }

    int boolExternSize(Slaw b) { return TRUE_OCT.length; }

    byte[] externBool(Slaw b) {
        return Arrays.copyOf(b.asBoolean() ? TRUE_OCT : FALSE_OCT, 8);
    }

    int stringExternSize(Slaw s) {
        final int bn = stringBytes(s).length;
        return (bn > 6) ? roundUp(bn + 8 + 1) : 8;
    }

    byte[] externString(Slaw s) {
        final byte[] bs = stringBytes(s);
        return (bs.length > 6) ? marshallStr(bs) : marshallWeeStr(bs);
    }

    int numberExternSize(Slaw n) {
        return roundUp(5 + n.numericIlk().bytes());
    }

    byte[] externNumber(Slaw n) {
        return (n.numericIlk().bytes() < 5)
            ? marshallSmallNum(n) : marshallNum(n);
    }


    int complexExternSize(Slaw c) {
        return 8 + 2 * c.numericIlk().bytes();
    }

    byte[] externComplex(Slaw c) {
        final SlawBuffer r = new SlawBuffer(complexExternSize(c));
        final Pair<Slaw,Slaw> cc = c.asPair();
        return r.putLong(NUM_OCTS.get(c.numericIlk())|COMPLEX_OCT_MASK)
                .putNumVal(cc.first)
                .putNumVal(cc.second)
                .bytes();
    }

    int vectorExternSize(Slaw v) {
        return roundUp(8 + v.dimension() * v.numericIlk().bytes());
    }

    byte[] externVector(Slaw v) {
        SlawBuffer r = new SlawBuffer(vectorExternSize(v));
        r.putLong(firstOct(v.numericIlk(), v.dimension() - 1));
        for (Slaw n : v.asList()) r.putNumVal(n);
        return r.bytes();
    }

    int complexVectorExternSize(Slaw v) {
        return roundUp(8 + 2 * v.dimension() * v.numericIlk().bytes());
    }

    byte[] externComplexVector(Slaw v) {
        final SlawBuffer r = new SlawBuffer(complexVectorExternSize(v));
        r.putLong(COMPLEX_OCT_MASK |
                  firstOct(v.numericIlk(), v.dimension() - 1));
        for (Slaw n : v.asList()) {
            final Pair<Slaw,Slaw> cs = n.asPair();
            r.putNumVal(cs.first).putNumVal(cs.second);
        }
        return r.bytes();
    }

    int multiVectorExternSize(Slaw v) {
        return roundUp(8 + v.count() * v.numericIlk().bytes());
    }

    byte[] externMultiVector(Slaw v) {
        final SlawBuffer r = new SlawBuffer(multiVectorExternSize(v));
        r.putLong(firstOct(v.numericIlk(), v.dimension() - 2));
        for (Slaw n : v.asList()) r.putNumVal(n);
        return r.bytes();
    }


    private static int roundUp(int len) { return (len + 7) & -8; }

    private static byte[] stringBytes(Slaw s) {
        try {
            return s.asString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("externalize: UTF-8 not supported", e);
        }
    }

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
        final SlawBuffer r = new SlawBuffer(roundUp(len));
        final int p = r.capacity() - len;
        return r.putFirstOct((STR_TB | p), r.octs()).put(bs).bytes();
    }

    private static long firstOct(NumericIlk i, long d) {
        return (d << 54) | NUM_OCTS.get(i);
    }

    private static byte[] marshallSmallNum(Slaw n) {
        final NumericIlk i = n.numericIlk();
        final int h = (int) (NUM_OCTS.get(i)>>32);
        final SlawBuffer r = new SlawBuffer(8);
        if (LE)
            r.putNumVal(n).pad(4 - i.bytes()).putInt(h);
        else
            r.putInt(h).putNumVal(n);
        return r.bytes();
    }

    private static byte[] marshallNum(Slaw n) {
        final SlawBuffer r = new SlawBuffer(16);
        return r.putLong(NUM_OCTS.get(n.numericIlk())).putNumVal(n).bytes();
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
    private static final long COMPLEX_OCT_MASK = 0x2L<<56;

    private static class SlawBuffer {

        SlawBuffer(int len) {
            assert len > 0;
            this.buffer = ByteBuffer.allocate(len);
            this.buffer.order(ByteOrder.nativeOrder());
        }

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
        SlawBuffer putLong(long l) { this.buffer.putLong(l); return this; }

        SlawBuffer putFirstOct(long b, long rest) {
            assert rest >= 0 && rest < (1<<56) : "<rest> was " + rest;
            this.buffer.putLong(rest).put(LE ? 7 : 0, (byte)b);
            return this;
        }

        SlawBuffer putNumVal(Slaw n) {
            assert n.is(SlawIlk.NUMBER);
            NumericIlk i = n.numericIlk();
            if (i == FLOAT32) {
                this.buffer.putFloat((float)n.asDouble());
            } else if (i == FLOAT64) {
                this.buffer.putDouble(n.asDouble());
            } else if (i == INT8 || i == UNT8) {
                this.buffer.put((byte)n.asLong());
            } else if (i == INT16 || i == UNT16) {
                this.buffer.putShort((short)n.asLong());
            } else if (i == INT32 || i == UNT32) {
                this.buffer.putInt((int)n.asLong());
            } else if (i == INT64 || i == UNT64) {
                this.buffer.putLong(n.asLong());
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

    }  // SlawBuffer

}  // SlawExternalizerV2
