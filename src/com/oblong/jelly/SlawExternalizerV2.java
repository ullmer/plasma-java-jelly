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
        ByteBuffer r = nativeBuffer(8 + 2 * ilk.bytes());
        r.put(firstOct(leadingNumByte(c), (ilk.bytes() - 1) << 54));
        putNumVal(r, c.re());
        putNumVal(r, c.im());
        padBuffer(r);
        return r.array();
    }

    byte[] externalizeNumVector(SlawVector<SlawNumber> v) {
        Ilk ilk = v.ilk();
        ByteBuffer r = nativeBuffer(8 + v.dimension() * ilk.bytes());
        r.put(firstOct(v));
        for (SlawNumber n : v.asList()) putNumVal(r, n);
        padBuffer(r);
        return r.array();
    }

    byte[] externalizeComplexVector(SlawVector<SlawComplex> v) {
        Ilk ilk = v.ilk();
        ByteBuffer r = nativeBuffer(8 + 2 * v.dimension() * ilk.bytes());
        r.put(firstOct(v));
        for (SlawComplex n : v.asList()) {
            putNumVal(r, n.re());
            putNumVal(r, n.im());
        }
        padBuffer(r);
        return r.array();
    }

    byte[] externalize(SlawMultiVector v) {
        Ilk ilk = v.ilk();
        List<SlawNumber> ls = v.asList();
        ByteBuffer r = nativeBuffer(8 + ls.size() * ilk.bytes());
        r.put(firstOct(v));
        for (SlawNumber n : ls) putNumVal(r, n);
        padBuffer(r);
        return r.array();
    }

    // byte[] externalize(SlawArray<SlawNumber> a);
    // byte[] externalize(SlawArray<SlawComplex> a);
    // byte[] externalize(SlawArray<SlawVector<SlawNumber>> a);
    // byte[] externalize(SlawArray<SlawVector<SlawComplex>> a);
    // byte[] externalize(SlawArray<SlawMultiVector> a);
    // byte[] externalize(SlawCons m);
    // byte[] externalize(SlawList l);
    // byte[] externalize(SlawMap m);

    private static boolean isLE() {
        return ByteOrder.LITTLE_ENDIAN == ByteOrder.nativeOrder();
    }

    private static int pad(int len) {
        return (len & 8) + ((len & 7) == 0 ? 0 : 1);
    }

    private static long octlen(long len) { return len >>> 3; }

    private static ByteBuffer nativeBuffer(int c) {
        final ByteBuffer r = ByteBuffer.allocate(pad(c));
        r.order(ByteOrder.nativeOrder());
        return r;
    }

    private static void padBuffer(ByteBuffer r) {
        final int end = r.capacity();
        for (int i = r.position(); i < end; i++) r.put(i, (byte)0);
    }

    private static byte[] firstOct(int b, long rest) {
        assert rest >= 0 && rest < (1<<56);
        final ByteBuffer r = nativeBuffer(8);
        r.putLong(rest);
        r.put(isLE() ? 7 : 0, (byte)b);
        return r.array();
    }

    private static byte[] marshallWeeStr(byte[] bs) {
        assert bs.length < 7;
        long h = 0;
        for (int i = 0; i < bs.length; i++) h = (h << 1) | bs[i];
        return firstOct((WEE_STR_TB | bs.length), h << 1); // final null
    }

    private static byte[] marshallStr(byte[] bs) {
        final int len = 8 + bs.length + 1;
        final ByteBuffer r = nativeBuffer(len);
        final int p = r.capacity() - len;
        final byte[] fst = firstOct((STR_TB | p), octlen(r.capacity()));
        r.put(fst).put(bs);
        padBuffer(r);
        return r.array();
    }

    private static void putNumVal(ByteBuffer b, SlawNumber n) {
        Ilk i = n.ilk();
        if (i == FLOAT32) {
            b.putFloat(n.floatValue());
        } else if (i == FLOAT64) {
            b.putDouble(n.doubleValue());
        } else if (i == INT8 || i == UNT8) {
            b.put(n.byteValue());
        } else if (i == INT16 || i == UNT16) {
            b.putShort(n.shortValue());
        } else if (i == INT32 || i == UNT32) {
            b.putInt(n.intValue());
        } else if (i == INT64 || i == UNT64) {
            b.putLong(n.longValue());
        }
    }

    private static byte leadingNumByte(Ilk ilk, boolean complex, boolean mv) {
        int r = NUM_TB.get(ilk);
        if (complex) r = r | 2;
        if (mv) r = r | 1;
        return (byte)r;
    }

    private static byte leadingNumByte(SlawNumber n) {
        return leadingNumByte(n.ilk(), false, false);
    }

    private static byte leadingNumByte(SlawComplex n) {
        return leadingNumByte(n.ilk(), true, false);
    }

    private static byte[] firstOct(SlawVector<?> v) {
        final Ilk i = v.ilk();
        return firstOct(leadingNumByte(i, v.isComplexVector(), false),
                        (v.dimension() - 1) << 56 | (i.bytes() - 1) << 54);
    }

    private static byte[] firstOct(SlawMultiVector v) {
        final Ilk i = v.ilk();
        return firstOct(leadingNumByte(i, false, true),
                        (v.dimension() - 2) << 56 | (i.bytes() - 1) << 54);
    }

    private static byte[] marshallSmallNum(SlawNumber n) {
        byte fb = leadingNumByte(n);
        long bs = n.ilk().bytes() - 1;
        if (n.ilk().isIntegral()) {
            return firstOct(fb, bs << 54 | n.longValue());
        } else {
            final ByteBuffer r = nativeBuffer(8);
            int h = (int) ((fb << 24) + (bs << 14));
            if (isLE()) {
                r.putFloat(n.floatValue());
                r.putInt(h);
            } else {
                r.putInt(h);
                r.putFloat(n.floatValue());
            }
            return r.array();
        }
    }

    private static byte[] marshallNum(SlawNumber n) {
        final ByteBuffer r = nativeBuffer(16);
        r.put(firstOct(leadingNumByte(n), 7 << 54));
        putNumVal(r, n);
        padBuffer(r);
        return r.array();
    }

    private static final byte NUL = 0;

    private static final byte[] FALSE_OCT = firstOct(0x20, 0x00);
    private static final byte[] TRUE_OCT = firstOct(0x20, 0x01);
    private static final byte[] NIL_OCT = firstOct(0x20, 0x02);
    private static final byte STR_TB = 0x70;
    private static final byte WEE_STR_TB = 0x30;

    private static final Map<Ilk, Byte> NUM_TB;
    static {
        NUM_TB = new EnumMap<Ilk, Byte>(Ilk.class);
        NUM_TB.put(INT8, (byte)02000);
        NUM_TB.put(INT16, (byte)02010);
        NUM_TB.put(INT32, (byte)02020);
        NUM_TB.put(INT64, (byte)02030);
        NUM_TB.put(UNT8, (byte)02100);
        NUM_TB.put(UNT16, (byte)02110);
        NUM_TB.put(UNT32, (byte)02120);
        NUM_TB.put(UNT64, (byte)02130);
        NUM_TB.put(FLOAT32, (byte)02220);
        NUM_TB.put(FLOAT64, (byte)02230);
    }
}
