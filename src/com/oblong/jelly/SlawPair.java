// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oblong.util.Pair;

abstract class SlawPair extends Slaw {

    @Override public final boolean asBoolean() { return true; }
    @Override public final String asString() { return ""; }

    @Override public final int dimension() { return 0; }
    @Override public final int count() { return 2; }

    @Override public final Slaw head() { return first; }
    @Override public final Slaw tail() { return second; }

    @Override public final List<Slaw> asList() {
        List<Slaw> result = new ArrayList<Slaw>(2);
        result.add(first);
        result.add(second);
        return result;
    }

    @Override public final Map<Slaw,Slaw> asMap() {
        Map<Slaw,Slaw> result = new HashMap<Slaw,Slaw>();
        result.put(first, second);
        return result;
    }

    @Override boolean equals(Slaw s) {
        return first.equals(s.head()) && second.equals(s.tail());
    }

    @Override public int hashCode() {
        int reh = first.hashCode();
        int imh = second.hashCode();
        return 27 + (reh + 31 * imh);
    }

    SlawPair(Slaw f, Slaw s) {
        first = f;
        second = s;
    }

    final Slaw first;
    final Slaw second;
}
