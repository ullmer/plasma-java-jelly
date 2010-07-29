// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import net.jcip.annotations.Immutable;

/**
 *
 * Created: Tue Jun 22 17:18:12 2010
 *
 * @author jao
 */
@Immutable
public class InOutException extends PoolException {

    public InOutException(Exception e) {
        super(Kind.IO_ERROR, e);
    }

    public InOutException(String msg) {
        super(Kind.IO_ERROR, msg);
    }

    private static final long serialVersionUID = -7864964213407695961L;
}
