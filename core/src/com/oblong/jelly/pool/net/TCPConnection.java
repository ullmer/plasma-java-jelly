
/* (c)  oblong industries */

package com.oblong.jelly.pool.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.oblong.jelly.*;
import com.oblong.jelly.slaw.SlawExternalizer;
import com.oblong.jelly.slaw.SlawFactory;
import com.oblong.jelly.slaw.SlawInternalizer;
import com.oblong.jelly.pool.Configuration;
import com.oblong.jelly.pool.PoolProtein;
import com.oblong.jelly.util.ByteReader;
import com.oblong.jelly.util.ExceptionHandler;

import static com.oblong.jelly.pool.net.Request.*;

final class TCPConnection implements NetConnection {

    @Override public PoolServerAddress address() { return address; }
    @Override public int version() { return version; }
    @Override public SlawFactory factory() { return factory; }
    @Override public Set<Request> supportedRequests() { return supported; }


    @Override public void setTimeout(long t, TimeUnit u)
        throws PoolException {
        try {
            if (isOpen() && t >= 0) socket.setSoTimeout((int)u.toMillis(t));
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
        return socket != null && !socket.isClosed();
    }

    @Override public PoolProtein polled() {

        throw new RuntimeException ("turd!");
    }

    @Override public PoolProtein resetPolled() {
        throw new RuntimeException ("turd!");

    }

    @Override public void setHose(Hose h) { hose = h; }

    static class Factory implements NetConnectionFactory {
        @Override public NetConnection get(PoolServer srv)
            throws PoolException {
            return new TCPConnection(srv);
        }

        @Override public String serviceName() {
            return "_pool-server._tcp";
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

    private TCPConnection(PoolServer srv) throws PoolException {
        try {
            address = srv.address();
            socket = new Socket(address.host(), address.port());
            input = new BufferedInputStream (socket.getInputStream());
            output = new BufferedOutputStream (socket.getOutputStream());
            sendPreamble(output);
            version = readVersions(input);
            supported = readSupported(input, version);
            externalizer = defaultExternalizer;
            internalizer = defaultInternalizer;
            hose = null;
        } catch (IOException e) {
            throw new InOutException(e);
        } catch (IllegalArgumentException e) {
            throw new InOutException(e);
        }

        if (Configuration.GREENHOUSE  &&
            ! supportedRequests () . contains (Request.GREENHOUSE))
            throw new PoolException (srv . address () +
                                     " is not a Greenhouse server");
    }

    private Slaw send(Protein p) throws PoolException {
        try {
            externalizer.extern(p, output);
            output.flush ();
        } catch (Exception e) {
            throw new InOutException(e);
        }
        return read(true);
    }

    private Slaw read(boolean cont) throws PoolException {
        Slaw ret = null;
        try {
            ret = internalizer.internProtein(input, factory);
        } catch (SocketTimeoutException e) {
            return null;
        } catch (Exception e) {
            throw new InOutException(e);
        }
        final Slaw code = getResultCode(ret);
        final Slaw args = getResultArgs(ret);
        if (CMD_RESULT.equals(code)) return args;
        if (FANCY_CMD_R1.equals(code)  ||
            FANCY_CMD_R2.equals(code)  ||
            FANCY_CMD_R3.equals(code)) {
            throw new PoolException ("turd!");
        }
        return cont ? read(cont) : null;
    }

    private static Slaw getResultCode(Slaw ret) throws InOutException {
        // Sorry for the bloated exception handling in this method, but we are investigating an elusive problem and
        // we need all the debug info we can get...

        if (!ret.isProtein() || ret.toProtein().ingests() == null) {
            String safeMsg = "Non-protein received from server; ret.ilk()= " + ret.ilk();
            ExceptionHandler.handleException(safeMsg + "; slaw will be printed in " +
                    "subsequent line, to avoid calling complex toString method on Slaw now");
            String msgWithDetails = safeMsg + "; (more details: ) ret.toString(): " + ret.toString();
            ExceptionHandler.handleException(msgWithDetails);

            throw new InOutException(msgWithDetails);
        }
        final Slaw code = ret.toProtein().ingests().find(OP_KEY);
        if (!VALID_RESULTS.contains(code)) {
            String safeMsg = "getResultCode: Unexpected response code. !VALID_RESULTS.contains(code). ret.ilk()=" +
                    ret.ilk() + "; code.ilk()=" + code.ilk() + ";";
            ExceptionHandler.handleException(safeMsg + "slaw will be printed in " +
                    "subsequent line, to avoid calling complex toString method on Slaw now");
            String msgWithDetails = safeMsg + " (more details: ) code=" + code + "; ret=" + ret;
            ExceptionHandler.handleException(msgWithDetails);
            throw new InOutException(msgWithDetails);
        }
        return code;
    }

    private static Slaw getResultArgs(Slaw ret) throws InOutException {
        final Slaw args = ret.toProtein().ingests().find(ARGS_KEY);
        if (args != null && !args.isList())
            throw new InOutException("Invalid response args: " + ret);
        return args;
    }

    private boolean positiveR2(Slaw args) {
        return (args.count() == 3
                && args.nth(0).isNumber(NumericIlk.INT64)
                && args.nth(0).emitLong() == 0
                && args.nth(2).isNumber(NumericIlk.INT64)
                && args.nth(1).isNumber(NumericIlk.FLOAT64));
    }

    private long r3Index(Slaw s) {
        long idx = s.emitLong();
        if (idx >= 0 || hose == null) return Math.max(0, idx);
        try {
            return hose.oldestIndex();
        } catch (PoolException e) {
            return 0;
        }
    }

    private void updateAsync(Slaw args) {
        throw new RuntimeException ("turd!");
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
    private Hose hose;

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

    static final Slaw CMD_RESULT = factory.number(NumericIlk.INT32, 14);
    static final Slaw FANCY_CMD_R1 = factory.number(NumericIlk.INT32, 64);
    static final Slaw FANCY_CMD_R2 = factory.number(NumericIlk.INT32, 65);
    static final Slaw FANCY_CMD_R3 = factory.number(NumericIlk.INT32, 66);
    static final Slaw VALID_RESULTS = factory.list(CMD_RESULT,
                                                   FANCY_CMD_R1,
                                                   FANCY_CMD_R2,
                                                   FANCY_CMD_R3);

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
