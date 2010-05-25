// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.HashMap;

import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;

import static com.oblong.jelly.Slaw.*;

/**
 * Unit Test for class PlasmaExternalizerV2: maps.
 *
 *
 * Created: Tue May 25 12:37:22 2010
 *
 * @author jao
 */
public class PlasmaV2MapsTest extends ExternalizerTestBase {

    public PlasmaV2MapsTest() { super(new PlasmaExternalizerV2()); }

    @Test public void maps() {
        Slaw[] ms = {map(),
                     map(string("k"), int32(1)),
                     map(nil(), string("foo"), float64(3.14), bool(true)),
                     map(cons(string("a"), bool(false)),
                         cons(int32(2), string("second")),
                         cons(string("b"), int64(-1)))};
        for (Slaw m : ms) checkMap(m);

        Map<Slaw,Slaw> slx = new HashMap<Slaw,Slaw>();
        for (int i = 0; i < 16; i++) slx.put(string("K" + i), unt64(i));
        checkMap(map(slx));
        for (int i = 0; i < 10; i++)
            slx.put(string("K2" + i), bool(i % 2 == 0));
        checkMap(map(slx));
    }

    private void checkMap(Slaw l) {
        final String msg = "Checking " + l;
        final ByteBuffer b = externalizer.extern(l);
        checkHeading(msg, b, l.count());
        checkSubslawx(msg, b, l);
    }

    private void checkHeading(String msg, ByteBuffer b, long count) {
        final long octs = PlasmaProtocolV2.octs(b.remaining());
        final long h = b.getLong();
        assertEquals(msg, 5, h>>>60);
        assertEquals(msg, octs, h & (~(0xff<<56)));
        long elems = (h>>>56) & 0x0f;
        if (elems > 14) elems = b.getLong();
        assertEquals(msg, count, elems);
    }
}
