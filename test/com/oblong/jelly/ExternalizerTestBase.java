// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.nio.ByteBuffer;
import org.junit.Assert;
import static org.junit.Assert.*;

class ExternalizerTestBase {
    ExternalizerTestBase(SlawExternalizer e) { externalizer = e; }

    SlawExternalizer externalizer = new PlasmaExternalizerV2();

    String arrayStr(byte[] bs) {
        StringBuilder buf = new StringBuilder ("{ ");
        for (byte b : bs)
            buf.append(Integer.toBinaryString(((int)b) & 0xff)).append(" ");
        buf.append("}");
        return buf.toString();
    }

    void check(Slaw s, byte[] b, String msg) {
        final byte[] sb = externalizer.extern(s).array();
        String m = msg + ": " + arrayStr(sb) + " vs. expected " + arrayStr(b);
        assertEquals(sb.length, externalizer.externSize(s));
        assertArrayEquals(m + " for " + s, b, sb);
    }

    void check(Slaw[] s, short[][] b) {
        for (int i = 0; i < s.length; i++) {
            byte[] bb = new byte[b[i].length];
            for (int j = 0; j < b[i].length; ++j) bb[j] = (byte)b[i][j];
            check(s[i], bb, i + "th iteration");
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
}