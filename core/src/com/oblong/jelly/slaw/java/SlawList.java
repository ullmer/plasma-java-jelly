// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.java;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIlk;

import net.jcip.annotations.Immutable;

@Immutable
public final class SlawList extends JavaSlaw {
    static final SlawList EMPTY_LIST =
        new SlawList(new ArrayList<Slaw>(), false);

    static SlawList valueOf(Slaw... sx) {
        if (sx.length == 0) return EMPTY_LIST;
        return new SlawList(sx);
    }

    static Slaw valueOf(List<Slaw> sx, boolean copy) {
        if (sx.size() == 0) return EMPTY_LIST;
        return new SlawList(sx, copy);
    }

    static Slaw valueOf(List<Slaw> sx) {
        return valueOf(sx, true);
    }

    @Override public SlawIlk ilk() { return SlawIlk.LIST; }
    @Override public NumericIlk numericIlk() { return null; }

    @Override public boolean emitBoolean() {
        return count() == 1 ? nth(0).emitBoolean() : super.emitBoolean();
    }
    @Override public String emitString() {
        return count() == 1 ? nth(0).emitString() : super.emitString();
    }
    @Override public long emitLong() {
        return count() == 1 ? nth(0).emitLong() : super.emitLong();
    }
    @Override public double emitDouble() {
        return count() == 1 ? nth(0).emitDouble() : super.emitDouble();
    }
    @Override public BigInteger emitBigInteger() {
        return count() == 1 ?
            nth(0).emitBigInteger() : super.emitBigInteger();
    }

    @Override public Slaw car() { return nth(0); }
    @Override public Slaw cdr() {
        if (elements.isEmpty())
            throw new UnsupportedOperationException("Empty list");
        return new SlawList(elements.subList(1, count()), false);
    }

    @Override public int dimension() { return 0; }
    @Override public int count() { return elements.size(); }

    @Override public Slaw nth(int n) { return elements.get(n); }

    @Override public Slaw find(Slaw k) {
        if (listOfConses(elements)) {
            for (int i = count() - 1; i >= 0; i--) {
                if (k.equals(elements.get(i).car()))
                    return elements.get(i).cdr();
            }
        } else {
            for (int i = count() / 2; i > 0; i--)
                if (k.equals(elements.get(2*i - 2)))
                    return elements.get(2*i - 1);
        }
        return null;
    }

    @Override public Map<Slaw,Slaw> emitMap() {
        final Map<Slaw,Slaw> map = new HashMap<Slaw,Slaw>();
        if (listOfConses(elements)) {
            for (Slaw e : elements) map.put(e.car(), e.cdr());
        } else {
            for (int i = 0, c = count() - 1; i < c; i += 2)
                map.put(elements.get(i), elements.get(i + 1));
        }
        return map;
    }

    @Override public int hashCode() { return elements.hashCode(); }

    @Override public boolean slawEquals(Slaw o) {
        final int c = count();
        if (o.count() != c) return false;
        for (int i = 0; i < c; i++)
            if (!nth(i).equals(o.nth(i))) return false;
        return true;
    }

    SlawList(List<Slaw> elems, boolean copy) {
        elements = copy ? new ArrayList<Slaw>(elems) : elems;
        while (elements.remove(null));
    }

    private SlawList(Slaw... sx) {
        elements = new ArrayList<Slaw>(sx.length);
        for (Slaw s : sx) if (s != null) elements.add(s);
    }

    static boolean listOfConses(List<Slaw> ls) {
        for (Slaw s : ls) if (!s.isCons()) return false;
        return true;
    }

    private final List<Slaw> elements;
}
