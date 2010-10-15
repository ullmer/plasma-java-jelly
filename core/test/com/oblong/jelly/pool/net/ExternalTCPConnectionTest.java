// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.net;

import com.oblong.jelly.PoolTestBase;
import com.oblong.jelly.PoolException;

/**
 * Unit tests for TCP pool server ops, using an external server.
 *
 * Created: Wed Jul 21 13:30:58 2010
 *
 * @author jao
 */
public class ExternalTCPConnectionTest extends PoolTestBase {

    public ExternalTCPConnectionTest() throws PoolException {
        super(externalServer());
    }

}
