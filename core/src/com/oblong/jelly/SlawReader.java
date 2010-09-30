// Copyright (c) 2010 Oblong Industries
// Created: Wed Sep 29 16:20:22 2010

package com.oblong.jelly;

import java.util.Iterator;

/**
 * Iterator for Slaw instances retrieved from input data.
 *
 * <p> Reader instances associated to a file can be obtained using
 * {@link SlawIO#reader}. For a sane resouce management, it's
 * recommended to close the reader once you're done with it, as in the
 * following sample:
 * <pre>
 *   SlawReader reader = SlawIO.reader("full-of-slawx.yaml");
 *   while (reader.hasNext()) {
 *       processSlaw(reader.next());
 *   }
 *   reader.close();
 * </pre>
 *
 * <p> As you can see in the example above, SlawReader extends the
 * <code>Iterator<Slaw></code> interface, traversing the Slaw found in
 * the data stream. However, calling <code>SlawReader.remove()</code>
 * will always throw an <code>UnsupportedOperationException</code>
 * exception (SlawReaders being, well, readers).
 *
 * @author jao
 */
public interface SlawReader extends Iterator<Slaw> {
    /**
     * Closes the underlying data stream. For proper resource
     * management, you should make sure that this method is called on
     * the reader once you're done with it.
     */
    boolean close();

    /**
     * Readers detect automatically the format of the data stream.
     * This method tells you the detected format, returning
     * <code>null</code> in case it wasn't recognisable.
     */
    SlawIO.Format format();
}
