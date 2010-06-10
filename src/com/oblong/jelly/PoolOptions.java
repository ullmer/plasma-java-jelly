// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import net.jcip.annotations.Immutable;

/**
 *
 * Created: Thu Jun 10 20:56:49 2010
 *
 * @author jao
 */
@Immutable
public final class PoolOptions {

    public static final PoolOptions SMALL;
    public static final PoolOptions MEDIUM;
    public static final PoolOptions LARGE;
    public static final PoolOptions HUGE;

    public static final Slaw SIZE_KEY;
    public static final Slaw INDEX_SIZE_KEY;

    public PoolOptions(Slaw options) {
        this.options = options == null ? Slaw.map() : options;
    }

    public PoolOptions(int size) {
        options = Slaw.map(SIZE_KEY, Slaw.int32(size));
    }

    public PoolOptions(long size, long indexSize) {
        options = Slaw.map(SIZE_KEY, Slaw.unt64(size),
                           INDEX_SIZE_KEY, Slaw.unt64(indexSize));
    }

    public long poolSize() {
        return getLong(SIZE_KEY);
    }

    public long indexSize() {
        return getLong(INDEX_SIZE_KEY);
    }

    public Slaw toSlaw() {
        return options;
    }

    private long getLong(Slaw k) {
        final Slaw v = options.find(k);
        return (v != null && v.isNumber()) ? v.emitLong() : -1;
    }

    private final Slaw options;

    static {
        SIZE_KEY = Slaw.string("size");
        INDEX_SIZE_KEY = Slaw.string("index-capacity");

        final long MB = 1<<20;
        final long CAP = 1<<10;

        SMALL = new PoolOptions(MB, CAP);
        MEDIUM = new PoolOptions(10 * MB, 10 * CAP);
        LARGE = new PoolOptions(100 * MB, 100 * CAP);
        HUGE = new PoolOptions(MB<<11, CAP<<10);
    }
}
