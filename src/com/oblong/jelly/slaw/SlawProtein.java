// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIlk;
import net.jcip.annotations.Immutable;

@Immutable
final class SlawProtein extends Protein {

    static SlawProtein valueOf(Slaw descrips, Slaw ingests, byte[] data) {
        return new SlawProtein(descrips, ingests, data);
    }

    @Override public SlawIlk ilk() { return SlawIlk.PROTEIN; }
    @Override public NumericIlk numericIlk() { return null; }

    @Override public boolean emitBoolean() {
        throw new UnsupportedOperationException(ilk() + " as boolean");
    }

    @Override public String emitString() {
        throw new UnsupportedOperationException(ilk() + " as string");
    }

    @Override public long emitLong() {
        throw new UnsupportedOperationException(ilk() + " as long");
    }

    @Override public double emitDouble() {
        throw new UnsupportedOperationException(ilk() + " as double");
    }

    @Override public BigInteger emitBigInteger() {
        throw new UnsupportedOperationException(ilk() + " as big integer");
    }

    @Override public Map<Slaw,Slaw> emitMap() {
        return new HashMap<Slaw,Slaw>();
    }

    @Override public Slaw withNumericIlk(NumericIlk ilk) {
        throw new UnsupportedOperationException(ilk() + " not numeric");
    }

    @Override public Slaw car() { return descrips; }
    @Override public Slaw cdr() { return ingests; }

    @Override public int dimension() { return 0; }
    @Override public int count() { return 0; }
    @Override public Slaw nth(int n) {
        throw new IndexOutOfBoundsException("Not a list");
    }
    @Override public Slaw find(Slaw e) { return null; }

    @Override public int hashCode() {
        int h = 5;
        if (descrips != null) h += 13 * descrips.hashCode();
        if (ingests != null) h += 17 * ingests.hashCode();
        if (data != null) h += 19 * data.hashCode();
        return h;
    }

    @Override public boolean slawEquals(Slaw o) {
        Protein op = o.toProtein();
        boolean eqs = descrips == null ?
            op.descrips() == null : descrips.equals(op.descrips());
        if (eqs) eqs = ingests == null ?
                     op.ingests() == null : ingests.equals(op.ingests());
        if (eqs) eqs = op.dataLength() == data.length;
        for (int i = 0, c = data.length; eqs && i < c; ++i)
            eqs = data[i] == op.data(i);
        return eqs;
    }

    @Override public String debugString() {
        return "\ndescrips: " + descrips + "\ningests: " + ingests
            + "\n(" + data.length + " bytes of raw data" + ")";
    }

    @Override public Slaw descrips() { return descrips; }
    @Override public Slaw ingests() { return ingests; }
    @Override public byte data(int n) { return data[n]; }
    @Override public int dataLength() { return data.length; }

    @Override public int putData(ByteBuffer b) {
        if (b.remaining() < data.length) return 0;
        b.put(data);
        return data.length;
    }

    private SlawProtein(Slaw d, Slaw i, byte[] b) {
        descrips = d;
        ingests = i;
        data = b == null? new byte[0] : b;
    }

    private final Slaw descrips;
    private final Slaw ingests;
    private final byte[] data;
}
