// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.oblong.jelly.NumericSlaw.Ilk;


/**
 *
 * Created: Mon Apr 19 02:26:18 2010
 *
 * @author jao
 */
abstract class BaseSlawVector<E extends SlawComplex> extends AbstractSlaw {

    BaseSlawVector(Ilk ilk, List<E> elems) {
        assert elems != null;
        assert elems.size() > 1 && elems.size() < 5;
        List<E> es = new ArrayList<E>(elems.size());
        for (E e : elems) {
            @SuppressWarnings("unchecked") E ne = (E)e.withIlk(ilk);
            es.add(ne);
        }
        this.elements = Collections.unmodifiableList(es);
    }

    // Implementation of com.oblong.jelly.NumericSlaw

    public final Ilk ilk() { return get(0).ilk(); }

    // Implementation of com.oblong.jelly.SlawVector

    public final E get(int n) { return this.elements.get(n); }
    public final List<E> asList() { return this.elements; }
    public final int dimension() { return this.elements.size(); }

    // Implementation of com.oblong.jelly.Slaw

    public final int hashCode() { return this.elements.hashCode(); }
    public final  boolean isNumeric() { return true; }

    private final List<E> elements;
}

final class NativeNumberSlawVector extends BaseSlawVector<SlawNumber>
    implements SlawNumberVector {

    static SlawNumberVector valueOf(Ilk ilk, List<SlawNumber> l) {
        return new NativeNumberSlawVector(ilk, l);
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

    @Override public NumericSlaw withIlk(Ilk ilk) {
        return new NativeNumberSlawVector(ilk, asList());
    }

    @Override public SlawNumberVector numberVector() { return this; }

    private NativeNumberSlawVector(Ilk ilk, List<SlawNumber> l) {
        super(ilk, l);
    }
}

final class NativeComplexSlawVector extends BaseSlawVector<SlawComplex>
    implements SlawComplexVector {

    static SlawComplexVector valueOf(Ilk ilk, List<SlawComplex> l) {
        return new NativeComplexSlawVector(ilk, l);
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

    @Override public NumericSlaw withIlk(Ilk ilk) {
        return new NativeComplexSlawVector(ilk, asList());
    }

    @Override public SlawComplexVector complexVector() { return this; }

    private NativeComplexSlawVector(Ilk ilk, List<SlawComplex> l) {
        super(ilk, l);
    }
}
