// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIlk;
import com.oblong.jelly.util.ByteReader;

import static com.oblong.jelly.slaw.V2Protocol.*;

@Immutable
public
final class V2Internalizer implements SlawInternalizer {

    @Override public Protein internProtein(InputStream s, SlawFactory f)
        throws SlawParseError, IOException {
        ByteReader b = new ByteReader(s, OCT_LEN);
        final byte hn = peekNibble(b, 0);
        if (hn == PROTEIN_NON_NATIVE_NIBBLE) {
            b.setLittleEndian();
        } else if (hn != PROTEIN_NATIVE_NIBBLE) {
            throw new SlawParseError(0, "Not a protein: nibble was " + hn);
        }
        return internProtein(b, f);
    }

    @Override public Slaw internSlaw(InputStream s, SlawFactory f)
        throws SlawParseError, IOException {
        ByteReader b = new ByteReader(s, OCT_LEN);
        return internSlaw(b, f);
    }

    private static Protein internProtein(ByteReader b, SlawFactory f)
        throws SlawParseError, IOException {
        b.skip(OCT_LEN);
        final long sh = b.getLong();
        final Slaw descrips =
            proteinHasDescrips(sh) ? internSlaw(b, f) : null;
        final Slaw ingests =
            proteinHasIngests(sh) ? internSlaw(b, f) : null;
        final int dataLen = (int)(proteinDataLen(sh));
        if (dataLen < 0)
            throw new SlawParseError(b.bytesSeen(),
                                     "Invalid data length: " + dataLen);
        final byte[] data = new byte[dataLen];
        if (dataLen > 0) {
            final boolean wee = proteinHasWeeData(sh);
            if (wee) weeBytes(sh, data, b.isLittleEndian());
            else align(b.get(data, dataLen));
        }
        return f.protein(descrips, ingests, data);
    }

    private static Slaw internSlaw(ByteReader b, SlawFactory f)
        throws SlawParseError, IOException {
        final Slaw s =
            readSlaw(peekNibble(b, b.isLittleEndian() ? 7 : 0), b, f);
        align(b);
        return s;
    }

    private static Slaw readSlaw(byte nb, ByteReader b, SlawFactory f)
        throws SlawParseError, IOException {
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
                (b.bytesSeen(), "Unrecognized format (" + nb + ")");
        }
    }

    private static Slaw internAtom(ByteReader b, SlawFactory f)
        throws SlawParseError, IOException {
        final long h = b.getLong();
        if (h == NIL_HEADING) return f.nil();
        if (h == TRUE_HEADING) return f.bool(true);
        if (h == FALSE_HEADING) return f.bool(false);
        throw new SlawParseError(b.bytesSeen(), "Invalid atom (" + h + ")");
    }

    private static Slaw internWeeString(ByteReader b, SlawFactory f)
        throws SlawParseError, IOException {
        final byte hb = b.peek(b.isLittleEndian() ? 7 : 0);
        final int len = weeStringLength(hb) - 1;
        if (len < 0)
            throw new SlawParseError(b.bytesSeen(),
                                     "Invalid wee string length: " + len);
        if (len == 0) return f.string("");
        final byte[] bs = new byte[len];
        b.skip(weeOffset(b, len + 1)).get(bs, len);
        return f.string(makeString(bs));
    }

    private static Slaw internString(ByteReader b, SlawFactory f)
        throws SlawParseError, IOException {
        final long h = b.getLong();
        final int len = stringLength(h) - 1;
        final byte[] bs = new byte[len];
        b.get(bs, len);
        b.get(); // consuming trailing null 
        return f.string(makeString(bs));
    }

    private static String makeString(byte[] bs) throws SlawParseError {
        try {
            return new String(bs, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new SlawParseError(0, "UTF-8 not supported");
        }
    }

    private static Slaw internNumeric(ByteReader b, SlawFactory f)
        throws SlawParseError, IOException {
        final long h = b.peekLong();
        final int bs = numericBytes(h);
        final boolean isWee = bs <= NUM_WEE_LEN;
        b.skip(isWee ? weeOffset(b, bs) : OCT_LEN);
        Slaw res = isNumericScalar(h) ?
            internNum(h, b, f) : internVector(h, b, f);
        return res;
    }

    private static Slaw internNum(long h, ByteReader b, SlawFactory f)
        throws SlawParseError, IOException {
        return readNumber(numericIlk(h), isComplexNumeric(h), b, f);
    }

    private static Slaw readNumber(NumericIlk ni,
                                   boolean c,
                                   ByteReader b,
                                   SlawFactory f)
        throws SlawParseError, IOException {
        if (c) {
            final Slaw re = readScalar(ni, b, f);
            final Slaw im = readScalar(ni, b, f);
            return f.complex(re, im);
        } else {
            return readScalar(ni, b, f);
        }
    }

    private static Slaw readScalar(NumericIlk ni, ByteReader b, SlawFactory f)
        throws SlawParseError, IOException {
        final int bs = ni.bytes();
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

    private static Slaw internVector(long h, ByteReader b, SlawFactory f)
        throws SlawParseError, IOException {
        final NumericIlk ni = numericIlk(h);
        final boolean mv = isMultivector(h);
        final boolean c = !mv && isComplexNumeric(h);
        final int d = numericDimension(h);
        return readVector(ni, d, c, mv, b, f);
    }

    private static Slaw internArray(ByteReader b, SlawFactory f)
        throws SlawParseError, IOException {
        final long h = b.getLong();
        final int count = (int)arrayBreadth(h);
        if (count == 0) return emptyArray(h, b, f);
        Slaw[] cmps = readArray(h, count, b, f);
        return f.array(cmps);
    }

    private static Slaw[] readArray(long h, int count,
                                    ByteReader b, SlawFactory f)
        throws SlawParseError, IOException {
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

    private static Slaw emptyArray(long h, ByteReader b, SlawFactory f) {
        final NumericIlk ni = numericIlk(h);
        final boolean c = isComplexNumeric(h);
        SlawIlk i;
        if (isNumericScalar(h)) i = c ? SlawIlk.COMPLEX_ARRAY : SlawIlk.NUMBER_ARRAY;
        else if (isMultivector(h)) i = SlawIlk.MULTI_VECTOR_ARRAY;
        else i = c ? SlawIlk.COMPLEX_VECTOR_ARRAY : SlawIlk.VECTOR_ARRAY;
        return f.array(i, ni, numericDimension(h));
    }

    private static Slaw readVector(NumericIlk ni,
                                   int d,
                                   boolean c,
                                   boolean mv,
                                   ByteReader b,
                                   SlawFactory f)
        throws SlawParseError, IOException {
        final int cn = mv ? 1<<d : d;
        final Slaw[] cmps = new Slaw[cn];
        for (int i = 0; i < cn; i++) cmps[i] = readNumber(ni, c, b, f);
        return mv ? f.multivector(cmps) : f.vector(cmps);
    }

    private static Slaw internCons(ByteReader b, SlawFactory f)
        throws SlawParseError, IOException {
        b.skip(OCT_LEN);
        final Slaw car = internSlaw(b, f);
        final Slaw cdr = internSlaw(b, f);
        return f.cons(car, cdr);
    }

    private static Slaw internList(ByteReader b, SlawFactory f)
        throws SlawParseError, IOException {
        return f.list(readList(b, f));
    }

    private static Slaw internMap(ByteReader b, SlawFactory f)
        throws SlawParseError, IOException {
        return f.map(readList(b, f));
    }

    private static List<Slaw> readList(ByteReader b, SlawFactory f)
        throws SlawParseError, IOException {
        long count = compositeCount(b.getLong());
        if (count > COMPOSITE_THRESHOLD_COUNT) count = b.getLong();
        final List<Slaw> cmps = new ArrayList<Slaw>();
        for (long i = 0; i < count; ++i) cmps.add(internSlaw(b, f));
        return cmps;
    }

    private static int weeOffset(ByteReader b, int len) {
        return b.isLittleEndian() ? 0 : OCT_LEN - len;
    }

    private static byte peekNibble(ByteReader b, int offset)
        throws IOException {
        return (byte)(0x0f & (b.peek(offset)>>>4));
    }

    private static void align(ByteReader b) throws IOException {
        b.skipToBoundary(OCT_LEN);
    }
}
