// Copyright (c) 2010 Oblong Industries
// Created: Tue Jun 22 17:18:12 2010

package com.oblong.jelly;

import net.jcip.annotations.Immutable;

/**
 * A PoolException denoting I/O errors when communicating with a server.
 *
 * <p>When the remote server is being accessed through the network,
 * this problem will be often caused by glitches in the communication
 * link.
 *
 * <p> It has kind {@code IO_ERROR}.
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
