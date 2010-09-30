// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;

/**
 *
 * Created: Thu Sep 30 16:26:00 2010
 *
 * @author jao
 */
final class BinaryHeader {

    static BinaryHeader read(PushbackInputStream is) throws IOException {
        for (int i = 0; i < HEADER.length; ++i) {
            final int c = is.read();
            if (c != HEADER[i]) {
                is.unread(c);
                for (int j = i - 1; j > -1; --j) is.unread(HEADER[j]);
                return null;
            }
        }
        final int v = is.read();
        final int t = is.read();
        final int hf = is.read();
        final int lf = is.read();
        return new BinaryHeader(v, t, hf, lf);
    }

    BinaryHeader() {
        this(VERSION, TYPE, HFLAGS, LFLAGS);
    }

    void write(OutputStream os) throws IOException {
        for (int i = 0; i < HEADER.length; ++i) os.write(HEADER[i]);
        os.write(version);
        os.write(type);
        os.write(hflags);
        os.write(lflags);
    }

    boolean isLittleEndian() {
        return (lflags & BIG_ENDIAN_FLAG) == 1;
    }

    private BinaryHeader(int v, int t, int hf, int lf) {
        version = v;
        type = t;
        hflags = hf;
        lflags = lf;
    }

    private static final int[] HEADER = {0xff, 0xff, 0x0b, 0x10};
    private static final byte VERSION = 2;
    private static final byte TYPE = 1;
    private static final byte BIG_ENDIAN_FLAG = 1;
    private static final byte HFLAGS = 0;
    private static final byte LFLAGS = BIG_ENDIAN_FLAG;

    private final int version;
    private final int type;
    private final int hflags;
    private final int lflags;
}
