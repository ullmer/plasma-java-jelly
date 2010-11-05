// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.mem;


import com.oblong.jelly.Protein;
import com.oblong.jelly.ProteinMetadata;
import com.oblong.jelly.ProteinMetadata;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.slaw.io.BinaryExternalizer;

final class MemProteinMetadata implements ProteinMetadata {

    MemProteinMetadata(Protein p) {
        protein = p;
    }

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
        return listCount(protein.ingests());
    }

    @Override public long descripsNumber() {
        return listCount(protein.descrips());
    }

    @Override public long dataSize() {
        return protein.dataLength();
    }

    private static long listCount(Slaw s) {
        return s == null || !s.isList() ? 0 : s.count();
    }

    final Protein protein;

    private static final BinaryExternalizer externalizer =
        new BinaryExternalizer();
}
