package com.oblong.jelly.pool.impl;

import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.PoolException;

public interface ServerConnectionFactory {

    ServerConnection get(PoolServerAddress address) throws PoolException;
}
