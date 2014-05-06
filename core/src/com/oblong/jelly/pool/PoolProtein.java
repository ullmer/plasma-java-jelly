// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool;

import com.oblong.jelly.Hose;
import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;
import net.jcip.annotations.Immutable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

/**
 *
 * Created: Fri Jun 18 22:14:29 2010
 *
 * @author jao
 */
@Immutable
public class PoolProtein extends Protein {

    public PoolProtein(Protein p, long idx, double ts, Hose h) {
        protein = p;
        index = idx;
        stamp = ts;
        hose = h;
    }

    public PoolProtein(PoolProtein p, Hose h) {
        this(p.bareProtein(), p.index(), p.timestamp(), h);
    }

    public Protein bareProtein() { return protein; }

    @Override public String toString() {
        return super.toString() + "\nIndex: " + index + ", Stamp: " + stamp;
    }

    @Override public Slaw descrips() { return protein.descrips(); }
    @Override public Slaw ingests() { return protein.ingests(); }
    @Override public byte datum(int n) { return protein.datum(n); }
    @Override public int dataLength() { return protein.dataLength(); }

    @Override public int putData(OutputStream os) throws IOException {
        return protein.putData(os);
    }

    @Override public long index() { return index; }
    @Override public double timestamp() { return stamp; }
    @Override public long timestamp(TimeUnit unit) {
        return unit.convert((long)(stamp * 1e9), TimeUnit.NANOSECONDS);
    }
    @Override public String source() {
        return hose == null ? null : hose.name();
    }

    private final Protein protein;
    private final long index;
    private final double stamp;
    private final Hose hose;

}
