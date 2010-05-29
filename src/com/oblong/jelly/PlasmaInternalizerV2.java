// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static com.oblong.jelly.PlasmaProtocolV2.*;

final class PlasmaInternalizerV2 implements SlawInternalizer {

    @Override public Protein internProtein(ByteBuffer b, SlawFactory f)
        throws SlawParseError {
        final byte hn = peekNibble(b, 0);
        final ByteOrder bo = b.order();
        if (hn == PROTEIN_NATIVE_NIBBLE) {
            b.order(ByteOrder.BIG_ENDIAN);
        } else if (hn == PROTEIN_NON_NATIVE_NIBBLE) {
            b.order(ByteOrder.LITTLE_ENDIAN);
        } else {
            throw new SlawParseError(b, "Not a protein");
        }
        try {
            return doInternProtein(b, f);
        } finally {
            b.order(bo);
        }
    }

    @Override public Slaw internSlaw(ByteBuffer b, SlawFactory f)
        throws SlawParseError {
        final int beg = b.position();
        try {
            checkLength(b, OCT_LEN);
            final byte nb = peekNibble(b, littleEndian(b) ? 7 : 0);
            switch (nb) {
            case PROTEIN_NATIVE_NIBBLE: case PROTEIN_NON_NATIVE_NIBBLE:
                return internProtein(b, f);
            case ATOM_NIBBLE:
                return internAtom(b, f);
            case WEE_STR_NIBBLE:
                return internWeeString(b, f);
            case STR_NIBBLE:
                return internString(b, f);
            case INT_NIBBLE: case UNT_NIBBLE: case FLOAT_NIBBLE:
                return internNumeric(b, f);
            case INT_ARRAY_NIBBLE: case UNT_ARRAY_NIBBLE:
            case FLOAT_ARRAY_NIBBLE:
                return internArray(b, f);
            case CONS_NIBBLE:
                return internCons(b, f);
            case LIST_NIBBLE:
                return internList(b, f);
            case MAP_NIBBLE:
                return internMap(b, f);
            default:
                throw new SlawParseError
                    (b, "Unrecognized format (" + nb + ")");
            }
        } catch (BufferUnderflowException e) {
            b.position(beg);
            throw new SlawParseError(b, "Buffer too short");
        }
    }

    private Protein doInternProtein(ByteBuffer b, SlawFactory f)
        throws SlawParseError {
        final int beg = b.position();
        final long hd = b.getLong();
        final long sh = b.getLong();
        final Slaw descrips =
            proteinHasDescrips(sh) ? internSlaw(b, f) : null;
        final Slaw ingests =
            proteinHasIngests(sh) ? internSlaw(b, f) : null;
        final int end = b.position();
        final boolean wee = proteinHasWeeData(sh);
        final int dataLen = (int)(proteinDataLen(sh));
        final byte[] data = new byte[dataLen];
        if (wee) b.position(beg + weeOffset(b, dataLen));
        b.get(data, 0, dataLen);
        if (wee) b.position(end);
        return f.protein(descrips, ingests, data);
    }

    private static Slaw internAtom(ByteBuffer b, SlawFactory f)
        throws SlawParseError {
        final long h = b.getLong();
        if (h == NIL_HEADING) return f.nil();
        if (h == TRUE_HEADING) return f.bool(true);
        if (h == FALSE_HEADING) return f.bool(false);
        throw new SlawParseError(b, "Invalid atom (" + h + ")");
    }

    private static Slaw internWeeString(ByteBuffer b, SlawFactory f)
        throws SlawParseError {
        final int beg = b.position();
        final int len = weeStringLength(b.get());
        b.position(beg + weeOffset(b, len));
        final String str = readString(b, len - 1);
        b.position(beg + OCT_LEN);
        return f.string(str);
    }

    private static Slaw internString(ByteBuffer b, SlawFactory f)
        throws SlawParseError {
        final long h = b.getLong();
        final String str = readString(b, stringLength(h) - 1);
        b.position(b.position() + stringPadding(h) + 1);
        return f.string(str);
    }

    private static String readString(ByteBuffer b, int len)
        throws SlawParseError {
        checkLength(b, len);
        if (len == 0) return "";
        final byte[] bs = new byte[len];
        b.get(bs, 0, len);
        try {
            return new String(bs, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new SlawParseError(b, "UTF-8 not supported");
        }
    }

    private static Slaw internNumeric(ByteBuffer b, SlawFactory f)
        throws SlawParseError {
        final int beg = b.position();
        final long h = b.getLong();
        final int bs = numericBytes(h);
        final boolean isWee = bs <= NUM_WEE_LEN;
        if (isWee) b.position(beg + weeOffset(b, bs));
        checkLength(b, bs);
        Slaw res = isNumericScalar(h) ?
            internNum(h, b, f) : internVector(h, b, f);
        if (!isWee) b.position(beg + roundUp(OCT_LEN + bs));
        return res;
    }

    private static Slaw internNum(long h, ByteBuffer b, SlawFactory f)
        throws SlawParseError {
        return readNumber(numericIlk(h), isComplexNumeric(h), b, f);
    }

    private static Slaw readNumber(NumericIlk ni,
                                   boolean c,
                                   ByteBuffer b,
                                   SlawFactory f) throws SlawParseError {
        if (c) {
            final Slaw re = readScalar(ni, b, f);
            final Slaw im = readScalar(ni, b, f);
            return f.complex(re, im);
        } else {
            return readScalar(ni, b, f);
        }
    }

    private static Slaw readScalar(NumericIlk ni, ByteBuffer b, SlawFactory f)
        throws SlawParseError {
        final int bs = ni.bytes();
        checkLength(b, bs);
        if (ni.isIntegral()) {
            long v = 0;
            switch (bs) {
            case 1: v = b.get() & 0xffL; break;
            case 2: v = b.getShort() & 0xffffL; break;
            case 4: v = b.getInt() & 0xffffffffL; break;
            case 8: v = b.getLong(); break;
            default: assert false : "Unexpected width: " + ni.bytes();
            }
            return f.number(ni, v);
        }
        return (ni == NumericIlk.FLOAT32) ?
            f.number(ni, b.getFloat()) : f.number(ni, b.getDouble());
    }

    private static Slaw internVector(long h, ByteBuffer b, SlawFactory f)
        throws SlawParseError {
        final NumericIlk ni = numericIlk(h);
        final boolean mv = isMultivector(h);
        final boolean c = !mv && isComplexNumeric(h);
        final int d = numericDimension(h);
        return readVector(ni, d, c, mv, b, f);
    }

    private static Slaw internArray(ByteBuffer b, SlawFactory f)
        throws SlawParseError {
        final long h = b.getLong();
        final int count = (int)arrayBreadth(h);
        if (count == 0) return emptyArray(h, b, f);
        final int len = (int) (count * numericBytes(h));
        checkLength(b, len);
        final int beg = b.position();
        Slaw[] cmps = readArray(h, count, b, f);
        b.position(beg + roundUp(len));
        return f.array(cmps);
    }

    private static Slaw[] readArray(long h, int count,
                                    ByteBuffer b, SlawFactory f)
        throws SlawParseError {
        final NumericIlk ni = numericIlk(h);
        final boolean c = isComplexNumeric(h);
        final Slaw[] cmps = new Slaw[count];
        if (isNumericScalar(h)) {
            for (int i = 0; i < count; i++)
                cmps[i] = readNumber(ni, c, b, f);
        } else {
            final boolean mv = isMultivector(h);
            final int d = numericDimension(h);
            for (int i = 0; i < count; i++)
                cmps[i] = readVector(ni, d, c, mv, b, f);
        }
        return cmps;
    }

    private static Slaw emptyArray(long h, ByteBuffer b, SlawFactory f) {
        final NumericIlk ni = numericIlk(h);
        final boolean c = isComplexNumeric(h);
        SlawIlk i;
        if (isNumericScalar(h)) i = c ? SlawIlk.COMPLEX_ARRAY : SlawIlk.ARRAY;
        else if (isMultivector(h)) i = SlawIlk.MULTI_VECTOR_ARRAY;
        else i = c ? SlawIlk.COMPLEX_VECTOR_ARRAY : SlawIlk.VECTOR_ARRAY;
        return f.array(i, ni, numericDimension(h));
    }

    private static Slaw readVector(NumericIlk ni,
                                   int d,
                                   boolean c,
                                   boolean mv,
                                   ByteBuffer b,
                                   SlawFactory f) throws SlawParseError {
        final int cn = mv ? 1<<d : d;
        final Slaw[] cmps = new Slaw[cn];
        for (int i = 0; i < cn; i++) cmps[i] = readNumber(ni, c, b, f);
        return mv ? f.multivector(cmps) : f.vector(cmps);
    }

    private Slaw internCons(ByteBuffer b, SlawFactory f)
        throws SlawParseError {
        b.position(b.position() + OCT_LEN);
        final Slaw car = internSlaw(b, f);
        final Slaw cdr = internSlaw(b, f);
        return f.cons(car, cdr);
    }

    private Slaw internList(ByteBuffer b, SlawFactory f)
        throws SlawParseError {
        return f.list(readList(b, f));
    }

    private Slaw internMap(ByteBuffer b, SlawFactory f)
        throws SlawParseError {
        return f.map(readList(b, f));
    }

    private List<Slaw> readList(ByteBuffer b, SlawFactory f)
        throws SlawParseError {
        long count = compositeCount(b.getLong());
        if (count > COMPOSITE_THRESHOLD_COUNT) count = b.getLong();
        final List<Slaw> cmps = new ArrayList<Slaw>();
        for (long i = 0; i < count; i++) cmps.add(internSlaw(b, f));
        return cmps;
    }

    private static boolean littleEndian(ByteBuffer b) {
        return b.order() == ByteOrder.LITTLE_ENDIAN;
    }

    private static int weeOffset(ByteBuffer b, int len) {
        return littleEndian(b) ? 0 : OCT_LEN - len;
    }

    private static void checkLength(ByteBuffer b, long len)
        throws SlawParseError {
        if (b.remaining() < len) {
            throw new SlawParseError
                (b, "Buffer too short (" + len + " expected)");
        }
    }

    private static byte peekNibble(ByteBuffer b, int offset) {
        return (byte)(0x0f & (b.get(b.position() + offset)>>>4));
    }
}
