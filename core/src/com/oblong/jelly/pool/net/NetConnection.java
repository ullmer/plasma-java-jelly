// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.net;

import java.util.concurrent.TimeUnit;
import java.util.Set;

import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.slaw.SlawFactory;
import com.oblong.jelly.pool.PoolProtein;

public interface NetConnection {

    PoolServerAddress address();
    int version();
    SlawFactory factory();

    void setTimeout(long t, TimeUnit u) throws PoolException;

    Slaw send(Request r, Slaw... args) throws PoolException;

    void close();
    boolean isOpen();

    Set<Request> supportedRequests();

    PoolProtein polled();
    PoolProtein resetPolled();
}
