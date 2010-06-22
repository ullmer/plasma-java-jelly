// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool;

import com.oblong.jelly.PoolException;
import net.jcip.annotations.Immutable;

/**
 *
 * Created: Tue Jun 22 17:18:12 2010
 *
 * @author jao
 */
@Immutable
public final class PoolIOException extends PoolException {

    public PoolIOException(Exception e) {
        super(Code.IO_ERROR, e);
    }

    public PoolIOException(String msg) {
        super(Code.IO_ERROR, msg);
    }

    private static final long serialVersionUID = -7864964213407695961L;
}
