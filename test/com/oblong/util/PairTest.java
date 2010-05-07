// Copyright (c) 2010 Oblong Industries

package com.oblong.util;

import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;
import java.math.BigDecimal;

/**
 *  Unit Test for class Pair
 *
 * Created: Thu Apr 15 18:08:19 2010
 *
 * @author jao
 */
public class PairTest {

    void assertNotEquals(Object o1, Object o2) {
        assertFalse(o1.equals(o2));
        assertFalse(o2.equals(o1));
    }

    @Test public void equality() {
        Pair<Integer,BigDecimal> p0 =
            Pair.create(new Integer(3), new BigDecimal(3.14));
        Pair<BigDecimal,Integer> p1 = Pair.create(p0.second(), p0.first());
        Pair<BigDecimal,Integer> p2 = Pair.create(p1.first(), p1.second());
        Pair<BigDecimal,Integer> p3 = Pair.create(p2.first(), p2.second());

        assertEquals(p0, p0);
        assertNotEquals(p0, p1);
        assertNotEquals(p1, p0);
        assertNotEquals(p0, p2);
        assertNotEquals(p2, p0);

        assertTrue(p1.equals(p2));
        assertTrue(p2.equals(p1));
        assertTrue(p2.equals(p3));
        assertTrue(p1.equals(p3));
    }
}
