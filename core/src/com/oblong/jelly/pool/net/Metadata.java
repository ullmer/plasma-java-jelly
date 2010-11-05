// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.net;

import java.util.ArrayList;
import java.util.List;

import com.oblong.jelly.Hose;
import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.Protein;
import com.oblong.jelly.ProteinMetadata;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.pool.PoolProtein;

public final class Metadata implements ProteinMetadata {

    public static final Slaw RKEY = Slaw.string("retort");
    public static final Slaw INDEX_KEY = Slaw.string("idx");
    public static final Slaw STAMP_KEY = Slaw.string("time");
    public static final Slaw SIZE_KEY = Slaw.string("tbytes");
    public static final Slaw ISIZE_KEY = Slaw.string("ibytes");
    public static final Slaw DSIZE_KEY = Slaw.string("dbytes");
    public static final Slaw RSIZE_KEY = Slaw.string("rbytes");
    public static final Slaw INO_KEY = Slaw.string("ning");
    public static final Slaw DNO_KEY = Slaw.string("ndes");
    public static final Slaw PROTEIN_KEY = Slaw.string("prot");

    @Override public long index() { return readLong(INDEX_KEY, -1); }
    @Override public double timestamp() { return readDouble(STAMP_KEY, -1); }
    @Override public long size() { return readLong(SIZE_KEY, 0); }
    @Override public long ingestsSize() { return readLong(ISIZE_KEY, 0); }
    @Override public long descripsSize() { return readLong(DSIZE_KEY, 0); }
    @Override public long ingestsNumber() { return readLong(INO_KEY, 0); }
    @Override public long descripsNumber() { return readLong(DNO_KEY, 0); }
    @Override public long dataSize() { return readLong(RSIZE_KEY, 0); }

    Protein protein(Hose h) {
        final Slaw p = map.find(PROTEIN_KEY);
        if (p == null || !p.isProtein()) return null;
        return new PoolProtein(p.toProtein(), index(), timestamp(), h);
    }

    long retort() { return readLong(RKEY, -1); }

    static List<ProteinMetadata> parseMeta(Slaw lm) {
        final List<ProteinMetadata> result =
            new ArrayList<ProteinMetadata>(lm.count());
        for (Slaw m : lm) {
            if (m.isMap()) {
                final Metadata md = new Metadata(m);
                if (md.retort() == 0) result.add(md);
            }
        }
        return result;
    }

    static List<Protein> parseProteins(Slaw lm, Hose h) {
        final List<Protein> result = new ArrayList<Protein>(lm.count());
        for (Slaw m : lm) {
            if (m.isMap()) {
                final Metadata md = new Metadata(m);
                if (md.retort() == 0) {
                    final Protein p = md.protein(h);
                    if (p != null) result.add(p);
                }
            }
        }
        return result;
    }

    Metadata(Slaw m) { map = m; }

    private final long readLong(Slaw key, long def) {
        final Slaw v = map.find(key);
        return v != null && v.isNumber(NumericIlk.INT64) ? v.emitLong() : def;
    }

    private final double readDouble(Slaw key, double def) {
        final Slaw v = map.find(key);
        return v != null && v.isNumber(NumericIlk.FLOAT64) ?
            v.emitDouble() : def;
    }

    private final Slaw map;
}
