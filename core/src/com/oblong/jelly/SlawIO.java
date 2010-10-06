// Copyright (c) 2010 Oblong Industries
// Created: Wed Sep 29 23:45:26 2010

package com.oblong.jelly;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import com.oblong.jelly.slaw.io.FileIO;

/**
 * Utilities for serializing Slaw instances to files.
 *
 * <p> Serialization can be piecemeal, via a {@link SlawWriter}
 * instance, or performed in a single, atomic operation using {@link
 * #write}. Reading the resulting files can be accomplished via a Slaw
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
     * Flags for customizing YAML output.
     */
    public enum YamlOptions {
        /** Don't emit tags for numbers. */
        NO_NUMBER_TAGS,
        /** Don't emit YAML directives. */
        NO_DIRECTIVES;
        // /* Don't order maps (i.e., use !!map instead of !!omap). */
        // UNORDERED_MAPS;

        /**
         * Default option set, containing none of the flags above.
         */
        public static final Set<YamlOptions> DEFAULTS =
           EnumSet.noneOf(YamlOptions.class);
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
     * @see SlawReader
     */
    public static SlawReader reader(String fileName) throws IOException {
        return FileIO.reader(fileName);
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
        final SlawReader reader = reader(fileName);
        final List<Slaw> result = new ArrayList<Slaw>();
        while (reader.hasNext()) result.add(reader.next());
        reader.close();
        return result;
    }

    /**
     * Factory method creating a writer associated with the given
     * file.
     *
     * <p> The file will be created or, if it exists, truncated to
     * zero length. If the given format is YAML, none of the options
     * available in {@link YamlOptions} will be used.
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
        switch (format) {
        case BINARY:
            return FileIO.binaryWriter(fileName);
        case YAML:
            return writer(fileName, YamlOptions.DEFAULTS);
        }
        return null;
    }

    public static SlawWriter writer(String fileName, Set<YamlOptions> opts)
        throws IOException {
        return FileIO.yamlWriter(fileName, opts);
    }

    /**
     * Convenience method writing an array of slawx in one shot. If
     * the given file exists, it'll be overwritten.
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
     * Convenience method writing an array of slawx in one shot, in
     * YAML format, with the specified options. If the given file
     * exists, it'll be overwritten.
     *
     * @throws IOException when the file cannot be created or written
     * to. Otherwise, the return value indicates whether all slawx
     * were written.
     */
    public static boolean write(
        Slaw[] slawx, String fileName, Set<YamlOptions> opts)
        throws IOException {
        return write(slawx, writer(fileName, opts));
    }


    private static boolean write(Slaw[] slawx, SlawWriter writer) {
        boolean result = true;
        for (Slaw s : slawx) result= writer.write(s) && result;
        result = writer.close() && result;
        return result;
    }

    private SlawIO() {}
}
