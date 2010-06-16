// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.util;

import java.io.IOException;
import java.io.ByteArrayInputStream;
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
        assertEquals(r.position(), r.read());
        assertEquals(2, r.getLong());
        assertEquals(r.position(), r.read());
        assertEquals(3, r.get());
        assertEquals(r.position(), r.read());
        assertEquals(-34, r.getShort());
        assertEquals(r.position(), r.read());
        assertEquals(12345, r.getInt());
    }

    @Test public void peek() throws IOException {
        ByteBuffer bf = ByteBuffer.allocate(9);
        bf.put((byte)1);
        bf.putLong(0x0102030405060708L);
        ByteReader r = new ByteReader(new ByteArrayInputStream(bf.array()));

        assertEquals(1, r.peek(0));
        assertEquals(0, r.position());
        assertEquals(1, r.read());

        assertEquals(1, r.peek(1));
        assertEquals(0, r.position());
        assertEquals(2, r.read());

        assertEquals(7, r.peek(7));
        assertEquals(0, r.position());
        assertEquals(8, r.read());

        assertEquals(1, r.get());
        assertEquals(1, r.position());
        assertEquals(8, r.read());

        assertEquals(0x0102030405060708L, r.peekLong());
        assertEquals(1, r.position());
        assertEquals(9, r.read());

        assertEquals(0x01020304, r.getInt());
        assertEquals(5, r.position());
        assertEquals(9, r.read());

        assertEquals(0x05060708, r.getInt());
        assertEquals(9, r.position());
        assertEquals(9, r.read());
    }

}
