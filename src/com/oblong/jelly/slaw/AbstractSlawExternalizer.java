// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw;

import java.io.IOException;
import java.io.OutputStream;

import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIlk;
import com.oblong.jelly.slaw.SlawExternalizer;
import com.oblong.jelly.util.ByteWriter;

import static com.oblong.jelly.SlawIlk.*;

public abstract class AbstractSlawExternalizer implements SlawExternalizer {

    @Override public final long extern(Slaw s, OutputStream os)
        throws IOException {
        final ByteWriter b = new ByteWriter(os);
        extern(s, b);
        return b.bytesWritten();
    }

    @Override public final int externSize(Slaw s) {
        if (s == null) return 0;
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

    protected final void extern(Slaw s, ByteWriter b) throws IOException {
        prepareBuffer(b, s);
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
        finishBuffer(b, s);
    }

    protected abstract void externNil(Slaw s, ByteWriter b)
        throws IOException;
    protected abstract int nilExternSize(Slaw s);

    protected abstract void externBool(Slaw b, ByteWriter bf)
        throws IOException;
    protected abstract int boolExternSize(Slaw b);

    protected abstract void externString(Slaw s, ByteWriter b)
        throws IOException;
    protected abstract int stringExternSize(Slaw s);

    protected abstract void externNumber(Slaw n, ByteWriter b)
        throws IOException;
    protected abstract int numberExternSize(Slaw n);

    protected abstract void externComplex(Slaw c, ByteWriter b)
        throws IOException;
    protected abstract int complexExternSize(Slaw c);

    protected abstract void externVector(Slaw v, ByteWriter b)
        throws IOException;
    protected abstract int vectorExternSize(Slaw v);

    protected abstract void externComplexVector(Slaw v, ByteWriter b)
        throws IOException;
    protected abstract int complexVectorExternSize(Slaw v);

    protected abstract void externMultivector(Slaw v, ByteWriter b)
        throws IOException;
    protected abstract int multivectorExternSize(Slaw v);

    protected abstract void externArray(Slaw a, ByteWriter b)
        throws IOException;
    protected abstract int arrayExternSize(Slaw a);

    protected abstract void externComplexArray(Slaw a, ByteWriter b)
        throws IOException;
    protected abstract int complexArrayExternSize(Slaw a);

    protected abstract void externVectorArray(Slaw a, ByteWriter b)
        throws IOException;
    protected abstract int vectorArrayExternSize(Slaw a);

    protected abstract void externComplexVectorArray(Slaw a, ByteWriter b)
        throws IOException;
    protected abstract int complexVectorArrayExternSize(Slaw a);

    protected abstract void externMultivectorArray(Slaw v, ByteWriter b)
        throws IOException;
    protected abstract int multivectorArrayExternSize(Slaw v);

    protected abstract void externCons(Slaw c, ByteWriter b)
        throws IOException;
    protected abstract int consExternSize(Slaw c);

    protected abstract void externList(Slaw c, ByteWriter b)
        throws IOException;
    protected abstract int listExternSize(Slaw c);

    protected abstract void externMap(Slaw c, ByteWriter b)
        throws IOException;
    protected abstract int mapExternSize(Slaw c);

    protected abstract void externProtein(Protein p, ByteWriter b)
        throws IOException;
    protected abstract int proteinExternSize(Protein p);

    protected abstract void prepareBuffer(ByteWriter b, Slaw s)
        throws IOException;
    protected abstract void finishBuffer(ByteWriter b, Slaw s)
        throws IOException;

}
