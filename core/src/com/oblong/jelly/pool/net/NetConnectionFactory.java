package com.oblong.jelly.pool.net;

import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.PoolException;

public interface NetConnectionFactory {

    NetConnection get(PoolServerAddress address) throws PoolException;
}
