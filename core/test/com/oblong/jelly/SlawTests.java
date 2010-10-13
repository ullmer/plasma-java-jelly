// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public final class SlawTests {

    public static Slaw[] numbers() { return numbers(false); }

    public static Slaw[] numbers(boolean check) {
        final List<Slaw> ns = new ArrayList<Slaw>();
        final Set<NumericIlk>
            nis = EnumSet.complementOf(EnumSet.of(NumericIlk.UNT64));
        for (NumericIlk ni : nis) {
            if (ni.isIntegral()) {
                add(ns, ni, ni.max(), check);
                add(ns, ni, ni.min(), check);
                add(ns, ni, 42, check);
                if (ni.isSigned()) {
                    add(ns, ni, -42, check);
                } else {
                    add(ns, ni, ni.min() + (ni.max() - ni.min()) / 2, check);
                }
            } else {
                add(ns, ni, ni.fmax(), check);
                add(ns, ni, ni.fmin(), check);
                add(ns, ni, 0, check);
                add(ns, ni, 3.141592653589793, check);
                add(ns, ni, -2.718281828459045, check);
            }
        }

        final BigInteger ml = BigInteger.valueOf(Long.MAX_VALUE);
        add(ns, ml.add(BigInteger.ONE), check);
        add(ns, ml.shiftLeft(1).subtract(BigInteger.ONE), check);
        add(ns, BigInteger.ZERO, check);

        return ns.toArray(new Slaw[0]);
    }

    static void add(List<Slaw> sl, NumericIlk ni, long v, boolean check) {
        final Slaw n = Slaw.number(ni, v);
        sl.add(n);
        if (check) assertEquals("Slaw was: " + n, v, n.emitLong());
    }

    static void add(List<Slaw> sl, NumericIlk ni, double v, boolean check) {
        final Slaw n = Slaw.number(ni, v);
        sl.add(n);
        if (check) assertEquals("Slaw was: " + n, v, n.emitDouble(), 0.00001);
    }

    static void add(List<Slaw> sl, BigInteger v, boolean check) {
        final Slaw n = Slaw.unt64(v);
        sl.add(n);
        if (check) assertEquals("Slaw was: " + n, v, n.emitBigInteger());
    }

    public static Slaw[] complexes() {
        final Slaw[] nos = numbers();
        final List<Slaw> cs = new ArrayList<Slaw>();
        for (int i = 0; i < nos.length / 2; ++i)
            cs.add(Slaw.complex(nos[i], nos[nos.length - 1 - i]));
        for (int i = 0; i < nos.length - 1; i += 2)
            cs.add(Slaw.complex(nos[i], nos[i + 1]));
        return cs.toArray(new Slaw[0]);
    }

    public static Slaw[] vectors() {
        final List<Slaw> cs = new ArrayList<Slaw>();
        for (Slaw nv : numberVectors(0)) cs.add(nv);
        for (Slaw cv : complexVectors(0)) cs.add(cv);
        return cs.toArray(new Slaw[0]);
    }

    public static Slaw[] numberVectors(int dimension) {
        return vectors(numbers(), dimension);
    }

    public static Slaw[] complexVectors(int dimension) {
        return vectors(complexes(), dimension);
    }

    public static Slaw[] vectors(Slaw[] nos, int d) {
        final List<Slaw> cs = new ArrayList<Slaw>();
        for (int i = 0; i < nos.length - 3; i += 4) {
            if (d == 2 || d == 0)
                cs.add(Slaw.vector(nos[i], nos[i+1]));
            if (d == 3 || d == 0)
                cs.add(Slaw.vector(nos[i], nos[i+1], nos[i+2]));
            if (d == 4 || d == 0)
                cs.add(Slaw.vector(nos[i], nos[i+1], nos[i+2], nos[i+3]));
        }
        return cs.toArray(new Slaw[0]);
    }

    public static Slaw[] arrays(Slaw[] ns) {
        assertTrue(SlawIlk.haveSameIlk(ns));
        final List<Slaw> arrays = new ArrayList<Slaw>();
        for (int i = 1; i < ns.length; ++i) {
            final Slaw[] cmps = new Slaw[i];
            for (int j = 0; j < i; ++j) cmps[j] = ns[j];
            arrays.add(Slaw.array(cmps));
        }
        return arrays.toArray(new Slaw[0]);
    }

    public static Slaw[] emptyArrays() {
        final List<Slaw> arrays = new ArrayList<Slaw>();
        for (SlawIlk i : SlawIlk.arrayIlks()) {
            for (NumericIlk ni : NumericIlk.values()) {
                final int minDim =
                    (i == SlawIlk.NUMBER_ARRAY || i == SlawIlk.COMPLEX_ARRAY)
                    ? 0 : 2;
                final int maxDim = minDim == 0
                    ? 0 : (i == SlawIlk.MULTI_VECTOR_ARRAY ? 5 : 4);
                for (int d = minDim; d <= maxDim; d++)
                    arrays.add(Slaw.array(i, ni, d));
            }
        }
        return arrays.toArray(new Slaw[0]);
    }

    public static void testIlk(Slaw s, SlawIlk ilk, NumericIlk nilk) {
        assertNotNull(s);
        assertEquals(s, s);
        assertEquals(ilk, s.ilk());
        assertEquals(nilk, s.numericIlk());
    }

    public static void testListness(Slaw s) {
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

    public static void testAtomicEmissions(Slaw s) {
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

    public static void testPairiness(Slaw s) {
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

    public static void testNotMap(Slaw s) {
        assertEquals(0, s.emitMap().size());
    }
}
