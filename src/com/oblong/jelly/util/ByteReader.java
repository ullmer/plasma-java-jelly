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

    public static final int DEFAULT_BUFFER_SIZE = 8;

    public ByteReader(InputStream s, int bufferSize) {
        assert s != null && bufferSize > 0;
        size = Math.max(bufferSize, DEFAULT_BUFFER_SIZE);
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

    public int bytesSeen() {
        return read;
    }

    public ByteReader skip(int n) throws IOException {
        ensure(n);
        shiftPosition(n);
        return this;
    }

    public byte peek(int p) throws IOException {
        ensure(1 + p);
        final byte r = buffer.get(buffer.position() + p);
        return r;
    }

    public long peekLong() throws IOException {
        ensure(8);
        final long result = buffer.getLong();
        buffer.position(buffer.position() - 8);
        return result;
    }

    public ByteReader get(byte[] bs, int len) throws IOException {
        final int buffered = Math.min(len, bytes);
        if (buffered > 0) {
            buffer.get(bs, buffer.position(), buffered);
            shiftPosition(buffered);
        }
        if (len > buffered) readFromStream(bs, buffered, len - buffered);
        return this;
    }

    public byte get() throws IOException {
        ensure(1);
        final byte r = buffer.get();
        adjustRead(1);
        return r;
    }

    public short getShort() throws IOException {
        ensure(2);
        final short r = buffer.getShort();
        adjustRead(2);
        return r;
    }

    public int getInt() throws IOException {
        ensure(4);
        final int r = buffer.getInt();
        adjustRead(4);
        return r;
    }

    public long getLong() throws IOException {
        ensure(8);
        final long r = buffer.getLong();
        adjustRead(8);
        return r;
    }

    public float getFloat() throws IOException {
        ensure(4);
        final float r = buffer.getFloat();
        adjustRead(4);
        return r;
    }

    public double getDouble() throws IOException {
        ensure(8);
        final double r = buffer.getDouble();
        adjustRead(8);
        return r;
    }

    public ByteReader skipToBoundary(int b) throws IOException {
        if (bytes > 0) shiftPosition(bytes);
        final int pad = ((read + b - 1) & -b) - read;
        if (pad > 0) skip(pad);
        return this;
    }

    public void dump(String msg) {
        System.out.println(msg + ":");
        System.out.println(" pos = " + buffer.position()
                           + ", bytes = " + bytes);
    }

    private void adjustRead(int bs) {
        buffer.position(buffer.position() % size);
        bytes = Math.max(0, bytes - bs);
    }

    private void shiftPosition(int by) {
        buffer.position((buffer.position() + by) % size);
        bytes = Math.max(0, bytes - by);
    }

    private void readFromStream(byte[] bs, int offset, int len)
        throws IOException {
        final int r = stream.read(bs, offset, len);
        if (len != r) throw new IOException(
            "Stream underflow reading " + len + " (" + bytes + ")");
        read += len;
    }

    private void ensure(int len) throws IOException {
        final int p = buffer.position();
        if (size - p < len) {
            final byte[] a = buffer.array();
            for (int i = 0; i < bytes; ++i) a[i] = buffer.get(p + i);
            buffer.position(0);
        }
        final int delta = len - bytes;
        if (delta > 0) {
            readFromStream(buffer.array(), buffer.position() + bytes, delta);
            bytes += delta;
        }
    }

    final ByteBuffer buffer;
    final InputStream stream;
    final int size;
    int bytes;
    int read;
}
