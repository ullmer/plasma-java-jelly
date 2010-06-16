// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.util;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *  Unit Test for class ByteReader
 *
 *
 * Created: Wed Jun 16 00:50:38 2010
 *
 * @author jao
 */
public class ByteReaderTest {

    @Test public void getters() throws IOException {
        ByteBuffer bf = ByteBuffer.allocate(60);
        bf.putLong(1);
        bf.putLong(2);
        bf.put((byte)3);
        bf.putShort((short)-34);
        bf.putInt(12345);

        ByteReader r = new ByteReader(new ByteArrayInputStream(bf.array()));
        assertEquals(1, r.getLong());
        assertEquals(8, r.bytesSeen());

        assertEquals(2, r.getLong());
        assertEquals(16, r.bytesSeen());

        assertEquals(3, r.get());
        assertEquals(17, r.bytesSeen());

        assertEquals(-34, r.getShort());
        assertEquals(19, r.bytesSeen());

        assertEquals(12345, r.getInt());
        assertEquals(23, r.bytesSeen());
    }

    @Test public void peek() throws IOException {
        ByteBuffer bf = ByteBuffer.allocate(9);
        bf.put((byte)1);
        bf.putLong(0x0102030405060708L);
        ByteReader r = new ByteReader(new ByteArrayInputStream(bf.array()));

        assertEquals(1, r.peek(0));
        assertEquals(1, r.bytesSeen());

        assertEquals(1, r.peek(1));
        assertEquals(2, r.bytesSeen());

        assertEquals(7, r.peek(7));
        assertEquals(8, r.bytesSeen());

        assertEquals(1, r.get());
        assertEquals(8, r.bytesSeen());

        final long l = r.peekLong();
        assertEquals(Long.toHexString(l), 0x0102030405060708L, l);
        assertEquals(9, r.bytesSeen());

        final int i = r.getInt();
        assertEquals(Integer.toHexString(i), 0x01020304, i);
        final int j = r.getInt();
        assertEquals(Integer.toHexString(j), 0x05060708, j);
    }

    @Test public void bulk() throws IOException {
        final byte bs[] = {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17
        };
        ByteReader r = new ByteReader(new ByteArrayInputStream(bs));
        final byte[] bs0 = new byte[7];
        r.get(bs0, 7);
        r.peek(2);
        for (int i = 0; i < 7; ++i) assertEquals(bs[i], bs0[i]);
        final byte[] bs1 = new byte[9];
        r.get(bs1, 9);
        for (int i = 0; i < 9; ++i) assertEquals(bs[i + 7], bs1[i]);
    }
}
