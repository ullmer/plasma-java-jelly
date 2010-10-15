// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.net;

import com.oblong.jelly.AsyncHoseTestBase;
import com.oblong.jelly.PoolException;

/**
 * Tests for waiting Hose operations against an external TCP pool
 * server.
 *
 * Created: Wed Jul 21 16:25:11 2010
 *
 * @author jao
 */
public class ExternalAsyncTCPHoseTest extends AsyncHoseTestBase {

    public ExternalAsyncTCPHoseTest() throws PoolException {
        super(externalServer());
    }
}
