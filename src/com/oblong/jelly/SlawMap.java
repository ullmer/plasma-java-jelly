// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

final class SlawMap extends Slaw {

    static Slaw valueOf(Map<Slaw,Slaw> map) {
        List<Slaw> ls = new ArrayList<Slaw>(map.size());
        for (Map.Entry<Slaw,Slaw> e : map.entrySet()) {
            Slaw key = e.getKey();
            Slaw value = e.getValue();
            if (key != null && value != null)
                ls.add(SlawCons.valueOf(key, value));
        }
        return new SlawMap(ls);
    }

    static Slaw valueOf(List<Slaw> e) {
        return new SlawMap(
            SlawList.listOfConses(e) ? filterConses(e) : listToConses(e));
    }

    @Override public SlawIlk ilk() { return SlawIlk.MAP; }
    @Override public NumericIlk numericIlk() { return NumericIlk.NAN; }

    @Override public int count() { return conses.count(); }
    @Override public Slaw nth(int n) { return conses.nth(n); }
    @Override public Slaw find(Slaw k) {
        for (int i = 0, c = count(); i < c; i++)
            if (k.equals(nth(i).car())) return nth(i).cdr();
        return null;
    }

    @Override public Slaw car() { return conses.car(); }
    @Override public Slaw cdr() { return conses.cdr(); }
    @Override public Map<Slaw,Slaw> emitMap() { return conses.emitMap(); }

    @Override String debugString() {
        final StringBuilder buff = new StringBuilder("{");
        for (int i = 0, c = count(); i < c; i++) {
            buff.append(nth(i).debugString());
            if (i + 1 < c) buff.append(" ");
        }
        buff.append("}");
        return buff.toString();
    }

    @Override boolean slawEquals(Slaw o) {
        if (o.count() != conses.count()) return false;
        for (int i = 0, c = count(); i < c; i++)
            if (!o.find(nth(i).car()).equals(nth(i).cdr())) return false;
        return true;
    }

    @Override public int hashCode() { return 3 + 11 * conses.hashCode(); }

    private SlawMap(List<Slaw> elems) {
        conses = new SlawList(elems, false);
    }

    private static List<Slaw> filterConses(List<Slaw> e) {
        List<Slaw> conses = new ArrayList<Slaw>(e.size());
        List<Slaw> keys = new ArrayList<Slaw>(e.size());
        for (Slaw s : e) {
            if (s.car() != null && s.cdr() != null
                && !keys.contains(s.car())) {
                keys.add(s.car());
                conses.add(s);
            }
        }
        return conses;
    }

    private static List<Slaw> listToConses(List<Slaw> e) {
        List<Slaw> conses = new ArrayList<Slaw>(e.size() / 2);
        List<Slaw> keys = new ArrayList<Slaw>(e.size() / 2);
        for (int i = 0; i < e.size() - 1; i ++) {
            Slaw key = e.get(i);
            Slaw value = e.get(i+1);
            if (key != null && value != null && !keys.contains(key)) {
                keys.add(key);
                conses.add(SlawCons.valueOf(key, value));
            }
        }
        return conses;
    }

    private final SlawList conses;
}
