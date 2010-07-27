// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;


import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import org.junit.Test;

import static com.oblong.jelly.Slaw.*;
import static com.oblong.jelly.SlawTests.*;

/**
 * Unit Test for class Slaw: maps.
 *
 * Created: Wed May 12 19:18:42 2010
 *
 * @author jao
 */
public class SlawMapTest {

    @Test public void maps() {
        testMap(map());
        testMap(map(string("k"), string("v"),
                    string("k2"), bool(true),
                    nil(), int32(-2313)));
        testMap(map(list(int8(1), int8(2), int32(3))));
    }

    @Test public void equality() {
        Slaw[] sx = {string("a"), int8(2),
                     cons(string("foo"), bool(false)), nil(),
                     float32(3), float32(3)};
        Slaw m0 = map(sx[0], sx[1], sx[4], sx[5], sx[2], sx[3]);
        assertEquals(map(sx), m0);
        Slaw m1 = map(string("a"), int32(1), string("b"), int32(2));
        Slaw m2 = map(string("a"), int32(1), string("b"), int32(2));
        assertEquals(2, m1.count());
        assertEquals(m1, m2);
        assertEquals(m2, m1);
        String a = new String("a");
        String b = new String("b");
        Slaw m3 = map(string(b), int32(2), string(a), int32(1));
        assertEquals(m1, m3);
        assertEquals(m2, m3);
        assertEquals(m3, m2);
        assertEquals(m3, m1);
    }

    @Test public void dups() {
        Slaw[] sx = {string("foo"), int32(12),
                     string("bar"), int64(3),
                     string("baz"), string("whatever"),
                     string("bar"), int8(5)};
        Slaw m = map(sx);
        assertEquals(3, m.count());
        assertEquals(sx[1], m.find(sx[0]));
        assertEquals(sx[7], m.find(sx[2]));
        assertEquals(sx[5], m.find(sx[4]));

        Slaw[] conses = {cons(sx[2], sx[3]), cons(sx[0], sx[1]),
                         cons(sx[6], sx[7]), cons(sx[4], sx[5])};

        Slaw m2 = map(conses);
        assertEquals(m, m2);

        Slaw l = list(sx);
        assertEquals(sx[1], l.find(sx[0]));
        assertEquals(sx[7], l.find(sx[2]));
        assertEquals(sx[5], l.find(sx[4]));

        l = list(conses);
        assertEquals(sx[1], l.find(sx[0]));
        assertEquals(sx[7], l.find(sx[2]));
        assertEquals(sx[5], l.find(sx[4]));
    }

    private void testMap(Slaw m) {
        assertTrue(m.isMap());
        assertEquals(m, m);
        Map<Slaw,Slaw> map = m.emitMap();
        List<Slaw> conses = m.emitList();
        assertEquals(m, map(map));
        assertEquals(m.count(), map.size());
        assertEquals(m.count(), conses.size());
        for (int i = 0, c = m.count(); i < c; i++) {
            assertTrue(conses.get(i).isCons());
            assertEquals(conses.get(i), m.nth(i));
            assertEquals(conses.get(i).cdr(), map.get(conses.get(i).car()));
        }
        testListness(m);
        testAtomicEmissions(m);
    }
}
