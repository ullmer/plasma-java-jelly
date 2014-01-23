// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.io;

import java.io.OutputStream;
import java.util.logging.Logger;

import com.oblong.util.ExceptionHandler;
import net.jcip.annotations.NotThreadSafe;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIO;
import com.oblong.jelly.SlawWriter;
import com.oblong.jelly.slaw.SlawExternalizer;

@NotThreadSafe
class StreamWriter implements SlawWriter {

    @Override public boolean write(Slaw s) {
        if (s == null) return false;
        try {
            externalizer.extern(s, stream);
            return true;
        } catch (Exception e) {
	        ExceptionHandler.handleException(e);
            logWarning("Error serializing slaw: ", e);
            return false;
        }
    }

    @Override public boolean close() {
        try {
            stream.close();
            return true;
        } catch (Exception e) {
	        ExceptionHandler.handleException(e);
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

    final OutputStream stream;

    private final SlawExternalizer externalizer;
    private final SlawIO.Format format;
}
