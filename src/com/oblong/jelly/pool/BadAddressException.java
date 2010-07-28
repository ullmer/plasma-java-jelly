// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool;

import com.oblong.jelly.PoolException;

public class BadAddressException extends PoolException {

    public BadAddressException(String i) { super(Kind.BAD_ADDRESS, i); }
    public BadAddressException(Throwable e) { super(Kind.BAD_ADDRESS, e); }

    public BadAddressException(long sc) {
        super(Kind.BAD_ADDRESS, sc, "Server rejected address");
    }

    private static final long serialVersionUID = -8010793100844536131L;
}