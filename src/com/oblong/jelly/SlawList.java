// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created: Wed Apr 28 02:53:36 2010
 *
 * @author jao
 */
class SlawList extends Slaw {
    static final SlawList EMPTY_LIST =
        new SlawList(new ArrayList<Slaw>());

    static Slaw valueOf(Slaw... sx) {
        if (sx.length == 0) return EMPTY_LIST;
        return new SlawList((List<Slaw>)Arrays.asList(sx));
    }

    static Slaw valueOf(List<Slaw> sx) {
        if (sx.size() == 0) return EMPTY_LIST;
        return new SlawList(sx);
    }

    @Override public SlawIlk ilk() { return SlawIlk.LIST; }
    @Override public final NumericIlk numericIlk() { return NumericIlk.NAN; }

    @Override public final boolean asBoolean() { return true; }
    @Override public final String asString() { return ""; }
    @Override public final long asLong() { return 0; }
    @Override public final double asDouble() { return 0; }
    @Override public final BigInteger asBigInteger() {
        return BigInteger.ZERO;
    }

    @Override public final int dimension() { return 0; }
    @Override public final int count() { return elements.size(); }

    @Override public final Slaw head() {
        return elements.isEmpty() ? EMPTY_LIST : elements.get(0);
    }
    @Override public final Slaw tail() {
        return elements.size() > 1
            ? new SlawList(elements.subList(1, count())) : EMPTY_LIST;
    }

    @Override public final List<Slaw> asList() { return elements; }

    @Override public final Map<Slaw,Slaw> asMap() {
        Map<Slaw,Slaw> map = new HashMap<Slaw,Slaw>();
        if (listOfConses(elements)) {
            for (Slaw e : elements) map.put(e.head(), e.tail());
        } else {
            for (int i = 0; i < count() - 1; i++)
                map.put(elements.get(i), elements.get(i + 1));
        }
        return map;
    }

    @Override public final int hashCode() { return elements.hashCode(); }
    @Override boolean equals(Slaw o) {
        return elements.equals(o.asList());
    }

    @Override final Slaw withNumericIlk(NumericIlk ilk) { return this; }

    SlawList(List<Slaw> elems) {
        List<Slaw> e = new ArrayList<Slaw>(elems);
        while (e.remove(null));
        elements = Collections.unmodifiableList(e);
    }

    static final boolean listOfConses(List<Slaw> ls) {
        return ls.isEmpty() || (ls.get(0).is(SlawIlk.CONS)
                                && SlawIlk.haveSameIlk((Slaw[])ls.toArray()));
    }

    private final List<Slaw> elements;
}
