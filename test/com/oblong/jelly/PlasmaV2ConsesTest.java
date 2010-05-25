// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

import static com.oblong.jelly.Slaw.*;

/**
 *  Unit Test for class PlasmaExternalizerV2: conses.
 *
 * Created: Fri May 21 18:59:01 2010
 *
 * @author jao
 */
public class PlasmaV2ConsesTest extends ExternalizerTestBase {

    public PlasmaV2ConsesTest() { super(new PlasmaExternalizerV2()); }

    @Test public void conses() {
        Slaw[] cs = {cons(int8(1), string("foo")),
                     cons(vector(int16(1), unt32(234), unt32(5)),
                          list(nil(), bool(false))),
                     cons(nil(), nil())};
        for (Slaw c : cs) checkCons(c);
    }

    private void checkCons(Slaw c) {
        final String msg = "Checking " + c;
        final ByteBuffer b = externalizer.extern(c);
        checkHeading(msg, b);
        checkSubslawx(msg, b, c);
    }

    private void checkHeading(String msg, ByteBuffer b) {
        final long octs = PlasmaProtocolV2.octs(b.remaining());
        final long h = b.getLong();
        assertEquals(msg, 0x62, h>>>56);
        assertEquals(msg, octs, h & (~(0xff<<56)));
    }
}
