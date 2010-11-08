// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.mem;

import org.junit.BeforeClass;

import com.oblong.jelly.HoseTests;
import com.oblong.jelly.PoolServerAddress;

/**
 *
 * Created: Tue Jul  6 15:14:14 2010
 *
 * @author jao
 */
public class MemHoseTest extends HoseTests.Tests<MemPoolConnection> {

    @BeforeClass public static void setUp() throws Exception {
        initTests(PoolServerAddress.fromURI("mem://localhost"));
    }

}
