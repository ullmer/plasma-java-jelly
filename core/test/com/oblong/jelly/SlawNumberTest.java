// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Unit Test for Slaw numbers.
 *
 * Created: Sat May  8 01:51:58 2010
 *
 * @author jao
 */
public class SlawNumberTest {

    @Test public void numbers() {
        for (Slaw n : SlawTests.numbers(true)) testNumber(n);
    }

    @Test public void byteArrays() {
        final byte[] elems = new byte[256];
        for (int i = 0; i < 256; ++i) elems[i] = (byte)i;
        testByteArray(elems, true);
        testByteArray(elems, false);
    }

    private void testNumber(Slaw s) {
        assertTrue(s.isNumeric());
        assertTrue(s.isNumber());
        SlawTests.testListness(s);
        SlawTests.testAtomicEmissions(s);
        SlawTests.testPairiness(s);
        SlawTests.testNotMap(s);
    }

    private void testByteArray(byte[] elems, boolean signed) {
        final Slaw s = Slaw.array(elems, signed);
        assertTrue(s.isByteArray());
        assertEquals(signed, s.numericIlk().isSigned());
        assertEquals(8, s.numericIlk().width());
        assertEquals(elems.length, s.count());
        for (int i = 0; i < elems.length; ++i)
            assertEquals(elems[i], s.nth(i).emitByte());
        assertArrayEquals(elems, s.emitByteArray());
        assertArrayEquals(elems, s.unsafeEmitByteArray());
    }
}
