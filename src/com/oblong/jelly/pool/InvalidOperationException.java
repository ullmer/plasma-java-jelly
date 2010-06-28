package com.oblong.jelly.pool;

import com.oblong.jelly.PoolException;
import net.jcip.annotations.Immutable;


@Immutable
public class InvalidOperationException extends PoolException {

    public InvalidOperationException(String info) { 
        super(Code.UNSUPPORTED_OP, info); 
    }

    public InvalidOperationException(long code) {
        super(Code.UNSUPPORTED_OP, code, "Server rejected op");
    }

    private static final long serialVersionUID = -8852204604279246564L;
}
