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
import static com.oblong.jelly.SlawIO.Format.*;

/**
 * Unit Test for class SlawIO
 *
 *
 * @author jao
 */
public class SlawIOTest {

    @BeforeClass public static void initDef() throws IOException {
        defFile = File.createTempFile("jelly-slawio", "");
        // defFile = new File("/tmp/jelly");
        defFileName = defFile.getAbsolutePath();
    }

    @After public void tearDown() {
        defFile.delete();
    }

    @Test public void binary() throws IOException {
        readWriteTest(BINARY);
    }

    @Test public void yaml() throws IOException {
        readWriteTest(YAML);
    }

    private final void readWriteTest(Format fmt) throws IOException {
        readWriteTest(fmt, noSlaw);
        readWriteTest(fmt, oneProtein);
        readWriteTest(fmt, SlawTests.numbers());
        readWriteTest(fmt, SlawTests.complexes());
        readWriteTest(fmt, SlawTests.vectors());
        readWriteTest(fmt, SlawTests.emptyArrays());
        readWriteTest(fmt, SlawTests.arrays(SlawTests.numbers()));
        for (int d = 2; d < 5; ++d) {
            readWriteTest(fmt, SlawTests.arrays(SlawTests.numberVectors(d)));
            readWriteTest(fmt, SlawTests.arrays(SlawTests.complexVectors(d)));
        }
        readWriteTest(fmt, someSlawx);
    }

    private final void readWriteTest(Format fmt, Slaw[] slawx)
        throws IOException {
        final SlawWriter writer = SlawIO.writer(defFileName, fmt);
        assertEquals(fmt, writer.format());
        for (Slaw s : slawx) assertTrue(writer.write(s));
        assertTrue(writer.close());
        checkReader(fmt, slawx);

        assertTrue(SlawIO.write(slawx, defFileName, fmt));
        checkReader(fmt, slawx);
    }

    private final void checkReader(Format fmt, Slaw[] slawx)
        throws IOException {
        final SlawReader reader = SlawIO.reader(defFileName);
        assertEquals(fmt, reader.format());
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

    private static final Slaw[] someSlawx = {
        bool(true), bool(false), nil(),
        string("astring"), string("a longer string this, with \" thingie's"),
        cons(int32(1), nil()),
        list(), list(unt16(1), nil(), string("helluva \"quoted\"")),
        map(nil(), nil(), int8(1), string("a")),
        protein(null, null),
        protein(list(int8(1), int32(2)), map(string("a"), nil()))
    };

    private static final Slaw[] noSlaw = new Slaw[0];

    private static final Slaw[] oneProtein = {
        protein(int8(1), map(nil(), nil()))
    };
}
