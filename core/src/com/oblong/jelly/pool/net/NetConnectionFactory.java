package com.oblong.jelly.pool.net;

import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServerAddress;

public interface NetConnectionFactory {

    NetConnection get(PoolServer srv) throws PoolException;
    String serviceName();

}
