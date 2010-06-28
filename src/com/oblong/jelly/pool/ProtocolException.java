package com.oblong.jelly.pool;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.PoolException;
import com.oblong.jelly.Slaw;

@Immutable
public class ProtocolException extends PoolException {

    public ProtocolException(String info) {
        this(null, info);
    }

    public ProtocolException(Throwable cause) {
        this(null, cause);
    }

    public ProtocolException(Slaw res, String info) {
        super(Code.PROTOCOL_ERROR, info);
        response = res;
    }

    public ProtocolException(Slaw res, Throwable cause) {
        super(Code.PROTOCOL_ERROR, cause);
        response = res;
    }

    public ProtocolException(Slaw res, long sc) {
        super(Code.PROTOCOL_ERROR, sc, "Server rejected request");
        response = res;
    }

    public Slaw response() { return response; }
    
    private final Slaw response;
    
    private static final long serialVersionUID = -7586082498540248002L;
}
