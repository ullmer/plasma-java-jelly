// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.io;

import java.io.InputStream;
import java.util.logging.Logger;

import net.jcip.annotations.NotThreadSafe;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIO.Format;
import com.oblong.jelly.SlawReader;
import com.oblong.jelly.slaw.JavaSlawFactory;
import com.oblong.jelly.slaw.SlawInternalizer;
import com.oblong.jelly.slaw.SlawFactory;

/**
 *
 * Created: Thu Sep 30 17:59:03 2010
 *
 * @author jao
 */
@NotThreadSafe
final class StreamReader implements SlawReader {

    @Override public Slaw next() {
        final Slaw result = hasNext() ? next : null;
        next = null;
        return result;
    }

    @Override public boolean hasNext() {
        if (next == null) next = fetchNext();
        return next != null;
    }

    @Override public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override public boolean close() {
        try {
            stream.close();
            return true;
        } catch (Exception e) {
            logWarning("Error closing stream: ", e);
            return false;
        }
    }

    @Override public Format format() { return format; }

    StreamReader(InputStream is, SlawInternalizer in, boolean le, Format fmt) {
        stream = is;
        internalizer = in;
        format = fmt;
        littleEndian = le;
        next = null;
    }

    private Slaw fetchNext() {
        try {
            return internalizer.internSlaw(stream, factory, littleEndian);
        } catch (Exception e) {
            return null;
        }
    }

    private void logWarning(String msg, Exception e) {
        final Logger log = Logger.getLogger(getClass().getName());
        log.warning(msg + e.getMessage());
    }

    private static final SlawFactory factory = new JavaSlawFactory();

    private final InputStream stream;
    private final SlawInternalizer internalizer;
    private final Format format;
    private final boolean littleEndian;
    private Slaw next;
}
