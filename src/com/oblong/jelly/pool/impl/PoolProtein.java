// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.impl;

import java.io.IOException;
import java.io.OutputStream;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.Hose;
import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;

/**
 *
 * Created: Fri Jun 18 22:14:29 2010
 *
 * @author jao
 */
@Immutable
public final class PoolProtein extends Protein {

    public PoolProtein(Protein p, long idx, double ts, Hose h) {
        protein = p;
        index = idx;
        stamp = ts;
        hose = h;
    }

    @Override public String debugString() {
        return protein.debugString()
            + "\nIndex: " + index + ", Timestamp: " + stamp;
    }

    @Override public Slaw descrips() { return protein.descrips(); }
    @Override public Slaw ingests() { return protein.ingests(); }
    @Override public byte data(int n) { return protein.data(n); }
    @Override public int dataLength() { return protein.dataLength(); }

    @Override public int putData(OutputStream os) throws IOException {
        return protein.putData(os);
    }

    @Override public long index() { return index; }
    @Override public double timestamp() { return stamp; }
    @Override public Hose source() { return hose; }


    private final Protein protein;
    private final long index;
    private final double stamp;
    private final Hose hose;
}
