// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import static org.junit.Assert.*;

class SlawTests {

    static final void testIlk(Slaw s, SlawIlk ilk, NumericIlk nilk) {
        assertNotNull(s);
        assertEquals(s, s);
        assertEquals(ilk, s.ilk());
        assertEquals(nilk, s.numericIlk());
    }

    static final void testListness(Slaw s) {
        int count = s.count();

        assertTrue(count == 1 || !s.isAtomic());

        for (int i = 0; i < count; i++) {
            assertNotNull(s.nth(i));
        }

        int k = 0;
        for (Slaw ss : s) {
            assertNotNull(ss);
            assertEquals(s.nth(k++), ss);
        }
        assertEquals(s.count(), k);

        for (int i = count; i < count + 3; i++) {
            try {
                s.nth(i);
                fail();
            } catch (IndexOutOfBoundsException e) {
                // good
            }
        }

        try {
            s.nth(-1);
            fail();
        } catch (IndexOutOfBoundsException e) {
            // good
        }
    }

    static final void testAtomicEmissions(Slaw s) {
        try {
            s.emitBoolean();
            assertTrue(s.isBoolean());
        } catch (UnsupportedOperationException e) {
            assertFalse(s.isBoolean());
        }

        try {
            s.emitString();
            assertTrue(s.isString());
        } catch (UnsupportedOperationException e) {
            assertFalse(s.isString());
        }

        try {
            s.emitLong();
            s.emitDouble();
            s.emitBigInteger();
            assertTrue(s.isNumber());
        } catch (UnsupportedOperationException e) {
            assertFalse(s.isNumber());
        }
    }

    static final void testPairiness(Slaw s) {
        Slaw first = null;
        try {
            first = s.car();
        } catch (UnsupportedOperationException e) {
            assertTrue(s.count() < 1);
        }
        try {
            Slaw second = s.cdr();
            assertEquals(s.nth(0), first);
            if (s.count() > 1) assertEquals(s.nth(1), second);
            else assertEquals(0, second.count());
        } catch (UnsupportedOperationException e) {
            assertTrue(s.count() < 2);
        }
    }

    static final void testNotMap(Slaw s) {
        assertEquals(0, s.emitMap().size());
    }
}
