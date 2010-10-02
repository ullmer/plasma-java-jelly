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
        defFileName = defFile.getAbsolutePath();
    }

    @After public void tearDown() {
        defFile.delete();
    }

    @Test public void binary() throws IOException {
        readWriteTest(BINARY);
    }

    private final void readWriteTest(Format fmt) throws IOException {
        readWriteTest(fmt, noSlaw);
        readWriteTest(fmt, someSlawx);
        readWriteTest(fmt, oneProtein);
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
            assertEquals(slawx[i], reader.next());
        }
        assertFalse(reader.hasNext());
        assertNull(reader.next());
        assertTrue(reader.close());

        List<Slaw> slawx2 = SlawIO.read(defFileName);
        assertArrayEquals(slawx, slawx2.toArray());
    }

    private static File defFile;
    private static String defFileName;

    private static final Slaw[] someSlawx = {
        int8(3), bool(true), nil(), unt64(123),
        string("astring"), string("a longer string this"),
        cons(int32(1), nil()), list(unt16(1)),
        map(nil(), nil(), int8(1), string("a")),
        protein(null, null)
    };

    private static final Slaw[] noSlaw = new Slaw[0];
    private static final Slaw[] oneProtein = {
        protein(int8(1), map(nil(), nil()))
    };
}
