// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.ArrayList;
import java.util.List;

abstract class CompositeNumericSlaw extends Slaw {

    CompositeNumericSlaw(NumericIlk ilk, Slaw[] elems) {
        elements = new ArrayList<Slaw>(elems.length);
        for (Slaw e : elems) elements.add(e.withNumericIlk(ilk));
    }

    CompositeNumericSlaw(NumericIlk ilk, List<Slaw> elems) {
        elements = new ArrayList<Slaw>(elems.size());
        for (Slaw e : elems) elements.add(e.withNumericIlk(ilk));
    }

    @Override public NumericIlk numericIlk() {
        return elements.get(0).numericIlk();
    }

    @Override public final Slaw cdr() {
        return SlawList.valueOf(elements.subList(1, count()));
    }

    @Override public int dimension() { return elements.get(0).dimension(); }
    @Override public final int count() { return elements.size(); }
    @Override public final Slaw nth(int n) { return elements.get(n); }
    @Override public final Slaw find(Slaw k) { return null; }

    @Override public final boolean slawEquals(Slaw s) {
        if (s.count() != elements.size()) return false;
        for (int i = 0, c = elements.size(); i < c; i++)
            if (!elements.get(i).equals(s.nth(i))) return false;
        return true;
    }

    @Override public final int hashCode() { return elements.hashCode(); }

    @Override final String debugString() {
        final StringBuilder buff = new StringBuilder("{");
        for (int i = 0, c = count(); i < c; i++) {
            buff.append(nth(i).debugString());
            if (i + 1 < c) buff.append(", ");
        }
        buff.append("}");
        return buff.toString();
    }

    final List<Slaw> elements;
}