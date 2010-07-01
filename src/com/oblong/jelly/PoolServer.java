// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.Set;

/**
 *
 * Created: Mon Jun 14 13:22:13 2010
 *
 * @author jao
 */
public interface PoolServer {

    PoolServerAddress address();

    void create(String name, PoolOptions opts) throws PoolException;

    void dispose(String name) throws PoolException;

    Hose participate(String name) throws PoolException;

    Hose participate(String name, PoolOptions opts) throws PoolException;

    Set<String> pools() throws PoolException;
}
