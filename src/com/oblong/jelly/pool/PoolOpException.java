package com.oblong.jelly.pool;

import com.oblong.jelly.PoolException;
import net.jcip.annotations.Immutable;


@Immutable
public class PoolOpException extends PoolException {

    public PoolOpException(String info) { super(Code.UNSUPPORTED_OP, info); }

    private static final long serialVersionUID = -8852204604279246564L;
}
