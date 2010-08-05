// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.mem;

import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.PoolTestBase;

/**
 *  Unit Test for class MemPoolConnection, via tests on a PoolServer
 *  connecting to a mem pool.
 *
 *
 * Created: Tue Jul  6 03:07:37 2010
 *
 * @author jao
 */
public class MemPoolConnectionTest extends PoolTestBase {

    public MemPoolConnectionTest() throws PoolException {
        super(PoolServerAddress.fromURI("mem://localhost"));
    }
}
