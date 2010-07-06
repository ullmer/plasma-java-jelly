// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assume.*;
import static org.junit.Assert.*;

/**
 *
 * Created: Tue Jul  6 14:46:05 2010
 *
 * @author jao
 */
public class HoseTestBase extends PoolServerTestBase {

    public HoseTestBase() {}

    protected HoseTestBase(PoolServerAddress addr) throws PoolException {
        super(addr);
    }

    @Before public void maybeDisable() {
        assumeTrue(server != null);
    }
    
    @Test public void hoseName() throws PoolException {
        final PoolAddress a = poolAddress("eipool");
        final Hose h = Pool.participate(a, PoolOptions.SMALL);
        assertEquals(a, h.poolAddress());
        assertEquals(a.toString(), h.name());
        final String newName = "this name is not the same";
        h.setName(newName);
        assertEquals(newName, h.name());
        h.withdraw();
        assertEquals(newName, h.name());
    }

    @Test public void deposit() throws PoolException {

    }
}
