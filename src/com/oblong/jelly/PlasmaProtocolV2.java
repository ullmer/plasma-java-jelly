// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.EnumMap;
import java.util.Map;

import static com.oblong.jelly.NumericIlk.*;
import static com.oblong.jelly.SlawIlk.*;

final class PlasmaProtocolV2 {

    static final byte NUL = 0;

    static int octs(int len) { return len >>> 3; }
    static int roundUp(int len) { return (len + 7) & -8; }

    static final long FALSE_HEADING = 0x20L<<56;
    static final long TRUE_HEADING = FALSE_HEADING | 0x01L;
    static final long NIL_HEADING = FALSE_HEADING | 0x02L;

    static final byte STR_HEADING_BYTE = 0x70;
    static final byte WEE_STR_HEADING_BYTE = 0x30;
    static final int STR_WEE_LEN = 6;
    static final int NUM_WEE_LEN = 4;

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

    static byte compositeHeadingByte(SlawIlk i) {
        return (byte)((int)COMPOSITE_BYTE.get(i));
    }

    static final byte PROTEIN_HEADING_BYTE = 0x10;

    static byte proteinSecondHeadingByte(boolean descrips,
                                         boolean ingests,
                                         int data_len) {
        int res = PROTEIN_SBYTE[descrips ? 1 : 0][ingests ? 1 : 0];
        res |= Math.min(8, data_len);
        return (byte)res;
    }

    private static final byte[][] PROTEIN_SBYTE = {{0x00, 0x20},{0x40, 0x60}};

    private static final long makeNumHeading(NumericIlk i, long d,
                                             boolean c, boolean mv) {
        long head = NUM_BYTE.get(i);
        if (mv) head = head | 0x01;
        if (c) head = head |0x02;
        return (head << 56)
            | ((d - (mv ? 2L : 1L)) << 54)
            | (d * i.bytes() * (c ? 2 : 1) - 1L) << 46;
    }

    private static final void putNumHeading(Map m, NumericIlk i, long d,
                                            boolean c, boolean mv) {
        @SuppressWarnings("unchecked")
        EnumMap<NumericIlk,Long> rm = (EnumMap<NumericIlk,Long>)m;
        rm.put(i, makeNumHeading(i, d, c, mv));
    }

    private static final Map<NumericIlk, Long> NUM_BYTE;
    private static final Map[] NUM_HEADING = new EnumMap[4];
    private static final Map[] COMPLEX_HEADING = new EnumMap[4];
    private static final Map[] MVECT_HEADING = new EnumMap[4];

    private static final long NUM_ARRAY_MASK = 0x40<<56;

    static {
        NUM_BYTE = new EnumMap<NumericIlk, Long>(NumericIlk.class);
        NUM_BYTE.put(INT8, 0x80L);
        NUM_BYTE.put(UNT8, 0x90L);
        NUM_BYTE.put(INT16, 0x84L);
        NUM_BYTE.put(UNT16, 0x94L);
        NUM_BYTE.put(INT32, 0x88L);
        NUM_BYTE.put(UNT32, 0x98L);
        NUM_BYTE.put(INT64, 0x8CL);
        NUM_BYTE.put(UNT64, 0x9CL);
        NUM_BYTE.put(FLOAT32, 0xA8L);
        NUM_BYTE.put(FLOAT64, 0xACL);
        NUM_BYTE.put(NAN, 0x00L);

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
        COMPOSITE_BYTE.put(CONS, 0x60);
        COMPOSITE_BYTE.put(LIST, 0x40);
        COMPOSITE_BYTE.put(MAP, 0x50);
    }

    private PlasmaProtocolV2() {}
}
