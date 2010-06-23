package com.oblong.jelly.pool;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.PoolException;
import com.oblong.jelly.Slaw;

@Immutable
public class PoolServerException extends PoolException {

    public PoolServerException(int serverCode, String info) {
        this(serverCode, null, info);
    }

    public PoolServerException(int serverCode, Throwable cause) {
        this(serverCode, null, cause);
    }

    public PoolServerException(int serverCode, Slaw res, String info) {
        super(serverCode, info);
        response = res;
    }

    public PoolServerException(int serverCode, Slaw res, Throwable cause) {
        super(serverCode, cause);
        response = res;
    }

    public Slaw response() { return response; }

    private final Slaw response;

    private static final long serialVersionUID = -7586082498540248002L;
}
