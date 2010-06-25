// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.impl;

import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.slaw.SlawFactory;

public interface ServerConnection {

    PoolServerAddress address();
    int version();
    SlawFactory factory();

    Slaw send(Request r, Slaw... args) throws PoolException;
    void close();
    boolean isOpen();
}
