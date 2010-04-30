// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.Map.Entry;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * Created: Fri Apr 30 02:11:37 2010
 *
 * @author jao
 */
final class NativeSlawMap extends NativeSlawList {
    static Slaw valueOf(Map<Slaw,Slaw> map) {
        List<Slaw> ls = new ArrayList<Slaw>(map.size());
        for (Map.Entry<Slaw,Slaw> e : map.entrySet()) {
            ls.add(NativeSlawCons.valueOf(e.getKey(), e.getValue()));
        }
        return new NativeSlawMap(ls);
    }

    static Slaw valueOf(List<Slaw> e) {
        if (listOfConses(e)) return new NativeSlawMap(e);
        List<Slaw> ls = new ArrayList<Slaw>(e.size() / 2);
        for (int i = 0; i < e.size() - 1; i ++)
            ls.add(NativeSlawCons.valueOf(e.get(i), e.get(i + 1)));
        return new NativeSlawMap(ls);
    }

    @Override public SlawIlk ilk() { return SlawIlk.MAP; }

    private NativeSlawMap(List<Slaw> elems) {
        super(elems);
    }
}
