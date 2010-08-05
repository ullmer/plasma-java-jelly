// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.mem;

import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.HoseTestBase;

/**
 *
 * Created: Tue Jul  6 15:14:14 2010
 *
 * @author jao
 */
public class MemHoseTest extends HoseTestBase {

    public MemHoseTest() throws PoolException {
        super(PoolServerAddress.fromURI("mem://localhost"));
    }
}
