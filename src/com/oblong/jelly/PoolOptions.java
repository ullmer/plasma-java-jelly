// Copyright (c) 2010 Oblong Industries
// Created: Thu Jun 10 20:56:49 2010

package com.oblong.jelly;

import net.jcip.annotations.Immutable;

/**
 * Options for newly created pools.
 * <p>
 * This class lets you specify creation options for new pools. It also
 * defines several pre-cooked instances with typical parameter values.
 *
 * @author jao
 */
@Immutable
public class PoolOptions {

    /**
     * Options specifying a pool of 1Mb with an index with a capacity
     * of 1024 entries.
     */
    public static final PoolOptions SMALL;

    /**
     * Options specifying a pool of 10Mb with an index with a capacity
     * of 10K entries.
     */
    public static final PoolOptions MEDIUM;

    /**
     * Options specifying a pool of 100Mb with an index with a capacity
     * of 100K entries.
     */
    public static final PoolOptions LARGE;

    /**
     * Options specifying a pool of 1Gb with an index with a capacity
     * of one million entries.
     */
    public static final PoolOptions HUGE;

    /**
     * For pool implementers defining a new pool type or server
     * protocol.
     */
    public PoolOptions(Slaw options) {
        this.options = options == null ? SMALL.options : options;
    }

    /**
     * Creates an options instance specifying a pool size of the given
     * number of bytes. For mmap pools, this size is the total disk
     * space occupied by the file in the server backing the pool.
     */
    public PoolOptions(long size) {
        options = Slaw.map(SIZE_KEY, Slaw.unt64(size));
    }

    /**
     * Creates an options instance specifying a pool size of the given
     * number of bytes and an index with the given number of entries.
     * Pools of type mmap use an index to speed up lookups; for
     * optimal performance, its capacity should be close to the
     * (average) expected number of proteins in the pool. Note that
     * every entry on the index contributes 16 bytes to its size.
     */
    public PoolOptions(long size, long indexCapacity) {
        options = Slaw.map(SIZE_KEY, Slaw.unt64(size),
                           INDEX_SIZE_KEY, Slaw.unt64(indexCapacity));
    }

    public long poolSize() {
        return getLong(SIZE_KEY);
    }

    public long indexCapacity() {
        return getLong(INDEX_SIZE_KEY);
    }

    /**
     * Options are transmitted through the wire as a Protein. Chances
     * are you'll get little use of this method.
     */
    public Protein toProtein() {
        return Slaw.protein(null, options);
    }

    private long getLong(Slaw k) {
        final Slaw v = options.find(k);
        return (v != null && v.isNumber()) ? v.emitLong() : -1;
    }

    private final Slaw options;

    private static final Slaw SIZE_KEY;
    private static final Slaw INDEX_SIZE_KEY;

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
