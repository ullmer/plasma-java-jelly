// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oblong.util.Pair;

abstract class NativeAtomicSlaw extends Slaw {

    @Override public NumericIlk numericIlk() { return NumericIlk.NAN; }
    @Override public Slaw withNumericIlk(NumericIlk ilk) { return this; }

    @Override public boolean asBoolean() { return true; }
    @Override public String asString() { return ""; }

    @Override public long asLong() { return 0; }
    @Override public double asDouble() { return 0; }
    @Override public BigInteger asBigInteger() { return BigInteger.ZERO; }

    @Override public final int dimension() { return 0; }
    @Override public final int count() { return 1; }

    @Override public final Slaw head() { return this; }
    @Override public final Slaw tail() { return null; }
                                // ^ NativeSlawList.EMPTY_LIST as cdr
    public final List<Slaw> asList() {
        List<Slaw> result = new ArrayList<Slaw>(1);
        result.add(this);
        return result;
    }

    public Map<Slaw,Slaw> asMap() { return EMPTY_MAP; }

    private static final Map<Slaw,Slaw> EMPTY_MAP =
        Collections.unmodifiableMap(new HashMap<Slaw,Slaw>());

}

final class NativeSlawNil extends NativeAtomicSlaw {

    static final NativeSlawNil INSTANCE = new NativeSlawNil();

    public SlawIlk ilk() { return SlawIlk.NIL; }
    @Override public boolean asBoolean() { return false; }

    boolean equals(Slaw o) { return true; }

    private NativeSlawNil () {}
}

final class NativeSlawBool extends NativeAtomicSlaw {

    static Slaw valueOf(boolean b) { return b ? TRUE : FALSE; }

    public SlawIlk ilk() { return SlawIlk.BOOL; }
    @Override public boolean asBoolean() { return this == TRUE; }

    boolean equals(Slaw o) { return o.asBoolean() == asBoolean(); }

    private NativeSlawBool () {}

    private static final NativeSlawBool TRUE = new NativeSlawBool();
    private static final NativeSlawBool FALSE = new NativeSlawBool();
}

final class NativeSlawString extends NativeAtomicSlaw {

    static Slaw valueOf(String s) {
        return new NativeSlawString(s);
    }

    public SlawIlk ilk() { return SlawIlk.STRING; }
    @Override public String asString() { return val; }

    public boolean equals(Slaw o) { return o.asString() == val; }

    @Override public int hashCode() { return val.hashCode(); }

    private NativeSlawString(String s) { val = s; }
    private final String val;
}
