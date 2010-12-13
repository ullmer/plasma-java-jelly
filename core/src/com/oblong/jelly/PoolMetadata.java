// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

/**
 * Metadata on remote pools. Once you connect to a pool and get a Hose
 * to it, use {@link Hose#metadata()} to obtain an instance of this
 * interface for the corresponding pool.
 *
 * @author jao
 */
public interface PoolMetadata {

    /** Total size of the pool, in bytes. */
    long size();

    /** Size currently in use in the pool, in bytes */
    long usedSize();

    /**
     * If the pool uses an index, returns its capacity. Otherwise,
     * this method returns -1.
     */
    long indexCapacity();

    /**
     * If the pool uses an index, returns the number of entries used.
     * Otherwise, this method returns -1.
     */
    long usedIndexCapacity();
}
