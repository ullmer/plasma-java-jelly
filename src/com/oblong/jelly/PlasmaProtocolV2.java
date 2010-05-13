// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.EnumMap;
import java.util.Map;

import static com.oblong.jelly.NumericIlk.*;
import static com.oblong.jelly.SlawIlk.*;

final class PlasmaProtocolV2 {

    static int octs(int len) { return len >>> 3; }
    static int roundUp(int len) { return (len + 7) & -8; }

    static long nilHeading() { return NIL_OCT; }

    static long boolHeading(boolean b) { return b ? TRUE_OCT : FALSE_OCT; }

    static byte stringHeadingByte() { return STR_BYTE; }
    static byte weeStringHeadingByte() { return WEE_STR_BYTE; }

    static long numberHeading(NumericIlk ni) {
        return NUM_OCTS.get(ni);
    }

    static long complexHeading(NumericIlk ni) {
        return numberHeading(ni) | COMPLEX_OCT_MASK;
    }

    static long vectorHeading(NumericIlk i, long d) {
        return ((d - 1)<< 54) | numberHeading(i);
    }

    static long complexVectorHeading(NumericIlk i, long d) {
        return vectorHeading(i, d) | COMPLEX_OCT_MASK;
    }

    static long multivectorHeading(NumericIlk i, long d) {
        return ((d - 2)<< 54) | numberHeading(i);
    }

    private static final long FALSE_OCT = 0x20L<<56;
    private static final long TRUE_OCT = FALSE_OCT | 0x01L;
    private static final long NIL_OCT = FALSE_OCT | 0x02L;
    private static final byte STR_BYTE = 0x70;
    private static final byte WEE_STR_BYTE = 0x30;

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

    private PlasmaProtocolV2() {}
}
