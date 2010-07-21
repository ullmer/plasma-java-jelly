// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.tcp;

import com.oblong.jelly.PoolTestBase;
import com.oblong.jelly.PoolException;

/**
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
