// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;

import static com.oblong.jelly.Slaw.*;
import static com.oblong.jelly.SlawTests.*;

/**
 *  Unit Test for class SlawVector
 *
 *
 * Created: Wed May 12 04:04:56 2010
 *
 * @author jao
 */
public class SlawVectorTest {

    @Test public void vectors() {
        testVector(vector(int32(2), int8(-3)));
        testVector(vector(unt64(239392), float64(3.12), unt8(2)));
        testVector(vector(unt32(-3452), unt8(1), int16(3452), int64(0)));
        testVector(vector(complex(int8(1), int8(2)),
                          complex(int32(-2), float32(12))));
        testVector(vector(complex(unt8(1), float32(2)),
                          complex(int32(23), float32(12)),
                          complex(float32(-2399.123F), int16(5))));
        testVector(vector(complex(int8(1), int8(2)),
                          complex(int32(-2), float32(12)),
                          complex(int8(1), int8(2)),
                          complex(int32(-2), float32(12))));
    }

    private void testVector(Slaw v) {
        assertTrue(v.isVector() || v.isComplexVector());
        assertEquals(v, v);
        Slaw v2 = Slaw.vector(v.nth(0), v.nth(1));
        assertTrue(v2.equals(v) || v.count() > 2);
        assertEquals(v2.nth(0), v.nth(0));
        assertEquals(v2.nth(1), v.nth(1));
        assertEquals(v2.ilk(), v.ilk());
        assertEquals(v2.numericIlk(), v.numericIlk());
        assertEquals(v.nth(0), v.car());
        Slaw cdr = v.cdr();
        assertEquals(v.count() - 1, cdr.count());
        for (int i = 1, c = v.count(); i < c; i++)
            assertEquals(v.nth(i), cdr.nth(i - 1));
        testListness(v);
        testAtomicEmissions(v);
        testNotMap(v);
    }

}
