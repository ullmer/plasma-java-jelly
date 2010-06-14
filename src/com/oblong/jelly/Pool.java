// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.List;

/**
 *
 * Created: Thu Jun  3 10:08:24 2010
 *
 * @author jao
 */
public interface Pool {

    String name();
    PoolOptions options();
    PoolServer server();

    Hose participate() throws PoolException;
    void dispose() throws PoolException;

}
