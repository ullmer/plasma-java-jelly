// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import net.jcip.annotations.NotThreadSafe;

import com.oblong.jelly.Protein;

/**
 *
 * Created: Thu Jun 17 13:41:22 2010
 *
 * @author jao
 */
@NotThreadSafe
public final class ByteWriter {

    public ByteWriter(OutputStream os) throws IOException {
        if (os == null) throw new IOException("Null stream");
        stream = os;
        buffer = ByteBuffer.allocate(8);
        bytes = 0;
    }

    public long bytesWritten() { return bytes; }

    public ByteWriter putProteinData(Protein p) throws IOException {
        bytes += p.putData(stream);
        return this;
    }

    public ByteWriter put(byte[] bs, int offset, int len) throws IOException {
        stream.write(bs, offset, len);
        bytes += len;
        return this;
    }

    public ByteWriter put(byte[] bs) throws IOException {
        return put(bs, 0, bs.length);
    }

    public ByteWriter put(byte b) throws IOException {
        return reset(buffer.put(b));
    }

    public ByteWriter putShort(short s) throws IOException {
        return reset(buffer.putShort(s));
    }

    public ByteWriter putInt(int n) throws IOException {
        return reset(buffer.putInt(n));
    }

    public ByteWriter putLong(long n) throws IOException {
        return reset(buffer.putLong(n));
    }

    public ByteWriter putFloat(float n) throws IOException {
        return reset(buffer.putFloat(n));
    }

    public ByteWriter putDouble(double n) throws IOException {
        return reset(buffer.putDouble(n));
    }

    private final ByteWriter reset(ByteBuffer b) throws IOException {
        try {
            stream.write(b.array(), 0, b.position());
            bytes += b.position();
        } finally {
            b.clear();
        }
        return this;
    }

    private final OutputStream stream;
    private final ByteBuffer buffer;
    private long bytes;
}
