// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.io;

import java.io.OutputStream;
import java.util.logging.Logger;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIO;
import com.oblong.jelly.SlawWriter;
import com.oblong.jelly.slaw.SlawExternalizer;

/**
 *
 * Created: Thu Sep 30 21:43:13 2010
 *
 * @author jao
 */
final class StreamWriter implements SlawWriter {

    @Override public final boolean write(Slaw s) {
        if (s == null) return false;
        try {
            return externalizer.extern(s, stream) > 0;
        } catch (Exception e) {
            logWarning("Error serializing slaw: ", e);
            return false;
        }
    }

    @Override public final boolean close() {
        try {
            stream.close();
            return true;
        } catch (Exception e) {
            logWarning("Error closing stream: ", e);
            return false;
        }
    }

    @Override public final SlawIO.Format format() { return format; }

    StreamWriter(OutputStream os, SlawIO.Format fmt, SlawExternalizer ext) {
        stream = os;
        format = fmt;
        externalizer = ext;
    }

    private void logWarning(String msg, Exception e) {
        final Logger log = Logger.getLogger(getClass().getName());
        log.warning(msg + e.getMessage());
    }

    private final OutputStream stream;
    private final SlawExternalizer externalizer;
    private final SlawIO.Format format;
}
