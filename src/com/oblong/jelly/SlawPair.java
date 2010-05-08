// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oblong.util.Pair;

abstract class SlawPair extends Slaw {

    @Override public final int count() { return 2; }

    @Override public final Slaw car() { return pair.first(); }
    @Override public final Slaw cdr() { return pair.second(); }

    @Override public final Pair<Slaw,Slaw> emitPair() { return pair; }

    @Override public final List<Slaw> emitList() {
        List<Slaw> result = new ArrayList<Slaw>(2);
        result.add(pair.first());
        result.add(pair.second());
        return result;
    }

    @Override public final Map<Slaw,Slaw> emitMap() {
        Map<Slaw,Slaw> result = new HashMap<Slaw,Slaw>();
        result.put(car(), cdr());
        return result;
    }

    @Override final boolean equals(Slaw s) {
        return car().equals(s.car()) && cdr().equals(s.cdr());
    }

    @Override public final int hashCode() {
        int reh = car().hashCode();
        int imh = cdr().hashCode();
        return 7 + (reh + 31 * imh);
    }

    SlawPair(Slaw f, Slaw s) { pair = Pair.create(f, s); }

    Pair<Slaw,Slaw> pair;
}
