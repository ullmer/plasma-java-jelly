// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.java;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIlk;

@Immutable
final class SlawEmptyArray extends JavaSlaw {

    static Slaw valueOf(SlawIlk ilk, NumericIlk ni, int dimension) {
        assert ilk.isNumeric();
        if (ilk == SlawIlk.NUMBER_ARRAY && ni.width() == 8)
            return SlawByteArray.valueOf(ni.isSigned(), new byte[0]);
        return new SlawEmptyArray(ilk, ni, dimension);
    }

    @Override public SlawIlk ilk() { return ilk; }
    @Override public NumericIlk numericIlk() { return numericIlk; }

    @Override public int dimension() { return dimension; }
    @Override public int count() { return 0; }

    @Override public Slaw nth(int n) {
        throw new IndexOutOfBoundsException("Empty array");
    }

    @Override public Slaw find(Slaw k) { return null; }

    @Override public Slaw withNumericIlk(NumericIlk ni) {
        return (ni == numericIlk)
            ? this : new SlawEmptyArray(ilk, ni, dimension);
    }

    @Override public boolean slawEquals(Slaw o) {
        return o.count() == 0 && o.dimension() == dimension;
    }

    @Override public int hashCode() {
        return ilk.hashCode() + 11 * numericIlk.hashCode();
    }

    private SlawEmptyArray(SlawIlk i, NumericIlk ni, int dim) {
        ilk = i;
        numericIlk = ni;
        dimension = dim;
    }

    private final SlawIlk ilk;
    private final NumericIlk numericIlk;
    private final int dimension;
}