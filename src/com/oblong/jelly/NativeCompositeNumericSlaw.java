// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oblong.util.Pair;

/**
 * Created: Tue Apr 27 13:21:27 2010
 *
 * @author jao
 */
abstract class NativeCompositeNumericSlaw extends Slaw {

    NativeCompositeNumericSlaw (NumericIlk ilk, Slaw[] elems) {
        final List<Slaw> es = new ArrayList<Slaw>(elems.length);
        for (Slaw e : elems) es.add(e.withNumericIlk (ilk));
        elements = Collections.unmodifiableList(es);
    }

    @Override public final NumericIlk numericIlk() {
        return elements.get(0).numericIlk();
    }

    @Override public final boolean asBoolean() { return false; }
    @Override public final String asString() { return ""; }

    @Override public final long asLong() { return 0; }
    @Override public double asDouble() { return 0; }
    @Override public final BigInteger asBigInteger() {
        return BigInteger.ZERO;
    }

    @Override public final int count() { return elements.size(); }


    @Override public final Slaw head() { return elements.get(0); }
    @Override public final Slaw tail() {
        return NativeSlawList.valueOf(elements.subList(1, count()));
    }

    @Override public final List<Slaw> asList() { return elements; }

    @Override public final Map<Slaw,Slaw> asMap() {
        final Map<Slaw,Slaw> result = new HashMap<Slaw,Slaw>();
        for (int i = 0; i < keys.length && i < elements.size(); i++)
            result.put(keys[i], elements.get(i));
        for (int i = keys.length; i < elements.size(); i++)
            result.put(makeKey(i), elements.get(i));
        return Collections.unmodifiableMap(result);
    }

    @Override public final boolean equals(Slaw s) {
        return elements.equals(s.asList());
    }

    @Override public int hashCode() { return elements.hashCode(); }

    static NumericIlk commonIlk(Slaw[] slawx) {
        final NumericIlk res = NumericIlk.dominantIlk(slawx);
        if (res != NumericIlk.NAN) {
            final SlawIlk si = slawx[0].ilk();
            for (int i = 1; i < slawx.length; i++)
                if (slawx[i].ilk() != si) return NumericIlk.NAN;
        }
        return res;
    }

    private static final Slaw makeKey(int i) {
        return NativeSlawNumber.valueOf(NumericIlk.INT32, i);
    }

    private final List<Slaw> elements;

    private static final Slaw[] keys = new Slaw[10];
    static {
        for (int i = 0; i < keys.length; i++) keys[i] = makeKey(i);
    }
}
