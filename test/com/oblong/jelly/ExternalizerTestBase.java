// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.nio.ByteBuffer;
import org.junit.Assert;
import static org.junit.Assert.*;

class ExternalizerTestBase {
    ExternalizerTestBase(SlawExternalizer e) {
        externalizer = e;
        internalizer = null;
    }

    ExternalizerTestBase(SlawExternalizer e, SlawInternalizer i) {
        externalizer = e;
        internalizer = i;
    }

    SlawExternalizer externalizer;
    SlawInternalizer internalizer;
    SlawFactory factory = new JavaSlawFactory();

    String arrayStr(byte[] bs) {
        StringBuilder buf = new StringBuilder ("{ ");
        for (byte b : bs)
            buf.append(Integer.toBinaryString(((int)b) & 0xff)).append(" ");
        buf.append("}");
        return buf.toString();
    }

    void check(String msg, Slaw s, byte[] b) {
        final byte[] sb = externalizer.extern(s).array();
        String m = msg + ": " + arrayStr(sb) + " vs. expected " + arrayStr(b);
        assertEquals(sb.length, externalizer.externSize(s));
        assertArrayEquals(m + " for " + s, b, sb);
        checkIntern(msg, s, b);
    }

    void checkIntern(String msg, Slaw s, byte[] b) {
        if (internalizer != null) {
            try {
                final ByteBuffer b2 = ByteBuffer.wrap(b);
                final Slaw s2 = internalizer.internSlaw(b2, factory);
                assertEquals(s, s2);
                assertEquals(msg, 0, b2.remaining());
            } catch (SlawParseError e) {
                fail(msg + ": " + e);
            }
        }
    }

    void check(Slaw[] s, short[][] b) {
        for (int i = 0; i < s.length; i++) {
            check(i + "th iteration", s[i], asBytes(b[i]));
        }
    }

    void checkSubslawx(String msg, ByteBuffer b, Slaw s) {
        for (int i = 0, c = s.count(); i < c; i++) {
            byte[] sb = externalizer.extern(s.nth(i)).array();
            for (int j = 0; j < sb.length; j++)
                assertEquals(msg + "/" + j + "th slaw", sb[j], b.get());
        }
        assertFalse(b.hasRemaining());
    }

    byte[] asBytes(short[] s) {
        byte[] bb = new byte[s.length];
        for (int j = 0; j < s.length; ++j) bb[j] = (byte)s[j];
        return bb;
    }
}
