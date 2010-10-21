// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import static org.junit.Assert.*;
import org.junit.Test;

import static com.oblong.jelly.Slaw.*;

/**
 * Unit Test for class SlawProtein
 *
 * Created: Mon May 31 15:12:32 2010
 *
 * @author jao
 */
public class SlawProteinTest {

    @Test public void empty() {
        final Protein p =  protein(null, null, null);
        assertNull(p.ingests());
        assertNull(p.descrips());
        assertEquals(0, p.dataLength());
        assertEquals(protein(null, null, null), p);
    }

    @Test public void match() {
        final Slaw e0 = int8(1);
        final Slaw e1 = string("foo");
        final Slaw e2 = map(e0, e1, e1, e0);
        final Protein p = protein(list(e0, e1, e2), map(), null);
        assertEquals(-1, p.indexOf(p.ingests()));
        assertEquals(-1, p.indexOf(p.descrips()));
        assertEquals(-1, p.indexOf(p));
        assertTrue(p.matches(p.descrips().emitArray()));
        final Slaw[][] ms = {{e0}, {e1}, {e2}, {e0, e1}, {e0, e2}, {e1, e2}};
        for (Slaw[] m : ms) assertTrue(p.matches(m));
        final Slaw[][] nms = {
            {e1, e0}, {e2, e0}, {e2, e1}, {e1, e0, e2}, {e0, e0},
            {e0, e1, e2, e0}
        };
        for (Slaw[] m : nms) assertFalse(p.matches(m));
    }
}
