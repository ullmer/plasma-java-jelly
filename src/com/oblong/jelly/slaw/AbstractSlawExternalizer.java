// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw;

import java.nio.ByteBuffer;

import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIlk;
import com.oblong.jelly.slaw.SlawExternalizer;

import static com.oblong.jelly.SlawIlk.*;

public abstract class AbstractSlawExternalizer implements SlawExternalizer {

    public final ByteBuffer extern(Slaw s) {
        final ByteBuffer b = ByteBuffer.allocate(externSize(s));
        extern(s, b);
        return (ByteBuffer)b.rewind();
    }

    public final int extern(Slaw s, ByteBuffer b) {
        prepareBuffer(b, s);
        final int begin = b.position();
        // Ugly, but shorter than an EnumMap and anonymous classes
        switch (s.ilk()) {
        case PROTEIN: externProtein(s.toProtein(), b); break;
        case BOOL: externBool(s, b); break;
        case STRING: externString(s, b); break;
        case NUMBER: externNumber(s, b); break;
        case COMPLEX: externComplex(s, b); break;
        case VECTOR: externVector(s, b); break;
        case COMPLEX_VECTOR: externComplexVector(s, b); break;
        case MULTI_VECTOR: externMultivector(s, b); break;
        case ARRAY: externArray(s, b); break;
        case COMPLEX_ARRAY: externComplexArray(s, b); break;
        case VECTOR_ARRAY: externVectorArray(s, b); break;
        case COMPLEX_VECTOR_ARRAY: externComplexVectorArray(s, b); break;
        case MULTI_VECTOR_ARRAY: externMultivectorArray(s, b); break;
        case CONS: externCons(s, b); break;
        case LIST: externList(s, b); break;
        case MAP: externMap(s, b); break;
        default:
            assert s.ilk() == NIL : "Unexpected ilk: " + s.ilk();
            externNil(s, b);
        }
        finishBuffer(b, s, begin);
        return b.position() - begin;
    }

    public final int externSize(Slaw s) {
        switch (s.ilk()) {
        case PROTEIN: return proteinExternSize(s.toProtein());
        case BOOL: return boolExternSize(s);
        case STRING: return stringExternSize(s);
        case NUMBER: return numberExternSize(s);
        case COMPLEX: return complexExternSize(s);
        case VECTOR: return vectorExternSize(s);
        case COMPLEX_VECTOR: return complexVectorExternSize(s);
        case MULTI_VECTOR: return multivectorExternSize(s);
        case ARRAY: return arrayExternSize(s);
        case COMPLEX_ARRAY: return complexArrayExternSize(s);
        case VECTOR_ARRAY: return vectorArrayExternSize(s);
        case COMPLEX_VECTOR_ARRAY: return complexVectorArrayExternSize(s);
        case MULTI_VECTOR_ARRAY: return multivectorArrayExternSize(s);
        case CONS: return consExternSize(s);
        case LIST: return listExternSize(s);
        case MAP: return mapExternSize(s);
        default:
            assert s.ilk() == NIL : "Unexpected ilk: " + s.ilk();
            return nilExternSize(s);
        }
    }

    protected abstract void externNil(Slaw s, ByteBuffer b);
    protected abstract int nilExternSize(Slaw s);

    protected abstract void externBool(Slaw b, ByteBuffer bf);
    protected abstract int boolExternSize(Slaw b);

    protected abstract void externString(Slaw s, ByteBuffer b);
    protected abstract int stringExternSize(Slaw s);

    protected abstract void externNumber(Slaw n, ByteBuffer b);
    protected abstract int numberExternSize(Slaw n);

    protected abstract void externComplex(Slaw c, ByteBuffer b);
    protected abstract int complexExternSize(Slaw c);

    protected abstract void externVector(Slaw v, ByteBuffer b);
    protected abstract int vectorExternSize(Slaw v);

    protected abstract void externComplexVector(Slaw v, ByteBuffer b);
    protected abstract int complexVectorExternSize(Slaw v);

    protected abstract void externMultivector(Slaw v, ByteBuffer b);
    protected abstract int multivectorExternSize(Slaw v);

    protected abstract void externArray(Slaw a, ByteBuffer b);
    protected abstract int arrayExternSize(Slaw a);

    protected abstract void externComplexArray(Slaw a, ByteBuffer b);
    protected abstract int complexArrayExternSize(Slaw a);

    protected abstract void externVectorArray(Slaw a, ByteBuffer b);
    protected abstract int vectorArrayExternSize(Slaw a);

    protected abstract void externComplexVectorArray(Slaw a, ByteBuffer b);
    protected abstract int complexVectorArrayExternSize(Slaw a);

    protected abstract void externMultivectorArray(Slaw v, ByteBuffer b);
    protected abstract int multivectorArrayExternSize(Slaw v);

    protected abstract void externCons(Slaw c, ByteBuffer b);
    protected abstract int consExternSize(Slaw c);

    protected abstract void externList(Slaw c, ByteBuffer b);
    protected abstract int listExternSize(Slaw c);

    protected abstract void externMap(Slaw c, ByteBuffer b);
    protected abstract int mapExternSize(Slaw c);

    protected abstract void externProtein(Protein p, ByteBuffer b);
    protected abstract int proteinExternSize(Protein p);

    protected abstract void prepareBuffer(ByteBuffer b, Slaw s);
    protected abstract void finishBuffer(ByteBuffer b, Slaw s, int begin);

}
