// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * Created: Tue Jun 15 21:40:38 2010
 *
 * @author jao
 */
public final class ByteReader {

    public static final int DEFAULT_BUFFER_SIZE = 512;

    public ByteReader(InputStream s, int bufferSize) {
        assert s != null && bufferSize > 0;
        size = Math.max(bufferSize, 8);
        buffer = ByteBuffer.wrap(new byte[size]);
        stream = s;
        bytes = 0;
        read = 0;
    }

    public ByteReader(InputStream s) {
        this(s, DEFAULT_BUFFER_SIZE);
    }

    public void setLittleEndian() {
        buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    public boolean isLittleEndian() {
        return buffer.order() == ByteOrder.LITTLE_ENDIAN;
    }

    public int position() {
        return buffer.position();
    }

    public int read() {
        return read;
    }

    public ByteReader skip(int n) throws IOException {
        ensure(n);
        buffer.position(buffer.position() + n);
        return this;
    }

    public byte peek(int p) throws IOException {
        ensure(1 + p);
        final byte result = buffer.get(buffer.position() + p);
        bytes += 1 + p;
        return result;
    }

    public long peekLong() throws IOException {
        final int pos = buffer.position();
        final long result = getLong();
        buffer.position(pos);
        bytes += 8;
        return result;
    }

    public byte get() throws IOException {
        ensure(1);
        return buffer.get();
    }

    public ByteReader get(byte[] bs, int len) throws IOException {
        final int blocks = len / size;
        for (int i = 0; i < blocks; ++i) {
            ensure(size);
            buffer.get(bs, i * size, size);
        }
        final int left = len % size;
        if (left > 0) {
            ensure(left);
            buffer.get(bs, blocks * size, left);
        }
        return this;
    }

    public short getShort() throws IOException {
        ensure(2);
        return buffer.getShort();
    }

    public int getInt() throws IOException {
        ensure(4);
        return buffer.getInt();
    }

    public long getLong() throws IOException {
        ensure(8);
        return buffer.getLong();
    }

    public float getFloat() throws IOException {
        ensure(4);
        return buffer.getFloat();
    }

    public double getDouble() throws IOException {
        ensure(8);
        return buffer.getDouble();
    }

    public ByteReader skipToBoundary(int b) throws IOException {
        final int last = buffer.position() + bytes;
        buffer.position(last);
        bytes = 0;
        final int pad = ((last + b - 1) & -b) - last;
        if (pad > 0) {
            ensure(pad);
            buffer.position(last + pad);
        }
        return this;
    }

    private void ensure(int len) throws IOException {
        assert len <= size;
        final int delta = len - bytes;
        if (buffer.remaining() < len) {
            final int p = buffer.position();
            buffer.position(0);
            buffer.put(buffer.array(), buffer.position(), bytes);
            buffer.position(0);
        }
        if (delta > 0) {
            final int r = stream.read(buffer.array(),
                                      buffer.position() + bytes,
                                      delta);
            if (delta != r)
                throw new IOException(
                    "Stream underflow reading " + delta + " (" + bytes + ")");
            bytes = 0;
            read += delta;
        } else {
            bytes -= len;
        }
    }

    final ByteBuffer buffer;
    final InputStream stream;
    final int size;
    int bytes;
    int read;
}
