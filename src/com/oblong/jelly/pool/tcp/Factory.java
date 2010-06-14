// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.tcp;

import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolServer;

/**
 *
 * Created: Mon Jun 14 14:42:48 2010
 *
 * @author jao
 */
public class Factory implements PoolServer.Factory {

    @Override public PoolServer get(String scheme, String host, short port)
        throws PoolException {
        return null;
    }

}
