// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.EnumMap;

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

    final byte[] extern(Slaw s) {
        return externalizers.get(s.ilk()).extern(this, s);
    }

    final int externSize(Slaw s) {
        return externalizers.get(s.ilk()).externSize(this, s);
    }

    abstract byte[] externNil(Slaw s);
    abstract int nilExternSize(Slaw s);

    abstract byte[] externBool(Slaw b);
    abstract int boolExternSize(Slaw b);

    abstract byte[] externString(Slaw s);
    abstract int stringExternSize(Slaw s);

    abstract byte[] externNumber(Slaw n);
    abstract int numberExternSize(Slaw n);

    abstract byte[] externComplex(Slaw c);
    abstract int complexExternSize(Slaw c);

    abstract byte[] externVector(Slaw v);
    abstract int vectorExternSize(Slaw v);

    abstract byte[] externComplexVector(Slaw v);
    abstract int complexVectorExternSize(Slaw v);

    abstract byte[] externMultiVector(Slaw v);
    abstract int multiVectorExternSize(Slaw v);
    // abstract byte[] externalize(SlawArray<SlawNumber> a);
    // abstract byte[] externalize(SlawArray<SlawComplex> a);
    // abstract byte[] externalize(SlawArray<SlawVector<SlawNumber>> a);
    // abstract byte[] externalize(SlawArray<SlawVector<SlawComplex>> a);
    // abstract byte[] externalize(SlawArray<SlawMultiVector> a);
    // abstract byte[] externalize(SlawCons m);
    // abstract byte[] externalize(SlawList l);
    // abstract byte[] externalize(SlawMap m);

    private interface Externalizer {
        byte[] extern(SlawExternalizer ext, Slaw s);
        int externSize(SlawExternalizer ext, Slaw s);
    }

    private static final Map<SlawIlk,Externalizer> externalizers;
    static {
        externalizers = new EnumMap<SlawIlk,Externalizer>(SlawIlk.class);
        externalizers.put(NIL, new Externalizer {
                public byte[] extern(SlawExternalizer ext, Slaw s) {
                    return ext.externNil(s);
                }
                public int externSize(SlawExternalizer ext, Slaw s) {
                    return ext.nilExternSize(s);
                }
            });
        externalizers.put(BOOL, new Externalizer {
                public byte[] extern(SlawExternalizer ext, Slaw s) {
                    return ext.externBool(s);
                }
                public int externSize(SlawExternalizer ext, Slaw s) {
                    return ext.boolExternSize(s);
                }
            });
        externalizers.put(STRING, new Externalizer {
                public byte[] extern(SlawExternalizer ext, Slaw s) {
                    return ext.externString(s);
                }
                public int externSize(SlawExternalizer ext, Slaw s) {
                    return ext.stringExternSize(s);
                }
            });
        externalizers.put(NUMBER, new Externalizer {
                public byte[] extern(SlawExternalizer ext, Slaw s) {
                    return ext.externNumber(s);
                }
                public int externSize(SlawExternalizer ext, Slaw s) {
                    return ext.numberExternSize(s);
                }
            });
        externalizers.put(COMPLEX, new Externalizer {
                public byte[] extern(SlawExternalizer ext, Slaw s) {
                    return ext.externComplex(s);
                }
                public int externSize(SlawExternalizer ext, Slaw s) {
                    return ext.complexExternSize(s);
                }
            });
        externalizers.put(VECTOR, new Externalizer {
                public byte[] extern(SlawExternalizer ext, Slaw s) {
                    return ext.externVector(s);
                }
                public int externSize(SlawExternalizer ext, Slaw s) {
                    return ext.vectorExternSize(s);
                }
            });
        externalizers.put(COMPLEX_VECTOR, new Externalizer {
                public byte[] extern(SlawExternalizer ext, Slaw s) {
                    return ext.externComplexVector(s);
                }
                public int externSize(SlawExternalizer ext, Slaw s) {
                    return ext.complexVectorExternSize(s);
                }
            });
        externalizers.put(MULTI_VECTOR, new Externalizer {
                public byte[] extern(SlawExternalizer ext, Slaw s) {
                    return ext.externMultiVector(s);
                }
                public int externSize(SlawExternalizer ext, Slaw s) {
                    return ext.multiVectorExternSize(s);
                }
            });
    }

    private static SlawExternalizer current = new SlawExternalizerV2();
}
