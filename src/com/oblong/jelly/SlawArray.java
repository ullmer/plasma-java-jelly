// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.oblong.jelly.SlawIlk.*;

final class SlawArray extends CompositeNumericSlaw {

    static Slaw valueOf(List<Slaw> cmps) {
        assert cmps.size() > 0;
        NumericIlk ni = NumericIlk.dominantIlkForList(cmps);
        assert ni != NumericIlk.NAN;
        return new SlawArray(ni, cmps);
    }

    @Override public SlawIlk ilk() { return ilks.get(nth(0).ilk()); }

    @Override Slaw withNumericIlk(NumericIlk ilk) {
        return (ilk == numericIlk()) ? this : new SlawArray(ilk, elements);
    }

    private SlawArray(NumericIlk i, List<Slaw> l) { super(i, l); }

    static private final Map<SlawIlk,SlawIlk> ilks;
    static {
        ilks = new EnumMap<SlawIlk,SlawIlk>(SlawIlk.class);
        ilks.put(NUMBER, ARRAY);
        ilks.put(COMPLEX, COMPLEX_ARRAY);
        ilks.put(VECTOR, VECTOR_ARRAY);
        ilks.put(COMPLEX_VECTOR, COMPLEX_VECTOR_ARRAY);
        ilks.put(MULTI_VECTOR, MULTI_VECTOR_ARRAY);
    }
}

final class EmptyArray extends Slaw {

    static Slaw valueOf(SlawIlk ilk, NumericIlk ni, int dimension) {
        assert ilk.isNumeric();
        return new EmptyArray(ilk, ni, dimension);
    }

    @Override public SlawIlk ilk() { return ilk; }
    @Override public NumericIlk numericIlk() { return numericIlk; }

    @Override public int dimension() { return dimension; }
    @Override public int count() { return 0; }

    @Override public Slaw nth(int n) {
        throw new IndexOutOfBoundsException("Empty array");
    }

    @Override public Slaw find(Slaw k) { return null; }

    @Override String debugString() { return "{}"; }

    @Override Slaw withNumericIlk(NumericIlk ni) {
        return (ni == numericIlk) ? this : new EmptyArray(ilk, ni, dimension);
    }

    @Override boolean slawEquals(Slaw o) {
        return o.count() == 0 && o.dimension() == dimension;
    }

    @Override public int hashCode() {
        return ilk.hashCode() + 11 * numericIlk.hashCode();
    }

    private EmptyArray(SlawIlk i, NumericIlk ni, int dim) {
        ilk = i;
        numericIlk = ni;
        dimension = dim;
    }

    private final SlawIlk ilk;
    private final NumericIlk numericIlk;
    private final int dimension;
}
