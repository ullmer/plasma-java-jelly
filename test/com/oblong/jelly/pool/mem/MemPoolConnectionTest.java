// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.mem;

import com.oblong.jelly.PoolServerTestBase;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolServerAddress;

/**
 *  Unit Test for class MemPoolConnection
 *
 *
 * Created: Tue Jul  6 03:07:37 2010
 *
 * @author jao
 */
public class MemPoolConnectionTest extends PoolServerTestBase {

    public MemPoolConnectionTest() throws PoolException {
        super(PoolServerAddress.fromURI("mem://localhost"));
    }

    static {
        MemServerFactory.register();
    }
}
