// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oblong.util.Pair;

/**
 * Describe class NativeSlawComplex here.
 *
 *
 * Created: Mon Apr 19 01:57:03 2010
 *
 * @author jao
 */
final class NativeSlawComplex extends Slaw {

    static Slaw valueOf(Slaw r, Slaw i) {
        if (r.is(SlawIlk.NUMBER) && i.is(SlawIlk.NUMBER)) {
            NumericIlk ilk =
                NumericIlk.dominantIlk(r.numericIlk(), i.numericIlk());
            return new NativeSlawComplex(ilk, r, i);
        }
        return ZERO;
    }

    public SlawIlk ilk() { return SlawIlk.COMPLEX; }
    public NumericIlk numericIlk() { return im.numericIlk(); }

    public boolean asBoolean() { return true; }
    public String asString() { return ""; }

    public long asLong() { return re.asLong(); }
    public double asDouble() { return re.asDouble(); }
    public BigInteger asBigInteger() { return re.asBigInteger(); }

    public int dimension() { return 0; }
    public int count() { return 2; }

    public Pair<Slaw,Slaw> asPair() { return Pair.create(re, im); }
    public List<Slaw> asList() {
        List<Slaw> result = new ArrayList<Slaw>(2);
        result.add(re);
        result.add(im);
        return result;
    }
    public Map<Slaw,Slaw> asMap() {
        Map<Slaw,Slaw> result = new HashMap<Slaw,Slaw>();
        result.put(re, im);
        return result;
    }

    Slaw withNumericIlk(NumericIlk ilk) {
        if (numericIlk() == ilk) return this;
        return new NativeSlawComplex(ilk, re, im);
    }

    boolean equals(Slaw slaw) {
        Pair<Slaw,Slaw> p = slaw.asPair();
        return re.equals(p.first) && im.equals(p.second);
    }

    @Override public int hashCode() {
        int reh = re.hashCode();
        int imh = im.hashCode();
        return 27 + (reh + 31 * imh);
    }

    private NativeSlawComplex(NumericIlk ilk, Slaw r, Slaw i) {
        re = r.withNumericIlk(ilk);
        im = i.withNumericIlk(ilk);
    }

    private final Slaw re;
    private final Slaw im;

    private static final Slaw ZERO =
        valueOf(NativeSlawNumber.valueOf(NumericIlk.INT8, 0),
                NativeSlawNumber.valueOf(NumericIlk.INT8, 0));
}
