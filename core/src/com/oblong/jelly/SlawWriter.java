// Copyright (c) 2010 Oblong Industries
// Created: Thu Sep 30 12:16:24 2010

package com.oblong.jelly;

/**
 * Slaw serializer, writing binary and YAML data streams.
 *
 * <p> The {@link SlawIO} factory creates instances of this interface,
 * which provides methods for serializing Slaw objects.
 *
 * <p> Writers offer not concurrency guarantees, and attaching more
 * than one to the same output stream (or the same file) will produce
 * undefined results. For proper resource management, they should be
 * explictly closed once you're done with them.
 *
 * @author jao
 */
public interface SlawWriter {
    /**
     * Appends a new serialized Slaw to the end of the underlying data
     * stream. Returns a success indicator.
     */
    boolean write(Slaw s);

    /**
     * Closes the underlying data stream. For proper resource
     * management, you should make sure that this method is called on
     * the reader once you're done with it.
     */
    boolean close();

    /**
     * Writers can output binary or YAML data. This method tells you
     * what format this writer is using.
     */
    SlawIO.Format format();
}
