package com.oblong.jelly.pool;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.PoolException;
import com.oblong.jelly.Slaw;

@Immutable
public class PoolProtocolException extends PoolException {

    public PoolProtocolException(String info) {
        this(null, info);
    }

    public PoolProtocolException(Throwable cause) {
        this(null, cause);
    }

    public PoolProtocolException(Slaw res, String info) {
        super(Code.PROTOCOL_ERROR, info);
        response = res;
    }

    public PoolProtocolException(Slaw res, Throwable cause) {
        super(Code.PROTOCOL_ERROR, cause);
        response = res;
    }

    public Slaw response() { return response; }

    private final Slaw response;

    private static final long serialVersionUID = -7586082498540248002L;
}
