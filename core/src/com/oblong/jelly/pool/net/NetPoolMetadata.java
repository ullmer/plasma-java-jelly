// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.net;

import com.oblong.jelly.PoolMetadata;
import com.oblong.jelly.Slaw;

/**
 *
 * @author jao
 */
final class NetPoolMetadata implements PoolMetadata {

    NetPoolMetadata(Slaw md) {
        size = readLong(md, SIZE_K, 0);
        usedSize = readLong(md, USIZE_K, 0);
        indexCapacity = readLong(md, INDEX_K, -1);
        usedIndexCapacity = readLong(md, UINDEX_K, 0);
    }

    @Override public long size() { return size; }

    @Override public long usedSize() { return usedSize; }

    @Override public long indexCapacity() { return indexCapacity; }

    @Override public long usedIndexCapacity() { return usedIndexCapacity; }


    private static long readLong(Slaw map, Slaw key, long def) {
        final Slaw v = map.find(key);
        return v != null && v.isNumber() ? v.emitLong() : def;
    }

    private final static Slaw SIZE_K = Slaw.string("size");
    private final static Slaw USIZE_K = Slaw.string("size-used");
    private final static Slaw INDEX_K = Slaw.string("index-capacity");
    private final static Slaw UINDEX_K = Slaw.string("index-count");

    private final long size;
    private final long usedSize;
    private final long indexCapacity;
    private final long usedIndexCapacity;
}
