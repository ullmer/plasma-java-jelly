// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.IOException;

import com.oblong.jelly.SlawIO.Format;
import com.oblong.jelly.SlawReader;
import com.oblong.jelly.SlawWriter;
import com.oblong.jelly.slaw.v2.BinaryExternalizer;
import com.oblong.jelly.slaw.v2.BinaryInternalizer;

/**
 *
 * Created: Fri Oct  1 00:22:39 2010
 *
 * @author jao
 */
public final class Factory {

    public static SlawReader reader(String fileName) throws IOException {
        final PushbackInputStream is =
            new PushbackInputStream(new FileInputStream(fileName));
        final BinaryHeader header = BinaryHeader.read(is);
        return header == null ? yamlReader(is) : binaryReader(is, header);
    }

    public static SlawWriter binaryWriter(String fileName)
        throws IOException {
        final FileOutputStream os = new FileOutputStream(fileName);
        HEADER.write(os);
        return new Writer(os, Format.BINARY, new BinaryExternalizer());
    }

    private static SlawReader yamlReader(InputStream is) {
        return null;
    }

    private static SlawReader binaryReader(InputStream is, BinaryHeader hd)
        throws IOException {
        return new Reader(is, new BinaryInternalizer(),
                          hd.isLittleEndian(), Format.BINARY);

    }

    private Factory() {}

    private static final BinaryHeader HEADER = new BinaryHeader();
}
