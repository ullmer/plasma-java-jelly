// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * Created: Mon Apr 19 02:26:18 2010
 *
 * @author jao
 */
abstract class NativeSlawVector extends Slaw {

    BaseSlawVector(NumericIlk ilk, E[] elems) {
        assert elems != null;
        assert elems.length > 1 && elems.length < 5;
        List<E> es = new ArrayList<E>(elems.length);
        for (E e : elems) {
            @SuppressWarnings("unchecked") E ne = (E)e.withIlk(ilk);
            es.add(ne);
        }
        this.elements = Collections.unmodifiableList(es);
    }

    // Implementation of com.oblong.jelly.NumericSlaw

    public final NumericIlk numericIlk() {
        return elements.get(0).numericIlk();
    }

    // Implementation of com.oblong.jelly.SlawVector

    public final E get(int n) { return this.elements.get(n); }
    public final List<E> asList() { return this.elements; }
    public final int dimension() { return this.elements.size(); }

    // Implementation of com.oblong.jelly.Slaw

    public final int hashCode() { return this.elements.hashCode(); }
    public final  boolean isNumeric() { return true; }

    @SuppressWarnings("unchecked")
    E[] toArray () { return (E[])this.elements.toArray(); }

    private final List<E> elements;
}

final class NativeNumberSlawVector extends BaseSlawVector<SlawNumber>
    implements SlawNumberVector {

    static SlawNumberVector valueOf(SlawNumber... l) {
        return new NativeNumberSlawVector(NumericIlk.dominantIlk(l), l);
    }

    @Override public boolean equals(Slaw slaw) {
        if (!(slaw instanceof SlawNumberVector)) return false;
        SlawNumberVector sv = (SlawNumberVector) slaw;
        return asList().equals(sv.asList());
    }

    @Override public final byte[] externalize(SlawExternalizer e) {
        return e.externalize(this);
    }

    @Override public boolean isNumberVector() { return true; }

    @Override public NumericSlaw withIlk(NumericIlk ilk) {
        return new NativeNumberSlawVector(ilk, toArray());
    }

    @Override public SlawNumberVector numberVector() { return this; }

    private NativeNumberSlawVector(NumericIlk i, SlawNumber... l) {
        super(i, l);
    }
}

final class NativeComplexSlawVector extends BaseSlawVector<SlawComplex>
    implements SlawComplexVector {

    static SlawComplexVector valueOf(SlawComplex... l) {
        return new NativeComplexSlawVector(NumericIlk.dominantIlk(l), l);
    }

    @Override public boolean equals(Slaw slaw) {
        if (!(slaw instanceof SlawComplexVector)) return false;
        SlawComplexVector sv = (SlawComplexVector) slaw;
        return asList().equals(sv.asList());
    }

    @Override public final byte[] externalize(SlawExternalizer e) {
        return e.externalize(this);
    }

    @Override public boolean isComplexVector() { return true; }

    @Override public NumericSlaw withIlk(NumericIlk ilk) {
        return new NativeComplexSlawVector(ilk, toArray());
    }

    @Override public SlawComplexVector complexVector() { return this; }

    private NativeComplexSlawVector(NumericIlk i, SlawComplex... l) {
        super(i, l);
    }
}
