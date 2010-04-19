// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

/**
 * Describe class NativeSlawComplex here.
 *
 *
 * Created: Mon Apr 19 01:57:03 2010
 *
 * @author jao
 */
final class NativeSlawComplex extends AbstractSlaw implements SlawComplex {

    static SlawComplex valueOf(SlawNumber r, SlawNumber i) {
        return new NativeSlawComplex(r, i);
    }

    // Implementation of com.oblong.jelly.NumericSlaw

    @Override public NumericSlaw withIlk(Ilk ilk) {
        return valueOf(this.re.withIlk(ilk), this.im.withIlk(ilk));
    }

    @Override public Ilk ilk() {
        return this.im.ilk();
    }

    // Implementation of com.oblong.jelly.ExternalizableSlaw

    @Override public byte[] externalize(SlawExternalizer e) {
        return e.externalize(this);
    }

    // Implementation of com.oblong.jelly.SlawComplex

    @Override public SlawNumber re() { return this.re; }
    @Override public SlawNumber im() { return this.im; }

    // Implementation of com.oblong.jelly.Slaw

    @Override public boolean equals(Slaw slaw) {
        if (!(slaw instanceof SlawComplex)) return false;
        SlawComplex o = (SlawComplex)slaw;
        return this.re.equals(o.re()) && this.im.equals(o.im());
    }

    @Override public int hashCode() {
        int reh = this.re.hashCode();
        int imh = this.im.hashCode();
        return 27 + (reh + 31 * imh);
    }

    @Override public boolean isNumeric() { return true; }
    @Override public boolean isComplex() { return true; }
    @Override public SlawComplex complex() { return this; }

    private NativeSlawComplex(SlawNumber r, SlawNumber i) {
        assert r.ilk() == i.ilk();
        this.re = r;
        this.im = i;
    }

    private final SlawNumber re;
    private final SlawNumber im;
}
