// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.java;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIlk;
import static com.oblong.jelly.SlawIlk.*;

import net.jcip.annotations.Immutable;

@Immutable
final class SlawArray extends CompositeNumericSlaw {

    static Slaw valueOf(List<Slaw> cmps) {
        assert cmps.size() > 0;
        NumericIlk ni = NumericIlk.dominantIlkForList(cmps);
        return new SlawArray(ni, cmps);
    }

    @Override public SlawIlk ilk() { return ilks.get(nth(0).ilk()); }

    @Override public Slaw withNumericIlk(NumericIlk ilk) {
        return (ilk == numericIlk()) ? this : new SlawArray(ilk, elements);
    }

    private SlawArray(NumericIlk i, List<Slaw> l) { super(i, l); }

    static private final Map<SlawIlk,SlawIlk> ilks;
    static {
        ilks = new EnumMap<SlawIlk,SlawIlk>(SlawIlk.class);
        ilks.put(NUMBER, NUMBER_ARRAY);
        ilks.put(COMPLEX, COMPLEX_ARRAY);
        ilks.put(NUMBER_VECTOR, VECTOR_ARRAY);
        ilks.put(COMPLEX_VECTOR, COMPLEX_VECTOR_ARRAY);
        ilks.put(MULTI_VECTOR, MULTI_VECTOR_ARRAY);
    }
}
