// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 *
 * Created: Mon May 17 14:25:51 2010
 *
 * @author jao
 */
public abstract class Protein extends Slaw {
    public static final long NO_INDEX = -1;
    public static final long NO_TIMESTAMP = -1;

    public abstract Slaw ingests();
    public abstract Slaw descrips();

    public abstract byte data(int n);
    public abstract int dataLength();
    public abstract int putData(OutputStream os) throws IOException;

    public abstract long index();
    public abstract long timestamp(TimeUnit unit);
    public abstract double timestamp();
    public abstract Hose source();

    @Override public final SlawIlk ilk() { return SlawIlk.PROTEIN; }
    @Override public final NumericIlk numericIlk() { return null; }

    @Override public final boolean emitBoolean() {
        throw new UnsupportedOperationException(ilk() + " as boolean");
    }

    @Override public final String emitString() {
        throw new UnsupportedOperationException(ilk() + " as string");
    }

    @Override public final long emitLong() {
        throw new UnsupportedOperationException(ilk() + " as long");
    }

    @Override public final double emitDouble() {
        throw new UnsupportedOperationException(ilk() + " as double");
    }

    @Override public final BigInteger emitBigInteger() {
        throw new UnsupportedOperationException(ilk() + " as big integer");
    }

    @Override public final Map<Slaw,Slaw> emitMap() {
        return new HashMap<Slaw,Slaw>();
    }

    @Override public final Slaw withNumericIlk(NumericIlk ilk) {
        throw new UnsupportedOperationException(ilk() + " not numeric");
    }

    @Override public final Slaw car() { return descrips(); }
    @Override public final Slaw cdr() { return ingests(); }

    @Override public final int dimension() { return 0; }
    @Override public final int count() { return 0; }
    @Override public final Slaw nth(int n) {
        throw new UnsupportedOperationException("Not a list");
    }
    @Override public final Slaw find(Slaw e) { return null; }

    @Override public final boolean slawEquals(Slaw o) {
        Protein op = o.toProtein();

        boolean eqs = index() == op.index()
            && timestamp() == timestamp()
            && op.dataLength() == dataLength()
            && eqRefs(descrips(), op.descrips())
            && eqRefs(ingests(), op.ingests());

        for (int i = 0, c = dataLength(); eqs && i < c; ++i)
            eqs = data(i) == op.data(i);
        return eqs;
    }

    @Override public final int hashCode() {
        int h = 5;
        if (descrips() != null) h += 13 * descrips().hashCode();
        if (ingests() != null) h += 17 * ingests().hashCode();
        for (int i = 0, c = dataLength(); i < c; ++i) h += data(i);
        h += index();
        h += (int)timestamp(TimeUnit.NANOSECONDS);
        return h;
    }

    private static boolean eqRefs(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }
}
