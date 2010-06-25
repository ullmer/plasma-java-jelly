package com.oblong.jelly.pool;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.PoolException;

@Immutable
public final class PoolTimeoutException extends PoolException {

    public PoolTimeoutException(double period) {
        super(Code.TIMEOUT, "Waiting for " + period + "seconds");
        this.period = period;
    }

    public double waitingPeriod() { return period; }

    private final double period;
    
    private static final long serialVersionUID = 3597941266546542983L;
}
