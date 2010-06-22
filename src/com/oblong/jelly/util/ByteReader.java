// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import net.jcip.annotations.NotThreadSafe;

/**
 *
 * Created: Tue Jun 15 21:40:38 2010
 *
 * @author jao
 */
@NotThreadSafe
public final class ByteReader {

    public static final int DEFAULT_BUFFER_SIZE = 8;

    public ByteReader(InputStream s, int bufferSize) {
        assert s != null && bufferSize > 0;
        size = Math.max(bufferSize, DEFAULT_BUFFER_SIZE);
        buffer = ByteBuffer.wrap(new byte[size]);
        stream = s;
        bufferedBytes = 0;
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
        final int buffered = Math.min(len, bufferedBytes);
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
        if (bufferedBytes > 0) shiftPosition(bufferedBytes);
        final int pad = ((read + b - 1) & -b) - read;
        if (pad > 0) skip(pad);
        return this;
    }

    private void adjustRead(int bs) {
        buffer.position(buffer.position() % size);
        bufferedBytes = Math.max(0, bufferedBytes - bs);
    }

    private void shiftPosition(int by) {
        buffer.position((buffer.position() + by) % size);
        bufferedBytes = Math.max(0, bufferedBytes - by);
    }

    private void readFromStream(byte[] bs, int offset, int len)
        throws IOException {
        while (len > 0) {
            int r = stream.read(bs, offset, len);
            if (r < 0) break;
            offset += r;
            len -= r;
            read += r;
        }
        if (len != 0) throw new IOException(
            "Stream underflow reading " + len + " (" + bufferedBytes + ")");
    }

    private void ensure(int len) throws IOException {
        final int p = buffer.position();
        if (size - p < len) {
            final byte[] a = buffer.array();
            for (int i = 0; i < bufferedBytes; ++i) a[i] = buffer.get(p + i);
            buffer.position(0);
        }
        final int delta = len - bufferedBytes;
        if (delta > 0) {
            readFromStream(
                buffer.array(), buffer.position() + bufferedBytes, delta);
            bufferedBytes += delta;
        }
    }

    final ByteBuffer buffer;
    final InputStream stream;
    final int size;
    int bufferedBytes;
    int read;
}
