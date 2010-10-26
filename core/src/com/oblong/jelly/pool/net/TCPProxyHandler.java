// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Logger;

import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.pool.PoolProtein;
import com.oblong.jelly.pool.ServerErrorCode;
import com.oblong.jelly.slaw.SlawExternalizer;
import com.oblong.jelly.slaw.SlawFactory;
import com.oblong.jelly.slaw.SlawInternalizer;
import com.oblong.jelly.slaw.SlawParseError;
import com.oblong.jelly.slaw.io.BinaryExternalizer;
import com.oblong.jelly.slaw.io.BinaryInternalizer;
import com.oblong.jelly.slaw.java.JavaSlawFactory;
import com.oblong.jelly.util.ByteReader;
import com.oblong.util.Pair;

/**
 *
 * Created: Fri Jul  2 20:45:34 2010
 *
 * @author jao
 */
final class TCPProxyHandler implements Runnable {
    TCPProxyHandler(Socket sock, NetConnection pc) {
        socket = sock;
        connection = pc;
    }

    @Override public void run() {
        try {
            init();
        } catch (IOException e) {
            log.warning("IO error initialising pool handler: "
                        + e.getMessage());
            log.warning("Closing handler");
            connection.close();
        }
        while (connection.isOpen()) {
            try {
                reply(forward(next()));
            } catch (Exception e) {
                if (connection.isOpen()) {
                    log.warning("Connection error (closing handler): " + e);
                    connection.close();
                }
            }
        }
        try {
            if (socket.isConnected()) socket.close();
        } catch (Exception e) {
            log.warning("Exception closing socket (ignored): "
                        + e.getMessage());
        }
    }

    void close() { connection.close(); }

    private void init() throws IOException {
        ByteReader br = new ByteReader(socket.getInputStream());
        final int preLen = TCPPoolConnection.PREAMBLE.length;
        final int postLen = TCPPoolConnection.POSTAMBLE.length;
        byte[] buffer = new byte[preLen];
        br.get(buffer, preLen);
        if (!Arrays.equals(buffer, TCPPoolConnection.PREAMBLE))
            throw new IOException("Unexpected preamble");
        final int netv = br.get();
        final int slawv = br.get();
        if (netv > connection.version() || slawv != 2)
            throw new IOException("Unsupported versions "
                                  + netv + ", " + slawv);
        br.get(buffer, postLen);
        final OutputStream os = socket.getOutputStream();
        os.write((byte)connection.version());
        os.write(slawv);
        os.write(TCPPoolConnection.supportedToData(
                     connection.supportedRequests()));
        os.flush();
    }

    private Protein next() throws IOException {
        try {
            return internalizer.internProtein(socket.getInputStream(),
                                              factory);
        } catch (SlawParseError e) {
            if (!socket.isClosed()) {
                log.warning("Error parsing protein from server: "
                            + e.getMessage());
            }
            return null;
        }
    }

    private Pair<Request, Slaw> forward(Protein p) {
        final Request req = getRequest(p);
        if (req == null) return Pair.create((Request)null, NO_OP);
        final Slaw[] args = getArgs(p);
        try {
            return Pair.create(req, connection.send(req, args));
        } catch (PoolException e) {
            if (e.serverCode() == 0) return Pair.create(req, NO_CONN);
            return Pair.create(req, makeRet(e.serverCode()));
        }
    }

    private Request getRequest(Protein p) {
        final Slaw ings = p == null ? null : p.ingests();
        final Slaw sreq = ings == null ?
            null : ings.find(TCPPoolConnection.OP_KEY);
        return sreq == null || !sreq.isNumber() ?
            null : Request.fromCode((int)sreq.emitLong());
    }

    private static Slaw[] getArgs(Protein p) {
        if (p == null || p.ingests() == null) return new Slaw[0];
        final Slaw sargs = p.ingests().find(TCPPoolConnection.ARGS_KEY);
        final Slaw[] args = new Slaw[sargs == null ? 0 : sargs.count()];
        for (int i = 0; i < args.length; ++i) args[i] = sargs.nth(i);
        return args;
    }

    private void reply(Pair<Request, Slaw> ra) throws IOException {
        final boolean isFancy = ra.first() == Request.FANCY_ADD_AWAITER;
        final Slaw op = isFancy
            ? TCPPoolConnection.FANCY_CMD_R1 : TCPPoolConnection.CMD_RESULT;
        final Slaw ings = factory.map(TCPPoolConnection.OP_KEY,
                                      op,
                                      TCPPoolConnection.ARGS_KEY,
                                      ra.second());
        final Protein reply = factory.protein(null, ings, null);
        externalizer.extern(reply, socket.getOutputStream());
        if (isFancy) {
            final PoolProtein p = connection.polled();
            if (p != null) sendR3(p);
        }
    }

    private void sendR3(PoolProtein p) throws IOException {
        final Slaw args = factory.list(factory.number(NumericIlk.FLOAT64,
                                                      p.timestamp()),
                                       factory.number(NumericIlk.INT64,
                                                      p.index()),
                                       p.bareProtein());
        final Slaw ings = factory.map(TCPPoolConnection.OP_KEY,
                                      TCPPoolConnection.FANCY_CMD_R3,
                                      TCPPoolConnection.ARGS_KEY,
                                      args);
        final Protein reply = factory.protein(null, ings, null);
        externalizer.extern(reply, socket.getOutputStream());
    }

    private static Slaw makeRet(long code) {
        return factory.list(factory.number(NumericIlk.INT64, code));
    }

    private static Slaw makeRet(ServerErrorCode c) {
        return makeRet(c.code());
    }

    private static final SlawFactory factory = new JavaSlawFactory();
    private static final Slaw NO_OP =
        makeRet(ServerErrorCode.POOL_UNSUPPORTED_OPERATION);
    private static final Slaw NO_CONN =
        makeRet(ServerErrorCode.POOL_SERVER_UNREACH);

    private final Socket socket;
    private final NetConnection connection;
    private static final SlawInternalizer internalizer =
        new BinaryInternalizer();
    private static final SlawExternalizer externalizer =
        new BinaryExternalizer();

    private final Logger log = Logger.getLogger(getClass().getName());
}
