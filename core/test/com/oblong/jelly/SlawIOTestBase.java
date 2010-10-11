// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.oblong.jelly.Slaw.*;
import com.oblong.jelly.SlawIO.Format;

/**
 * Base class for SlawIO unit tests.
 *
 * @author jao
 */
public class SlawIOTestBase {

    SlawIOTestBase(Format f) { format = f; }

    @BeforeClass public static void initDef() throws IOException {
        // defFile = File.createTempFile("jelly-slawio", "");
        defFile = new File("/tmp/jelly");
        defFileName = defFile.getAbsolutePath();
    }

    @After public void tearDown() {
        // defFile.delete();
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

    private final void readWriteTest(Slaw[] slawx) throws IOException {
        final SlawWriter writer = SlawIO.writer(defFileName, format);
        assertEquals(format, writer.format());
        for (Slaw s : slawx) assertTrue(writer.write(s));
        assertTrue(writer.close());
        checkReader(slawx);

        assertTrue(SlawIO.write(slawx, defFileName, format));
        checkReader(slawx);
    }

    private final void checkReader(Slaw[] slawx) throws IOException {
        final SlawReader reader = SlawIO.reader(defFileName);
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

        List<Slaw> slawx2 = SlawIO.read(defFileName);
        assertArrayEquals(slawx, slawx2.toArray());
    }

    private static File defFile;
    private static String defFileName;

    private static final Slaw[] atomicSlawx = {
        bool(true), bool(false), nil(),
        string("astring"), string("a longer string this, with \" thingie's"),
    };

    private static final Slaw[] compSlawx = {
        cons(int32(1), nil()),
        list(),
        list(unt16(1), nil(), string("helluva \"quoted\"")),
        map(nil(), nil(), int8(1), string("a")),
        protein(null, null),
        protein(list(int8(1), int32(2)), map(string("a"), nil()))
    };

    private static final Slaw[] nestedSlawx = {
        cons(compSlawx[0], compSlawx[4]),
        list(compSlawx[compSlawx.length-1], compSlawx[3], compSlawx[4]),
        map(compSlawx[3], compSlawx[4], compSlawx[2], compSlawx[2]),
        protein(compSlawx[0], compSlawx[5])
    };

    private static final Slaw[] noSlaw = new Slaw[0];

    private static final byte[] data = {
        1, 2, 3, 4, 5, 6, 7, 8, 9, 20, 65, -3, 5, -6, 93, 126, -127, 0, 0
    };

    private static final Slaw[] oneProtein = {
        protein(int8(1), map(nil(), nil()), data)
    };

    private final Format format;
}
