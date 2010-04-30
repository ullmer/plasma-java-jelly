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
final class SlawMap extends SlawList {
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
        if (listOfConses(e)) return new SlawMap(e);
        List<Slaw> ls = new ArrayList<Slaw>(e.size() / 2);
        for (int i = 0; i < e.size() - 1; i ++) {
            Slaw key = e.get(i);
            Slaw value = e.get(i+1);
            if (key != null && value != null)
                ls.add(SlawCons.valueOf(key, value));
        }
        return new SlawMap(ls);
    }

    @Override public SlawIlk ilk() { return SlawIlk.MAP; }

    private SlawMap(List<Slaw> elems) {
        super(elems);
    }
}
