package com.oblong.jelly;

import net.jcip.annotations.Immutable;


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

    public Slaw response() { return response; }
    
    private final Slaw response;
    
    private static final long serialVersionUID = -7586082498540248002L;
}
