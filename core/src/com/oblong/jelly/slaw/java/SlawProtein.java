// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.java;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;

@Immutable
final class SlawProtein extends Protein {

    private final Slaw descrips;
    private final Slaw ingests;
    private final byte[] data;

    static SlawProtein valueOf(Slaw descrips, Slaw ingests, byte[] data) {
        return new SlawProtein(descrips, ingests, data);
    }

    @Override public Slaw descrips() { return descrips; }
    @Override public Slaw ingests() { return ingests; }
    @Override public byte datum(int n) { return data[n]; }
    @Override public int dataLength() { return data.length; }

    @Override public int putData(OutputStream os) throws IOException {
        os.write(data);
        return data.length;
    }

    @Override public long index() { return NO_INDEX; }
    @Override public long timestamp(TimeUnit u) { return NO_TIMESTAMP; }
    @Override public double timestamp() { return NO_TIMESTAMP; }
    @Override public String source() { return null; }

    private SlawProtein(Slaw d, Slaw i, byte[] b) {
        descrips = d;
        ingests = i;
        data = b == null? new byte[0] : b;
    }

}
