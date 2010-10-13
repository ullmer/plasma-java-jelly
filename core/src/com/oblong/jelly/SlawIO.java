// Copyright (c) 2010 Oblong Industries
// Created: Wed Sep 29 23:45:26 2010

package com.oblong.jelly;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import com.oblong.jelly.slaw.io.IOFactory;

/**
 * Utilities for serializing Slaw instances to files, strings and byte
 * arrays.
 *
 * <p> Serialization can be piecemeal, via a {@link SlawWriter}
 * instance, or performed in a single, atomic operation using {@link
 * #write}. Reading the resulting data can be accomplished via a Slaw
 * iterator, i.e., an instance of {@link SlawReader}. It's also
 * possible to read all slawx in one shot using {@link #read}.
 *
 * <p> Slawx can be serialized in any of the formats enumerated by
 * {@link SlawIO.Format}, which will be automatically recognised by
 * readers.
 *
 * @author jao
 */
public final class SlawIO {
    /**
     * Available Slaw serialization formats.
     */
    public enum Format {
        BINARY, YAML
    }

    /**
     * Options for customizing YAML output.
     */
    public static class YamlOptions {
        /**
         * Default configuration, emitting tags and YAML directives.
         */
        public YamlOptions() {
            this(true, true);
        }

        /**
         * Customized configuration. See {@link #emitTags()} and
         * {@link #useDirectives()} for the meaning of the passed
         * flags.
         */
        public YamlOptions(boolean tags, boolean directives) {
            emitTags = tags;
            useDirectives = directives;
        }

        /**
         * When this is <code>false</code>, YAML tags for numbers
         * won't be emitted. That means that you'll lose information
         * on the concrete numeric ilks of externalized slawx.
         */
        public boolean emitTags() { return emitTags; }

        /**
         * Setter for the {@link #emitTags() emitTags} flags, returning
         * a reference to this for easy channing.
         */
        public YamlOptions emitTags(boolean tags) {
            emitTags = tags;
            return this;
        }

        /*
         * Whether to emit directives. By default, the YAML document
         * will contain directives on YAML version and Oblong's
         * namespace. Set <code>directives</code> to
         * <code>false</code> to skip them.
         */
        public boolean useDirectives() { return useDirectives; }

        /**
         * Setter for the {@link #useDirectives() useDirectives}
         * flags, returning a reference to this for easy channing.
         */
        public YamlOptions useDirectives(boolean directives) {
            useDirectives = directives;
            return this;
        }

        private boolean useDirectives;
        private boolean emitTags;
    }

    /**
     * Factory method creating a reader associated with a file, given
     * its path.
     *
     * <p> The reader will recognise the format of the data (binary or
     * YAML) automatically.
     *
     * @throws IOException is the requested file does not exist or
     * cannot be read for any other reason.
     *
     * @see #read for atomically reading all slawx in a file.
     *
     * @see #stringReader
     * @see #reader(byte[])
     * @see SlawReader
     */
    public static SlawReader reader(String fileName) throws IOException {
        return IOFactory.reader(new FileInputStream(fileName));
    }

    /**
     * A reader getting its data from a given byte array.
     *
     * @see #reader(String)
     * @see SlawReader
     */
    public static SlawReader reader(byte[] data) {
        try {
            return IOFactory.reader(new ByteArrayInputStream(data));
        } catch (IOException e) {
            // cannot happen
            return null;
        }
    }

    /**
     * A reader getting its data from a given string. Equivalent to
     * calling {@link #reader(byte[])} with <code>data.getBytes()</code>.
     */
    public static SlawReader stringReader(String data) {
        return reader(data.getBytes());
    }

    /**
     * Convenience method reading all slawx in the given file and
     * returning them as a list. If the file does not contain any Slaw
     * or is not in a recognisable format, the returned list will be
     * empty.
     *
     * @throws IOException is the requested file does not exist or
     * cannot be read for any other reason.
     */
    public static List<Slaw> read(String fileName) throws IOException {
        return read(reader(fileName));
    }

    /**
     * De-serialize a list of Slawx serialized in YAML format with
     * {@link #toString(Slaw[], YamlOptions) toString}.
     *
     * @throws IOException if the input cannot be decoded.
     */
    public static List<Slaw> fromString(String yaml) throws IOException {
        return fromBytes(yaml.getBytes());
    }

    /**
     * De-serialize a list of Slawx serialized in YAML format with
     * {@link #toBytes}.
     *
     * @throws IOException if the input cannot be decoded.
     */
    public static List<Slaw> fromBytes(byte[] bin) throws IOException {
        return read(reader(bin));
    }

    /**
     * Factory method creating a writer associated with the given
     * file.
     *
     * The file will be created or, if it exists, truncated to zero
     * length. If the given format is YAML, the default {@link
     * YamlOptions} will be used.
     *
     * @throws IOException when the file cannot be created or written
     * to.
     *
     * @see #write for atomically writing an array of Slawx without
     * using an intermediate writer.
     *
     * @see SlawWriter
     */
    public static SlawWriter writer(String fileName, Format format)
        throws IOException {
        return IOFactory.writer(new FileOutputStream(fileName), format, null);
    }

    /**
     * Factory method creating a writer with output to the given file
     * and YAML format. The second argument allows fine tunning of the
     * output, and can be safely null to use the default options.
     *
     * @throws IOException when the file cannot be created or written
     * to.
     *
     * @see #write for atomically writing an array of Slawx without
     * using an intermediate writer.
     *
     * @see #toString for serializing to a string instead of a file.
     *
     * @see SlawWriter
     */
    public static SlawWriter writer(String fileName, YamlOptions opts)
        throws IOException {
        return IOFactory.yamlWriter(new FileOutputStream(fileName), opts);
    }

    /**
     * Convenience method writing an array of slawx in one shot. If
     * the given file exists, it'll be overwritten.
     *
     * <p> For YAML output, you can also use
     * {@link #write(Slaw[], String, YamlOptions) this write method}.
     *
     * @throws IOException when the file cannot be created or written
     * to. Otherwise, the return value indicates whether all slawx
     * were written.
     */
    public static boolean write(Slaw[] slawx, String fileName, Format format)
        throws IOException {
        return write(slawx, writer(fileName, format));
    }

    /**
     * Convenience method serializing an array of slawx to a bytes, in
     * binary format.
     *
     * @see #toBytes(Slaw)
     */
    public static byte[] toBytes(Slaw[] slawx) {
        return toArray(slawx, Format.BINARY, null).toByteArray();
    }

    /**
     * Convenience method serializing an slaw to a bytes, in binary
     * format.
     *
     * @see #toBytes(Slaw[])
     *
     */
    public static byte[] toBytes(Slaw slaw) {
        final Slaw[] slawx = {slaw};
        return toArray(slawx, Format.BINARY, null).toByteArray();
    }

    /**
     * Convenience method writing an array of slawx in one shot, in
     * YAML format, with the specified options. If the given file
     * exists, it'll be overwritten.
     *
     * @throws IOException when the file cannot be created or written
     * to. Otherwise, the return value indicates whether all slawx
     * were written.
     *
     * @see #toString for serializing to a string instead of a file.
     */
    public static boolean write(Slaw[] slawx,
                                String fileName,
                                YamlOptions opts) throws IOException {
        return write(slawx, writer(fileName, opts));
    }

    /**
     * Convenience method writing an array of slawx in one shot to an
     * string, in YAML format, with the specified options.
     *
     * @see #toString(Slaw)
     */
    public static String toString(Slaw[] slawx, YamlOptions opts) {
        return toArray(slawx, Format.YAML, opts).toString();
    }

    /**
     * Convenience method serializing a slaw to an string, in YAML
     * format, with the specified options.
     *
     * @see #toString(Slaw[])
     */
    public static String toString(Slaw slaw, YamlOptions opts) {
        final Slaw[] slawx = {slaw};
        return toArray(slawx, Format.YAML, opts).toString();
    }

    private static List<Slaw> read(SlawReader reader) throws IOException {
        final List<Slaw> result = new ArrayList<Slaw>();
        while (reader.hasNext()) result.add(reader.next());
        reader.close();
        return result;
    }

    private static boolean write(Slaw[] slawx, SlawWriter writer) {
        boolean result = true;
        for (Slaw s : slawx) result= writer.write(s) && result;
        result = writer.close() && result;
        return result;
    }

    public static ByteArrayOutputStream toArray(Slaw[] s,
                                                Format f,
                                                YamlOptions o) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            write(s, IOFactory.writer(os, f, o));
        } catch (IOException e) { // this error should never happen
        }
        return os;
    }

    private SlawIO() {}
}
