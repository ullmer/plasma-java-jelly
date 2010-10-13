// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.math.BigInteger;

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

    void testNumber(Slaw s) {
        assertTrue(s.isNumeric());
        assertTrue(s.isNumber());
        SlawTests.testListness(s);
        SlawTests.testAtomicEmissions(s);
        SlawTests.testPairiness(s);
        SlawTests.testNotMap(s);
    }

}
