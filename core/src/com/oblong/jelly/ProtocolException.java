// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import net.jcip.annotations.Immutable;

/**
 * A PoolException signaling a misunderstanding between clean and server.
 *
 * <p> It has kind <code>PROTOCOL_ERROR</code>.
 *
 * <p> The reason for this error will usually be a message from the
 * server that the client cannot parse, or has unexpected fields.
 * ProtocolException can also originate by the server complaining
 * about a received message, although that should be considered a bug
 * in jelly's implementation of the pool protocols.
 *
 * <p> When the cause of the error is an unexpected response from the
 * server, you can access said response, as a Slaw, using {@link
 * #response()}.
 *
 * @author jao
 */
@Immutable
public class ProtocolException extends PoolException {

    public ProtocolException(String info) {
        this(null, info);
    }

    public ProtocolException(Throwable cause) {
        this(null, cause);
    }

    public ProtocolException(Slaw res, String info) {
        super(Kind.PROTOCOL_ERROR, info);
        response = res;
    }

    public ProtocolException(Slaw res, Throwable cause) {
        super(Kind.PROTOCOL_ERROR, cause);
        response = res;
    }

    public ProtocolException(Slaw res, long sc) {
        super(Kind.PROTOCOL_ERROR, sc, "Server rejected request");
        response = res;
    }

    /**
     * The Slaw (usually a protein) received from the server that they
     * client could not understand or wasn't expecting.
     */
    public Slaw response() { return response; }

    private final Slaw response;

    private static final long serialVersionUID = -7586082498540248002L;
}
