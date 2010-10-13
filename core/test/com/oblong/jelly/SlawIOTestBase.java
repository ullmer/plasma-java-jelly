// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

import static com.oblong.jelly.Slaw.*;
import com.oblong.jelly.SlawIO.Format;
import com.oblong.jelly.SlawIO.YamlOptions;

/**
 * Base class for SlawIO unit tests.
 *
 * @author jao
 */
public class SlawIOTestBase {

    public SlawIOTestBase() { this(null); }

    SlawIOTestBase(Format f) { format = f; }

    @Before public void hasFormat() {
    	assumeTrue(format != null);
    }

    @Test public void empty() throws IOException {
        readWriteTest(noSlaw);
    }

    @Test public void singleton() throws IOException {
        readWriteTest(oneProtein);
    }

    @Test public void atoms() throws IOException {
        readWriteTest(atomicSlawx);
    }

    @Test public void composites() throws IOException {
        readWriteTest(compSlawx);
    }

    @Test public void nested() throws IOException {
        readWriteTest(nestedSlawx);
    }

    @Test public void numbers() throws IOException {
        readWriteTest(SlawTests.numbers());
    }

    @Test public void complexes() throws IOException {
        readWriteTest(SlawTests.complexes());
    }

    @Test public void vectors() throws IOException {
        readWriteTest(SlawTests.vectors());
    }

    @Test public void arrays() throws IOException {
        readWriteTest(SlawTests.emptyArrays());
        readWriteTest(SlawTests.arrays(SlawTests.numbers()));
        for (int d = 2; d < 5; ++d) {
            readWriteTest(SlawTests.arrays(SlawTests.numberVectors(d)));
            readWriteTest(SlawTests.arrays(SlawTests.complexVectors(d)));
        }
    }

    @Test public void files() throws IOException {
        fileTest(nestedSlawx, null);
    }

    final void readWriteTest(Slaw[] slawx) throws IOException {
        readWriteTest(slawx, null);
    }

    final void readWriteTest(Slaw[] slawx, YamlOptions opts)
        throws IOException {
        final byte[] data = format == Format.BINARY ?
            SlawIO.toBytes(slawx) : SlawIO.toString(slawx, opts).getBytes();
        checkReader(slawx, SlawIO.reader(data));
        assertArrayEquals(slawx, SlawIO.fromBytes(data).toArray());
    }

    final void fileTest(Slaw[] slawx, YamlOptions opts) throws IOException {
        fileTest(slawx,
                 File.createTempFile("jelly-slawio-test", "." + format),
                 opts);
    }

    final void fileTest(Slaw[] slawx, File file, YamlOptions opts)
        throws IOException {
        final boolean bin = format == Format.BINARY;
        final String fileName = file.getAbsolutePath();
        final SlawWriter writer = bin
            ? SlawIO.writer(fileName, format) : SlawIO.writer(fileName, opts);
        checkWriter(slawx, writer);
        checkReader(slawx, SlawIO.reader(fileName));

        assertArrayEquals(slawx, SlawIO.read(fileName).toArray());

        if (bin) assertTrue(SlawIO.write(slawx, fileName, format));
        else assertTrue(SlawIO.write(slawx, fileName, opts));

        assertArrayEquals(slawx, SlawIO.read(fileName).toArray());

        file.delete();
    }

    final void checkWriter(Slaw[] slawx, SlawWriter writer)
        throws IOException {
        assertEquals(format, writer.format());
        for (Slaw s : slawx) assertTrue(writer.write(s));
        assertTrue(writer.close());
    }

    final void checkReader(Slaw[] slawx, SlawReader reader)
        throws IOException {
        assertEquals(format, reader.format());
        for (int i = 0; i < slawx.length; ++i) {
            assertTrue(reader.hasNext());
            final Slaw s = reader.next();
            assertEquals(s.toString(), slawx[i], s);
        }
        if (reader.hasNext()) {
            fail(reader.next().toString());
        }
        assertNull(reader.next());
        assertTrue(reader.close());
    }

    static final Slaw[] noSlaw = new Slaw[0];

    static final byte[] data = {
        1, 2, 3, 4, 5, 6, 7, 8, 9, 20, 65, -3, 5, -6, 93, 126, -127, 0, 0
    };

    static final Slaw[] oneProtein = {
        protein(list(int8(1), string("foo"), bool(false)),
                map(string("a"), nil(), string("b"), unt64(2)),
                data)
    };

    static final Slaw[] atomicSlawx = {
        bool(true), bool(false), nil(), int8(123), unt32(0),
        string("astring"), string("a longer string this, with \" thingie's"),
    };

    static final Slaw[] compSlawx = {
        cons(int32(1), nil()),
        list(),
        list(unt16(1), nil(), string("helluva \"quoted\"")),
        map(nil(), nil(), int8(1), string("a")),
        protein(null, null),
        protein(list(int8(1), int32(2)), map(string("a"), nil()))
    };

    static final Slaw[] nestedSlawx = {
        cons(compSlawx[0], compSlawx[4]),
        list(compSlawx[compSlawx.length-1], compSlawx[3], compSlawx[4]),
        list(oneProtein[0], oneProtein[0], compSlawx[compSlawx.length - 1]),
        map(compSlawx[3], compSlawx[4], compSlawx[2], compSlawx[2]),
        protein(compSlawx[0], compSlawx[5]),
        protein(oneProtein[0], compSlawx[compSlawx.length - 1], data)
    };

    final Format format;
}
