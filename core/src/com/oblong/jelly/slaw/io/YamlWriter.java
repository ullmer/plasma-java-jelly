// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.io;

import java.io.OutputStream;
import java.io.IOException;

import net.jcip.annotations.NotThreadSafe;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIO;
import com.oblong.jelly.SlawIO.YamlOptions;

@NotThreadSafe
final class YamlWriter extends StreamWriter {

    YamlWriter(OutputStream os, YamlOptions opts) throws IOException {
        super(os, SlawIO.Format.YAML, new YamlExternalizer(opts));
        useDirectives = opts.useDirectives();
        written = 0;
        if (useDirectives) {
            stream.write("%YAML 1.1\n".getBytes());
            stream.write("%TAG ! tag:oblong.com,2009:slaw/\n".getBytes());
        }
    }

    @Override public boolean write(Slaw s) {
        final boolean r = writePreamble() && super.write(s);
        if (r) ++written;
        return r;
    }

    @Override public boolean close() {
        if (written == 0) {
            try {
                stream.write(YamlTags.EOED.getBytes());
            } catch (IOException e) {}
        }
        return super.close();
    }

    private boolean writePreamble() {
        try {
            if (written > 0) stream.write(NL);
            stream.write(PREAMBLE);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private final boolean useDirectives;
    private int written;
    private static final byte[] PREAMBLE = "--- ".getBytes();
    private static final byte[] NL = "\n".getBytes();
}
