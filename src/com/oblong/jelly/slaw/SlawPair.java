// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw;

import java.util.HashMap;
import java.util.Map;

import com.oblong.jelly.Slaw;

abstract class SlawPair extends Slaw {

    @Override public Slaw car() { return first; }

    @Override public final Slaw cdr() { return second; }

    @Override public final int count() { return 2; }

    @Override public final Slaw nth(int n) {
        if (n == 0) return first;
        if (n == 1) return second;
        throw new IndexOutOfBoundsException();
    }

    @Override public final Slaw find(Slaw k) {
        return k.equals(first) ? second : null;
    }

    @Override public final Map<Slaw,Slaw> emitMap() {
        Map<Slaw,Slaw> result = new HashMap<Slaw,Slaw>();
        result.put(car(), cdr());
        return result;
    }

    @Override public final boolean slawEquals(Slaw s) {
        return car().equals(s.car()) && cdr().equals(s.cdr());
    }

    @Override public final int hashCode() {
        int reh = car().hashCode();
        int imh = cdr().hashCode();
        return 7 + (reh + 31 * imh);
    }

    SlawPair(Slaw f, Slaw s) {
        first = f;
        second = s;
    }

    private final Slaw first;
    private final Slaw second;
}
