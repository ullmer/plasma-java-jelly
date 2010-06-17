// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import com.oblong.jelly.*;
import static org.junit.Assert.*;

public class ExternalizerTestBase {
    protected ExternalizerTestBase(SlawExternalizer e) {
        externalizer = e;
        internalizer = null;
    }

    protected ExternalizerTestBase(SlawExternalizer e, SlawInternalizer i) {
        externalizer = e;
        internalizer = i;
    }

    protected SlawExternalizer externalizer;
    protected SlawInternalizer internalizer;
    protected SlawFactory factory =
        new com.oblong.jelly.slaw.JavaSlawFactory();

    protected final String arrayStr(byte[] bs) {
        StringBuilder buf = new StringBuilder ("{ ");
        for (byte b : bs)
            buf.append(Integer.toBinaryString(((int)b) & 0xff)).append(" ");
        buf.append("}");
        return buf.toString();
    }

    protected final void check(String msg, Slaw s, byte[] b) {
        checkExtern(msg, s, b);
        checkIntern(msg, s, b);
    }

    protected final byte[] slawToBytes(Slaw s) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            externalizer.extern(s, os);
        } catch (IOException e) {
            fail("Externalizing " + s + ": " + e.getMessage());
        }
        return os.toByteArray();
    }

    protected final void checkExtern(String msg, Slaw s, byte[] b) {
        if (externalizer != null) {
            final byte[] sb = slawToBytes(s);
            String m = msg + ": " + arrayStr(sb) + " vs. expected "
                + arrayStr(b);
            assertEquals(m, b.length, externalizer.externSize(s));
            assertArrayEquals(m + " for " + s, b, sb);
        }
    }

    protected final void checkIntern(String msg, Slaw s, byte[] b) {
        if (internalizer != null) {
            try {
                final InputStream is = new ByteArrayInputStream(b);
                final Slaw s2 = internalizer.internSlaw(is, factory);
                assertEquals(s, s2);
            } catch (Exception e) {
                fail(msg + ": " + e);
            }
        }
    }

    protected final void check(Slaw[] s, short[][] b) {
        for (int i = 0; i < s.length; i++) {
            check(i + "th iteration", s[i], asBytes(b[i]));
        }
    }

    protected final void checkSubslawx(String msg, ByteBuffer b, Slaw s) {
        for (Slaw c : s.emitList()) {
            byte[] bs = new byte[externalizer.externSize(c)];
            b.get(bs);
            checkExtern(msg, c, bs);
        }
    }

    protected final byte[] asBytes(short[] s) {
        byte[] bb = new byte[s.length];
        for (int j = 0; j < s.length; ++j) bb[j] = (byte)s[j];
        return bb;
    }
}
