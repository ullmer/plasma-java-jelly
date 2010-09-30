// Copyright (c) 2010 Oblong Industries
// Created: Wed Sep 29 23:45:26 2010

package com.oblong.jelly;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;

import com.oblong.jelly.slaw.io.Factory;

/**
 * Factory class defining methods for creating SlawReader and SlawWriter
 * instances.
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
        /** Don't emit tags for numbers */
        NO_TAGS,
        /** Don't emit directives */
        NO_DIRECTIVES,
        /** Don't order maps */
        UNORDERED_MAPS;

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
     * @see SlawReader
     */
    public static SlawReader reader(String fileName) throws IOException {
        return Factory.reader(fileName);
    }

    /**
     *
     */
    public static SlawWriter writer(String fileName, Format format)
        throws IOException {
        switch (format) {
        case BINARY:
            return Factory.binaryWriter(fileName);
        case YAML:
            return writer(fileName, YamlOptions.DEFAULTS);
        }
        return null;
    }

    public static SlawWriter writer(String fileName, Set<YamlOptions> opts)
        throws IOException {
        return null;
    }

    private SlawIO() {}
}
