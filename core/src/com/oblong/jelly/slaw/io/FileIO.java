// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.util.Set;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.SlawIO;
import com.oblong.jelly.SlawIO.Format;
import com.oblong.jelly.SlawIO.YamlOptions;
import com.oblong.jelly.SlawReader;
import com.oblong.jelly.SlawWriter;
import com.oblong.jelly.slaw.SlawFactory;
import com.oblong.jelly.slaw.java.JavaSlawFactory;

/**
 *
 * Created: Fri Oct  1 00:22:39 2010
 *
 * @author jao
 */
@Immutable
public final class FileIO {

    public static SlawReader reader(String fileName) throws IOException {
        final PushbackInputStream is =
            new PushbackInputStream(new FileInputStream(fileName));
        final BinaryFileHeader header = BinaryFileHeader.read(is);
        return header == null
            ? yamlReader(new InputStreamReader(is))
            : binaryReader(is, header.isLittleEndian());
    }

    public static SlawWriter binaryWriter(String fileName)
        throws IOException {
        final FileOutputStream os = new FileOutputStream(fileName);
        HEADER.write(os);
        return new StreamWriter(os, Format.BINARY, new BinaryExternalizer());
    }

    public static SlawWriter yamlWriter(String fileName,
                                        Set<YamlOptions> opts)
        throws IOException {
        final FileOutputStream os = new FileOutputStream(fileName);
        if (!opts.contains(YamlOptions.NO_DIRECTIVES)) {
            os.write("%YAML 1.1\n".getBytes());
            os.write("%TAG ! tag:oblong.com,2009:slaw/\n".getBytes());
        }
        return new StreamWriter(os, Format.YAML, new YamlExternalizer(opts));
    }

    private static SlawReader yamlReader(Reader reader) {
        return new YamlReader(reader, factory);
    }

    private static SlawReader binaryReader(InputStream is, boolean le)
        throws IOException {
        return new BinaryReader(is, new BinaryInternalizer(), le, factory);

    }

    private FileIO() {}

    private static final BinaryFileHeader HEADER = new BinaryFileHeader();
    private static final SlawFactory factory = new JavaSlawFactory();
}
