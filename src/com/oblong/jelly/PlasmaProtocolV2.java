// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;


import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static com.oblong.jelly.NumericIlk.*;
import static com.oblong.jelly.SlawIlk.*;

final class PlasmaProtocolV2 {

    static final byte NUL = 0;
    static final int OCT_LEN = 8;

    static int octs(int len) { return len >>> 3; }
    static int roundUp(int len) { return (len + 7) & -8; }

    static final byte ATOM_NIBBLE = 0x02;
    static final long FALSE_HEADING = ((long)ATOM_NIBBLE)<<60;
    static final long TRUE_HEADING = FALSE_HEADING | 0x01L;
    static final long NIL_HEADING = FALSE_HEADING | 0x02L;

    static final byte STR_NIBBLE = 0x7;
    static final byte WEE_STR_NIBBLE = 0x3;
    static final byte STR_HEADING_BYTE = STR_NIBBLE<<4;
    static final byte WEE_STR_HEADING_BYTE = WEE_STR_NIBBLE<<4;
    static final int STR_WEE_LEN = 6;

    static final int weeStringLength(byte hb) { return hb & 0x0f; }
    static final int stringLength(long hb) { return (int)((hb<<8)>>>8); }
    static final int stringPadding(long hb) { return (int)((hb>>>56)&0x07); }

    static final byte INT_NIBBLE = 0x08;
    static final byte UNT_NIBBLE = 0x09;
    static final byte FLOAT_NIBBLE = 0x0a;
    static final int NUM_WEE_LEN = 4;

    static NumericIlk numericIlk(long h) {
        return NUM_ILK.get((h>>>56) & 0xfcL);
    }

    static boolean isNumericScalar(long h) {
        return numericKind(h) == 0;
    }

    static boolean isComplexNumeric(long h) {
        return (h & (0x1L<<57)) != 0;
    }

    static boolean isNumericVector(long h) {
        final long nk = numericKind(h);
        return nk > 0 && nk < 4;
    }

    static boolean isMultivector(long h) {
        return numericKind(h) > 4;
    }

    static int numericDimension(long h) {
        final int nk = numericKind(h);
        return nk < 4 ? nk + 1 : nk - 2;
    }

    static int numericBytes(long h) {
        if (isMultivector(h))
            return numericByteSize(h) * (1<<numericDimension(h));
        final int b = numericByteSize(h) * numericDimension(h);
        return isComplexNumeric(h) ? b<<1 : b;
    }

    private static int numericKind(long h) {
        return (int)((h>>>54) & 7L);
    }

    private static int numericByteSize(long h) {
        return 1 + (int)((h>>>46) & 0xffL);
    }

    static long numberHeading(NumericIlk ni) {
        return (Long)NUM_HEADING[0].get(ni);
    }

    static long complexHeading(NumericIlk ni) {
        return (Long)COMPLEX_HEADING[0].get(ni);
    }

    static long vectorHeading(NumericIlk ni, int d) {
        assert d > 1 && d < 5;
        return (Long)NUM_HEADING[d - 1].get(ni);
    }

    static long complexVectorHeading(NumericIlk ni, int d) {
        assert d > 1 && d < 5;
        return (Long)COMPLEX_HEADING[d - 1].get(ni);
    }

    static long multivectorHeading(NumericIlk ni, int d) {
        assert d > 1 && d < 6;
        return (Long)MVECT_HEADING[d - 2].get(ni);
    }

    static long arrayHeading(NumericIlk ni) {
        return numberHeading(ni) | NUM_ARRAY_MASK;
    }

    static long complexArrayHeading(NumericIlk ni) {
        return complexHeading(ni) | NUM_ARRAY_MASK;
    }

    static long vectorArrayHeading(NumericIlk ni, int d) {
        return vectorHeading(ni, d) | NUM_ARRAY_MASK;
    }

    static long complexVectorArrayHeading(NumericIlk ni, int d) {
        return complexVectorHeading(ni, d) | NUM_ARRAY_MASK;
    }

    static long multivectorArrayHeading(NumericIlk ni, int d) {
        return multivectorHeading(ni, d) | NUM_ARRAY_MASK;
    }

    static final byte CONS_NIBBLE = 0x6;
    static final byte LIST_NIBBLE = 0x4;
    static final byte MAP_NIBBLE = 0x5;
    static final int COMPOSITE_THRESHOLD_COUNT = 14;

    static final int compositeCount(long h) {
        return (int)((h>>>56) & 0x0f);
    }

    static byte compositeHeadingByte(SlawIlk i) {
        return (byte)((int)COMPOSITE_BYTE.get(i));
    }

    static final byte PROTEIN_NATIVE_NIBBLE = 0x1;
    static final byte PROTEIN_NON_NATIVE_NIBBLE = 0x0;
    static final byte PROTEIN_HEADING_BYTE = PROTEIN_NATIVE_NIBBLE<<4;
    static final byte PROTEIN_NN_HEADING_BYTE = PROTEIN_NON_NATIVE_NIBBLE<<4;
    static final int WEE_PROTEIN_DATA_LEN = 14;

    static long proteinHeading(long octs) {
        return (PROTEIN_HEADING_BYTE << 56) | (octs<<4) | (octs & 0x0fL);
    }

    static long proteinLength(long hd) {
        return ((hd<<4)>>>8) + (hd & 0x0fL);
    }

    static byte proteinSecondHeadingByte(boolean descrips,
                                         boolean ingests,
                                         int data_len) {
        int res = PROTEIN_SBYTE[descrips ? 1 : 0][ingests ? 1 : 0];
        res |= Math.min(8, data_len);
        return (byte)res;
    }

    static boolean proteinHasDescrips(long sh) {
        final byte sb = proteinSByte(sh);
        return sb == PROTEIN_SBYTE[1][0] || sb == PROTEIN_SBYTE[1][1];
    }

    static boolean proteinHasIngests(long sh) {
        final byte sb = proteinSByte(sh);
        return sb == PROTEIN_SBYTE[0][1] || sb == PROTEIN_SBYTE[1][1];
    }

    static boolean proteinHasWeeData(long sh) {
        return ((sh>>>59) & 0x01L) == 1L;
    }

    static long proteinDataLen(long sh) {
        return proteinHasWeeData(sh) ? (sh>>>56) & 7L : (sh<<4)>>>4;
    }

    private static byte proteinSByte(long sh) {
        return (byte)((sh>>>56) & 0xc0);
    }

    private static final byte[][] PROTEIN_SBYTE = {{0x00, 0x20},{0x40, 0x60}};

    private static final long makeNumHeading(NumericIlk i, long d,
                                             boolean c, boolean mv) {
        long head = NUM_BYTE.get(i);
        if (mv) head = head | 0x01;
        if (c) head = head |0x02;
        return (head << 56)
            | ((d - (mv ? 2L : 1L)) << 54)
            | ((mv ? 1<<d : d) * i.bytes() * (c ? 2 : 1) - 1L) << 46;
    }

    private static final void putNumHeading(Map m, NumericIlk i, long d,
                                            boolean c, boolean mv) {
        @SuppressWarnings("unchecked")
        EnumMap<NumericIlk,Long> rm = (EnumMap<NumericIlk,Long>)m;
        rm.put(i, makeNumHeading(i, d, c, mv));
    }

    private static final Map<NumericIlk, Long> NUM_BYTE;
    private static final Map<Long, NumericIlk> NUM_ILK;
    private static final Map[] NUM_HEADING = new EnumMap[4];
    private static final Map[] COMPLEX_HEADING = new EnumMap[4];
    private static final Map[] MVECT_HEADING = new EnumMap[4];

    private static final long NUM_ARRAY_MASK = 0x3L<<62;

    static {
        final long intl = ((int)INT_NIBBLE)<<4;
        final long untl = ((int)UNT_NIBBLE)<<4;
        final long fll = ((int)FLOAT_NIBBLE)<<4;
        NUM_BYTE = new EnumMap<NumericIlk, Long>(NumericIlk.class);
        NUM_BYTE.put(INT8, intl);
        NUM_BYTE.put(UNT8, untl);
        NUM_BYTE.put(INT16, intl + 4);
        NUM_BYTE.put(UNT16, untl + 4);
        NUM_BYTE.put(INT32, intl + 8);
        NUM_BYTE.put(UNT32, untl + 8);
        NUM_BYTE.put(INT64, intl + 12);
        NUM_BYTE.put(UNT64, untl + 12);
        NUM_BYTE.put(FLOAT32, fll + 8);
        NUM_BYTE.put(FLOAT64, fll + 12);

        NUM_ILK = new HashMap<Long, NumericIlk>();
        for (Map.Entry<NumericIlk, Long> e : NUM_BYTE.entrySet())
            NUM_ILK.put(e.getValue(), e.getKey());

        for (int d = 0; d < 4; d++) {
            NUM_HEADING[d] = new EnumMap<NumericIlk, Long>(NumericIlk.class);
            COMPLEX_HEADING[d] =
                new EnumMap<NumericIlk, Long>(NumericIlk.class);
            MVECT_HEADING[d] =
                new EnumMap<NumericIlk, Long>(NumericIlk.class);
            for (NumericIlk ni : NumericIlk.values()) {
                putNumHeading(NUM_HEADING[d], ni, d+1, false, false);
                putNumHeading(COMPLEX_HEADING[d], ni, d+1, true, false);
                putNumHeading(MVECT_HEADING[d], ni, d+2, false, true);
            }
        }
    }

    private static final Map<SlawIlk, Integer> COMPOSITE_BYTE;
    static {
        COMPOSITE_BYTE = new EnumMap<SlawIlk, Integer>(SlawIlk.class);
        COMPOSITE_BYTE.put(CONS, CONS_NIBBLE<<4);
        COMPOSITE_BYTE.put(LIST, LIST_NIBBLE<<4);
        COMPOSITE_BYTE.put(MAP, MAP_NIBBLE<<4);
    }

    private PlasmaProtocolV2() {}
}
