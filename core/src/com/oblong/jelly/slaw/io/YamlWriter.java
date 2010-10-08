// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.io;

import java.io.FileOutputStream;
import java.io.IOException;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIO;
import com.oblong.jelly.SlawIO.YamlOptions;

/**
 *
 * @author jao
 */
final class YamlWriter extends StreamWriter {

    YamlWriter(String file, YamlOptions opts) throws IOException {
        super(new FileOutputStream(file),
              SlawIO.Format.YAML,
              new YamlExternalizer(opts));
        useDirectives = opts.useDirectives();
        written = 0;
        if (useDirectives) {
            stream.write("%YAML 1.1\n".getBytes());
            stream.write("%TAG ! tag:oblong.com,2009:slaw/\n".getBytes());
        }
    }

    @Override public boolean write(Slaw s) {
        final boolean r = super.write(s);
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

    private final boolean useDirectives;
    private int written;
}
