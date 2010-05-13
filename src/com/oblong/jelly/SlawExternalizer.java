// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.Map;

import static com.oblong.jelly.SlawIlk.*;

/**
 * Created: Sun Apr 18 01:46:42 2010
 *
 * @author jao
 */
abstract class SlawExternalizer {

    final ByteBuffer extern(Slaw s) {
        final ByteBuffer b = ByteBuffer.allocate(externSize(s));
        extern(s, b);
        return b;
    }

    final int extern(Slaw s, ByteBuffer b) {
        prepareBuffer(b, s);
        final int begin = b.position();
        externalizers.get(s.ilk()).extern(this, s, b);
        finishBuffer(b, s, begin);
        return b.position() - begin;
    }

    final int externSize(Slaw s) {
        return externalizers.get(s.ilk()).externSize(this, s);
    }

    abstract void externNil(Slaw s, ByteBuffer b);
    abstract int nilExternSize(Slaw s);

    abstract void externBool(Slaw b, ByteBuffer bf);
    abstract int boolExternSize(Slaw b);

    abstract void externString(Slaw s, ByteBuffer b);
    abstract int stringExternSize(Slaw s);

    abstract void externNumber(Slaw n, ByteBuffer b);
    abstract int numberExternSize(Slaw n);

    abstract void externComplex(Slaw c, ByteBuffer b);
    abstract int complexExternSize(Slaw c);

    abstract void externVector(Slaw v, ByteBuffer b);
    abstract int vectorExternSize(Slaw v);

    abstract void externComplexVector(Slaw v, ByteBuffer b);
    abstract int complexVectorExternSize(Slaw v);

    abstract void externMultivector(Slaw v, ByteBuffer b);
    abstract int multivectorExternSize(Slaw v);

    abstract void externArray(Slaw a, ByteBuffer b);
    abstract int arrayExternSize(Slaw a);

    abstract void externComplexArray(Slaw a, ByteBuffer b);
    abstract int complexArrayExternSize(Slaw a);

    abstract void externVectorArray(Slaw a, ByteBuffer b);
    abstract int vectorArrayExternSize(Slaw a);

    abstract void externComplexVectorArray(Slaw a, ByteBuffer b);
    abstract int complexVectorArrayExternSize(Slaw a);

    abstract void externMultivectorArray(Slaw v, ByteBuffer b);
    abstract int multivectorArrayExternSize(Slaw v);

    abstract void externCons(Slaw c, ByteBuffer b);
    abstract int consExternSize(Slaw c);

    abstract void externList(Slaw c, ByteBuffer b);
    abstract int listExternSize(Slaw c);

    abstract void externMap(Slaw c, ByteBuffer b);
    abstract int mapExternSize(Slaw c);

    abstract void prepareBuffer(ByteBuffer b, Slaw s);
    abstract void finishBuffer(ByteBuffer b, Slaw s, int begin);

    private interface Externalizer {
        void extern(SlawExternalizer ext, Slaw s, ByteBuffer b);
        int externSize(SlawExternalizer ext, Slaw s);
    }

    private static final Map<SlawIlk,Externalizer> externalizers;
    static {
        externalizers = new EnumMap<SlawIlk,Externalizer>(SlawIlk.class);
        externalizers.put(NIL, new Externalizer() {
                public void extern(SlawExternalizer e, Slaw s, ByteBuffer b) {
                    e.externNil(s, b);
                }
                public int externSize(SlawExternalizer e, Slaw s) {
                    return e.nilExternSize(s);
                }
            });
        externalizers.put(BOOL, new Externalizer() {
                public void extern(SlawExternalizer e, Slaw s, ByteBuffer b) {
                    e.externBool(s, b);
                }
                public int externSize(SlawExternalizer e, Slaw s) {
                    return e.boolExternSize(s);
                }
            });
        externalizers.put(STRING, new Externalizer() {
                public void extern(SlawExternalizer e, Slaw s, ByteBuffer b) {
                    e.externString(s, b);
                }
                public int externSize(SlawExternalizer e, Slaw s) {
                    return e.stringExternSize(s);
                }
            });
        externalizers.put(NUMBER, new Externalizer() {
                public void extern(SlawExternalizer e, Slaw s, ByteBuffer b) {
                    e.externNumber(s, b);
                }
                public int externSize(SlawExternalizer e, Slaw s) {
                    return e.numberExternSize(s);
                }
            });
        externalizers.put(COMPLEX, new Externalizer() {
                public void extern(SlawExternalizer e, Slaw s, ByteBuffer b) {
                    e.externComplex(s, b);
                }
                public int externSize(SlawExternalizer e, Slaw s) {
                    return e.complexExternSize(s);
                }
            });
        externalizers.put(VECTOR, new Externalizer() {
                public void extern(SlawExternalizer e, Slaw s, ByteBuffer b) {
                    e.externVector(s, b);
                }
                public int externSize(SlawExternalizer e, Slaw s) {
                    return e.vectorExternSize(s);
                }
            });
        externalizers.put(COMPLEX_VECTOR, new Externalizer() {
                public void extern(SlawExternalizer e, Slaw s, ByteBuffer b) {
                    e.externComplexVector(s, b);
                }
                public int externSize(SlawExternalizer e, Slaw s) {
                    return e.complexVectorExternSize(s);
                }
            });
        externalizers.put(MULTI_VECTOR, new Externalizer() {
                public void extern(SlawExternalizer e, Slaw s, ByteBuffer b) {
                    e.externMultivector(s, b);
                }
                public int externSize(SlawExternalizer e, Slaw s) {
                    return e.multivectorExternSize(s);
                }
            });
        externalizers.put(ARRAY, new Externalizer() {
                public void extern(SlawExternalizer e, Slaw s, ByteBuffer b) {
                    e.externArray(s, b);
                }
                public int externSize(SlawExternalizer e, Slaw s) {
                    return e.arrayExternSize(s);
                }
            });
        externalizers.put(COMPLEX_ARRAY, new Externalizer() {
                public void extern(SlawExternalizer e, Slaw s, ByteBuffer b) {
                    e.externComplexArray(s, b);
                }
                public int externSize(SlawExternalizer e, Slaw s) {
                    return e.complexArrayExternSize(s);
                }
            });
        externalizers.put(VECTOR_ARRAY, new Externalizer() {
                public void extern(SlawExternalizer e, Slaw s, ByteBuffer b) {
                    e.externVectorArray(s, b);
                }
                public int externSize(SlawExternalizer e, Slaw s) {
                    return e.vectorArrayExternSize(s);
                }
            });
        externalizers.put(COMPLEX_VECTOR_ARRAY, new Externalizer() {
                public void extern(SlawExternalizer e, Slaw s, ByteBuffer b) {
                    e.externComplexVectorArray(s, b);
                }
                public int externSize(SlawExternalizer e, Slaw s) {
                    return e.complexVectorArrayExternSize(s);
                }
            });
        externalizers.put(MULTI_VECTOR_ARRAY, new Externalizer() {
                public void extern(SlawExternalizer e, Slaw s, ByteBuffer b) {
                    e.externMultivectorArray(s, b);
                }
                public int externSize(SlawExternalizer e, Slaw s) {
                    return e.multivectorArrayExternSize(s);
                }
            });
        externalizers.put(CONS, new Externalizer() {
                public void extern(SlawExternalizer e, Slaw s, ByteBuffer b) {
                    e.externCons(s, b);
                }
                public int externSize(SlawExternalizer e, Slaw s) {
                    return e.consExternSize(s);
                }
            });
        externalizers.put(LIST, new Externalizer() {
                public void extern(SlawExternalizer e, Slaw s, ByteBuffer b) {
                    e.externList(s, b);
                }
                public int externSize(SlawExternalizer e, Slaw s) {
                    return e.listExternSize(s);
                }
            });
        externalizers.put(MAP, new Externalizer() {
                public void extern(SlawExternalizer e, Slaw s, ByteBuffer b) {
                    e.externMap(s, b);
                }
                public int externSize(SlawExternalizer e, Slaw s) {
                    return e.mapExternSize(s);
                }
            });
    }

}
