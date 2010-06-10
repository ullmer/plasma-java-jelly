// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool;

import com.oblong.jelly.Pool;
import com.oblong.jelly.PoolAddress;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolOptions;

/**
 *
 *
 * Created: Thu Jun 10 22:56:34 2010
 *
 * @author jao
 */
public interface PoolFactory {

    Pool create(PoolAddress a, PoolOptions p) throws PoolException;
    Pool lookup(PoolAddress a) throws PoolException;
    Pool lookup(PoolAddress a, PoolOptions p) throws PoolException;

}
