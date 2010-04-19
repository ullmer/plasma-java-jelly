// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

/**
 * Created: Sun Apr 18 01:46:42 2010
 *
 * @author jao
 */
abstract class SlawExternalizer {
    static final SlawExternalizer get() { return current; }
    static final SlawExternalizer set(SlawExternalizer ext) {
        SlawExternalizer r = current;
        current = ext;
        return r;
    }

    abstract byte[] externalize(Slaw s);
    abstract byte[] externalize(SlawBool b);
    abstract byte[] externalize(SlawString s);
    abstract byte[] externalize(SlawNumber n);
    abstract byte[] externalize(SlawComplex c);
    abstract byte[] externalizeNumVector(SlawVector<SlawNumber> v);
    abstract byte[] externalizeComplexVector(SlawVector<SlawComplex> v);
    // abstract byte[] externalize(SlawMultiVector v);
    // abstract byte[] externalize(SlawArray<SlawNumber> a);
    // abstract byte[] externalize(SlawArray<SlawComplex> a);
    // abstract byte[] externalize(SlawArray<SlawVector<SlawNumber>> a);
    // abstract byte[] externalize(SlawArray<SlawVector<SlawComplex>> a);
    // abstract byte[] externalize(SlawArray<SlawMultiVector> a);
    // abstract byte[] externalize(SlawCons m);
    // abstract byte[] externalize(SlawList l);
    // abstract byte[] externalize(SlawMap m);

    private static SlawExternalizer current = new SlawExternalizerV2();
}
