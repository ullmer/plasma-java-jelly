// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.tcp;

import com.oblong.jelly.PoolAddress;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;

interface ServerConnection {

    PoolAddress address();

    void close() throws PoolException;
    Slaw send(Protein cmd) throws PoolException;
    Slaw send(Request r, Protein... args) throws PoolException;

    int version();

}
