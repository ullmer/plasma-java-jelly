// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.v2;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;

import com.oblong.jelly.*;
import com.oblong.jelly.slaw.*;
import static com.oblong.jelly.Slaw.*;

/**
 * Unit Test for class PlasmaV2 serialization: lists.
 *
 * Created: Tue May 25 12:16:18 2010
 *
 * @author jao
 */
public class PlasmaV2ListsTest extends ExternalizerTestBase {

    public PlasmaV2ListsTest() {
        super(new Externalizer(), new Internalizer());
    }

    @Test public void lists() {
        Slaw[] ls = {list(),
                     list(int32(1)),
                     list(nil(), string("foo")),
                     list(list(cons(string("a"), bool(false)), int32(2)),
                          string("second"),
                          int64(-1))};
        for (Slaw l : ls) checkList(l);

        List<Slaw> slx = new ArrayList<Slaw>();
        for (int i = 0; i < 16; i++) slx.add(unt64(i));
        checkList(list(slx));
        for (int i = 0; i < 10; i++) slx.add(bool(i % 2 == 0));
        checkList(list(slx));
    }

    private void checkList(Slaw l) {
        final String msg = "Checking " + l;
        final ByteBuffer b = externalizer.extern(l);
        checkHeading(msg, b, l.count());
        checkSubslawx(msg, b, l);
        checkIntern(msg, l, b.array());
    }

    private void checkHeading(String msg, ByteBuffer b, long count) {
        final long octs = Protocol.octs(b.remaining());
        final long h = b.getLong();
        assertEquals(msg, 4, h>>>60);
        assertEquals(msg, octs, h & (~(0xff<<56)));
        long elems = (h>>>56) & 0x0f;
        if (elems > 14) elems = b.getLong();
        assertEquals(msg, count, elems);
    }
}
