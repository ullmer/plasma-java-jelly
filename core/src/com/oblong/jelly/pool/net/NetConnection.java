
/* (c)  oblong industries */

package com.oblong.jelly.pool.net;

import java.util.concurrent.TimeUnit;
import java.util.Set;

import com.oblong.jelly.*;
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

    void setHose(Hose hose);
}
