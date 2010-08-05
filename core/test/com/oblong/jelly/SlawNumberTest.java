// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.math.BigInteger;

import static org.junit.Assert.*;
import org.junit.Test;

import static com.oblong.jelly.SlawTests.*;

/**
 *  Unit Test for class SlawNumber
 *
 *
 * Created: Sat May  8 01:51:58 2010
 *
 * @author jao
 */
public class SlawNumberTest {

    void testNumber(Slaw s, NumericIlk i) {
        assertTrue(s.isNumeric());
        assertTrue(s.isNumber());
        testIlk(s, SlawIlk.NUMBER, i);
        testListness(s);
        testAtomicEmissions(s);
        testPairiness(s);
        testNotMap(s);
    }

    void testIntegral(NumericIlk i, long v) {
        Slaw s = Slaw.number(i, v);
        assertEquals("Checking " + i, v, s.emitLong());
        assertEquals("Checking " + i,
                     BigInteger.valueOf(v), s.emitBigInteger());
        testNumber(s, i);
    }

    void testFloat(NumericIlk i, double v) {
        Slaw s = Slaw.number(i, v);
        assertEquals("Checking " + i, v, s.emitDouble(), 0.0);
        testNumber(s, i);
    }

    @Test public void integrals() {
        for (NumericIlk i : NumericIlk.values()) {
            if (i.isIntegral()) {
                testIntegral(i, i.max());
                testIntegral(i, i.min());
                testIntegral(i, i.min() / 2 + i.max() / 2);
            } else {
                testFloat(i, i.fmax());
                testFloat(i, i.fmin());
                testFloat(i, i.fmin() / 2 + i.fmax() / 2);
                testFloat(i, 0.0);
            }
        }
    }

}
