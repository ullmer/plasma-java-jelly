// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.io;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import net.jcip.annotations.Immutable;

import org.yaml.snakeyaml.reader.UnicodeReader;

import com.oblong.jelly.SlawIO.Format;
import static com.oblong.jelly.SlawIO.Format.*;
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
public final class IOFactory {

    public static SlawReader reader(InputStream input) throws IOException {
        final PushbackInputStream is = new PushbackInputStream(input);
        final BinaryFileHeader header = BinaryFileHeader.read(is);
        return header == null
            ? yamlReader(new UnicodeReader(is))
            : binaryReader(is, header.isLittleEndian());
    }

    public static SlawWriter writer(OutputStream os,
                                    Format format,
                                    YamlOptions opts) throws IOException {
        switch (format) {
        case BINARY: return binaryWriter(os);
        case YAML: return yamlWriter(os, opts);
        default: assert false : format.toString(); return null;
        }
    }

    public static SlawWriter binaryWriter(OutputStream os)
        throws IOException {
        HEADER.write(os);
        return new StreamWriter(new BufferedOutputStream(os),
                                Format.BINARY,
                                new BinaryExternalizer());
    }

    public static SlawWriter yamlWriter(OutputStream os, YamlOptions opts)
    	throws IOException {
        return new YamlWriter(os, opts == null ? new YamlOptions() : opts);
    }

    private static SlawReader yamlReader(UnicodeReader reader) {
        return new YamlReader(reader, factory);
    }

    private static SlawReader binaryReader(InputStream is, boolean le)
        throws IOException {
        return new BinaryReader(is, new BinaryInternalizer(), le, factory);

    }

    private IOFactory() {}

    private static final BinaryFileHeader HEADER = new BinaryFileHeader();
    private static final SlawFactory factory = new JavaSlawFactory();
}
