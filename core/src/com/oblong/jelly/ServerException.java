// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import net.jcip.annotations.Immutable;

/**
 * A PoolException denoting a generic server-originated error.
 *
 * <p> It has kind {@code SERVER_ERROR}.
 *
 * <p> Every now and then, a pool server will report a problem that
 * doesn't fit in our error classification. Such errors are reported
 * using this exception type.
 *
 * @author jao
 */
@Immutable
public class ServerException extends PoolException {

    public ServerException(long serverCode, String info) {
        this(serverCode, null, info);
    }

    public ServerException(long serverCode, Throwable cause) {
        this(serverCode, null, cause);
    }

    public ServerException(long serverCode, Slaw res, String info) {
        super(serverCode, info);
        response = res;
    }

    public ServerException(long serverCode, Slaw res, Throwable cause) {
        super(serverCode, cause);
        response = res;
    }

    public Slaw response() { return response; }

    private final Slaw response;

    private static final long serialVersionUID = -7586082498540248002L;
}
