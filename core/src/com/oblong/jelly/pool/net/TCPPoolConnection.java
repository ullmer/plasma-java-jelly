// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.oblong.jelly.InOutException;
import com.oblong.jelly.InvalidOperationException;
import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.slaw.SlawExternalizer;
import com.oblong.jelly.slaw.SlawFactory;
import com.oblong.jelly.slaw.SlawInternalizer;
import com.oblong.jelly.util.ByteReader;

import static com.oblong.jelly.pool.net.Request.*;

final class TCPPoolConnection implements NetConnection {

    @Override public PoolServerAddress address() { return address; }
    @Override public int version() { return version; }
    @Override public SlawFactory factory() { return factory; }
    @Override public Set<Request> supportedRequests() { return supported; }


    @Override public void setTimeout(long t, TimeUnit u)
        throws PoolException {
        try {
            if (t >= 0) socket.setSoTimeout((int)u.toMillis(t));
        } catch (Exception e) {
            throw new InOutException(e);
        }
    }

    @Override public Slaw send(Request r, Slaw... args)
        throws PoolException {
        final Slaw code = factory.number(NumericIlk.INT32, r.code());
        final Slaw ings = factory.map(OP_KEY, code,
                                      ARGS_KEY, factory.list(args));
        try {
            return send(factory.protein(null, ings , null));
        } catch (InOutException e) {
            close();
            throw e;
        }
    }

    @Override public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            final Logger log = Logger.getLogger(getClass().getName());
            log.warning("Error closing socket (ignored): " + e.getMessage());
        }
    }

    @Override public boolean isOpen() {
        return !socket.isClosed();
    }

    static class Factory implements NetConnectionFactory {
        @Override public NetConnection get(PoolServerAddress addr)
            throws PoolException {
            return new TCPPoolConnection(addr);
        }
    }

    static byte[] supportedToData(Set<Request> supp) {
        final byte[] result = new byte[5];
        result[0] = 4;
        for (Request r : supp) {
            final int c = r.code();
            final int bn = 1 + c / 8;
            result[bn] |= 1<<(c % 8);
        }
        return result;
    }

    static Set<Request> readSupported(InputStream is, int v)
        throws InOutException, IOException {
        if (v == 0) return defaultSupported;
        final int len = is.read();
        if (len <= 0) throw new InOutException("Server supports no ops.");
        final byte[] data = new byte[len];
        final ByteReader reader = new ByteReader(is);
        reader.get(data, len);
        final Set<Request> result = EnumSet.noneOf(Request.class);
        for (Request c : Request.values()) {
            final int code = c.code();
            final int bn = code / 8;
            if (bn < data.length && (data[bn] & (1<<(code % 8))) != 0)
                result.add(c);
        }
        return result;
    }

    private TCPPoolConnection(PoolServerAddress addr) throws PoolException {
        try {
            address = addr;
            socket = new Socket(addr.host(), addr.port());
            input = socket.getInputStream();
            output = socket.getOutputStream();
            sendPreamble(output);
            version = readVersions(input);
            supported = readSupported(input, version);
            externalizer = defaultExternalizer;
            internalizer = defaultInternalizer;
        } catch (IOException e) {
            throw new InOutException(e);
        } catch (IllegalArgumentException e) {
            throw new InOutException(e);
        }
    }

    private Slaw send(Protein p) throws PoolException {
        try {
            while (input.available() > 0) input.read();
            externalizer.extern(p, output);
        } catch (Exception e) {
            throw new InOutException(e);
        }
        return read();
    }

    private Slaw read() throws PoolException {
        Slaw ret = null;
        try {
            ret = internalizer.internProtein(input, factory);
        } catch (SocketTimeoutException e) {
            return null;
        } catch (Exception e) {
            throw new InOutException(e);
        }
        if (!ret.isProtein())
            throw new InOutException("Non-protein received from server");
        ret = ret.toProtein().ingests();
        if (ret != null) ret = ret.find(ARGS_KEY);
        if (ret == null)
            throw new InOutException("Empty response from server");
        return ret;
    }

    private static void sendPreamble(OutputStream os) throws IOException {
        os.write(PREAMBLE);
        os.write(MAX_TCP_VERSION);
        os.write(MAX_SLAW_VERSION);
        os.write(POSTAMBLE);
        os.flush();
    }

    private static int readVersions(InputStream is)
        throws PoolException, IOException {
        final int tcpVersion = is.read();
        final int slawVersion = is.read();
        checkVersion(MIN_SLAW_VERSION, MAX_SLAW_VERSION, slawVersion, "slaw");
        checkVersion(MIN_TCP_VERSION, MAX_TCP_VERSION, tcpVersion, "tcp");
        return tcpVersion;
    }

    private static void checkVersion(int min, int max, int v, String msg)
        throws PoolException {
        if (v < min || v > max) {
            throw new InvalidOperationException("Unsupported " + msg
                                      + " server protocol (" + v + ")");
        }
    }

    private final int version;
    private final PoolServerAddress address;
    private final Socket socket;
    private final OutputStream output;
    private final InputStream input;
    private final Set<Request> supported;
    private final SlawExternalizer externalizer;
    private final SlawInternalizer internalizer;

    private static final SlawFactory factory =
        new com.oblong.jelly.slaw.java.JavaSlawFactory();
    private static final SlawExternalizer defaultExternalizer =
        new com.oblong.jelly.slaw.io.BinaryExternalizer();
    private static final SlawInternalizer defaultInternalizer =
        new com.oblong.jelly.slaw.io.BinaryInternalizer();

    static final Set<Request> defaultSupported =
        EnumSet.of(CREATE, DISPOSE, PARTICIPATE, PARTICIPATE_C,
                   WITHDRAW, DEPOSIT, NTH_PROTEIN, NEXT, PROBE_FWD,
                   NEWEST_INDEX, NEXT, OLDEST_INDEX,
                   AWAIT_NEXT, ADD_AWAITER, INFO, LIST);

    private static final int MIN_SLAW_VERSION = 2;
    private static final int MAX_SLAW_VERSION = 2;
    private static final int MIN_TCP_VERSION = 1;
    private static final int MAX_TCP_VERSION = 3;

    static final Slaw OP_KEY = factory.string("op");
    static final Slaw ARGS_KEY = factory.string("args");

    static final byte[] PREAMBLE = {
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x50,
        (byte)0x93, (byte)0x93, 0x00, (byte)0x80, 0x18, 0x00, 0x00, 0x02,
        0x00, 0x00, 0x00, 0x10, 0x40, 0x00, 0x00, 0x04,
        0x20, 0x00, 0x00, 0x01, 0x6f, 0x70, 0x00, 0x00,
        0x08, 0x00, 0x00, 0x03, 0x00, 0x00, 0x00, 0x01,
        0x40, 0x00, 0x00, 0x08, 0x20, 0x00, 0x00, 0x02,
        0x61, 0x72, 0x67, 0x73, 0x00, 0x00, 0x00, 0x00,
        0x10, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x05,
        0x20, 0x00, 0x00, 0x02, 0x5e, 0x2f, 0x5e, 0x2f,
        0x5e, 0x2f, 0x5e, 0x00
    };

    static final byte[] POSTAMBLE = {
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };

}