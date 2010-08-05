// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.Map;

import static org.junit.Assert.*;
import org.junit.Test;

import static com.oblong.jelly.Slaw.*;
import static com.oblong.jelly.SlawTests.*;

/**
 *  Unit Test for class SlawComplex
 *
 *
 * Created: Sat May  8 03:55:07 2010
 *
 * @author jao
 */
public class SlawComplexTest {

    @Test public void nonNumbers() {
        Slaw[] sx = {
            nil(), bool(true), bool(false),
            string("foo"), complex(int8(1), int32(3))
        };
        for (Slaw s : sx) {
            testNonNumber(s, s);
            testNonNumber(s, Slaw.int32(42));
            testNonNumber(Slaw.float32(3.1F), s);
        }
    }

    @Test public void complexes() {
        Slaw[] sx = {
            int8(1), unt8(23), int16(-23), unt16(2034),
            int64(-232343245), unt64(32), float32(3.141592F), float64(1)
        };
        for (Slaw r : sx) {
            for (Slaw i : sx) {
                testComplex(complex(r, i));
            }
        }
    }

    private void testComplex(Slaw c) {
        assertEquals(c, c);
        assertEquals(2, c.count());
        assertEquals(c, complex(c.car(), c.cdr()));
        assertEquals(c.numericIlk(), c.car().numericIlk());
        assertEquals(c.numericIlk(), c.cdr().numericIlk());
        testAtomicEmissions(c);
        testPairiness(c);
        testListness(c);
        Map<Slaw,Slaw> m = c.emitMap();
        assertEquals(c.cdr(), m.get(c.car()));
        assertEquals(c.cdr(), c.find(c.car()));
    }

    private void testNonNumber(Slaw r, Slaw i) {
        try {
            Slaw.complex(r, i);
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
}
