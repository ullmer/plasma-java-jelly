// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.mem;

import java.io.IOException;

import com.oblong.jelly.Protein;
import com.oblong.jelly.ProteinMetadata;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.slaw.io.BinaryExternalizer;

final class MemProteinMetadata implements ProteinMetadata {

    @Override public long size() {
        return externalizer.externSize(protein);
    }

    @Override public long index() {
        return protein.index();
    }

    @Override public double timestamp() {
        return protein.timestamp();
    }

    @Override public long ingestsSize() {
        return externalizer.externSize(protein.ingests());
    }

    @Override public long descripsSize() {
        return externalizer.externSize(protein.descrips());
    }

    @Override public long ingestsNumber() {
        return count(protein.ingests());
    }

    @Override public long descripsNumber() {
        return count(protein.descrips());
    }

    @Override public long dataSize() {
        return protein.dataLength();
    }

    @Override public Slaw descrips() {
        return partialProtein == null ? null : partialProtein.descrips();
    }

    @Override public Slaw ingests() {
        return partialProtein == null ? null : partialProtein.ingests();
    }

    @Override public byte[] data() {
        final byte[] empty = new byte[0];
        try {
            return partialProtein == null ? empty : partialProtein.copyData();
        } catch (IOException e) {
            return empty;
        }
    }

    MemProteinMetadata(Protein prot, Protein partial) {
        protein = prot;
        partialProtein = partial;
    }

    private static long count(Slaw s) {
        return s == null || !(s.isList() || s.isMap()) ? 0 : s.count();
    }

    private final Protein protein;
    private final Protein partialProtein;

    private static final BinaryExternalizer externalizer =
        new BinaryExternalizer();
}
