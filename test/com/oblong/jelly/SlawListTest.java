// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *  Unit Test for class SlawList
 *
 * Created: Mon May 10 16:08:48 2010
 *
 * @author jao
 */
public class SlawListTest {

    @Test public void emptylist() {
        Slaw ls = Slaw.list();
        assertTrue(ls.isList());
        assertEquals(0, ls.count());
        assertEquals(0, ls.emitList().size());
        assertEquals(0, ls.emitMap().size());
        assertEquals(Slaw.list(), ls);
        assertEquals(ls, Slaw.list((Slaw)null));
        assertEquals(ls, Slaw.list(null, null));
        try {
            ls.car();
            fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            ls.cdr();
            fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    @Test public void singleton() {
        Slaw n = Slaw.int8(23);
        Slaw ls = Slaw.list(n);
        assertTrue(ls.isList());
        assertEquals(Slaw.list(Slaw.int8(23)), ls);
        assertEquals(Slaw.list(Slaw.int8(23), null), ls);
        assertEquals(n, ls.get(0));
        assertEquals(n.emitLong(), ls.emitLong());
        assertEquals(n.emitDouble(), ls.emitDouble(), 0.0);
        assertEquals(n.emitBigInteger(), ls.emitBigInteger());
        assertEquals(n, ls.car());
        assertEquals(Slaw.list(), ls.cdr());
        assertEquals(null, ls.get(n));
        assertEquals(0, ls.emitMap().size());
    }

    @Test public void lists() {
        testList(Slaw.int32(3), Slaw.string("foo"));
        testList(Slaw.int32(3), null, Slaw.string("foo"));
        testList(Slaw.list(Slaw.unt16(234), null, null, Slaw.string("")),
                 Slaw.unt64(-1), Slaw.cons(Slaw.int8(2), Slaw.string("bar")));
    }

    private void testList(Slaw... sx) {
        listTests(Slaw.list(sx));
    }

    private void listTests(Slaw ls) {
        assertTrue(ls.isList());
        List<Slaw> sl = ls.emitList();
        Slaw lc = Slaw.list(sl);
        assertEquals(ls, lc);
        for (int i = 0, c = ls.count(); i < c; ++i) {
            assertEquals(sl.get(i), ls.get(i));
            assertEquals(i, ls.indexOf(ls.get(i)));
        }
        Map<Slaw,Slaw> m = ls.emitMap();
        assertEquals(ls.count()/2, m.size());
        for (int i = 0, c = ls.count() - 1; i < c; i += 2) {
            assertEquals(ls.get(i+1), m.get(ls.get(i)));
        }
        if (ls.count() > 0) {
            assertEquals(ls.get(0), ls.car());
            Slaw[] csx = new Slaw[ls.count() - 1];
            for (int i = 0; i < csx.length; i++) csx[i] = ls.get(i+1);
            assertEquals(Slaw.list(csx), ls.cdr());
        }
    }
}
