// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.mem;

import org.junit.Test;
import static org.junit.Assert.*;

import com.oblong.jelly.PoolServersTestBase;

/**
 *  Unit Test for class MemServerFactory
 *
 *
 * Created: Thu Jul  1 17:36:33 2010
 *
 * @author jao
 */
public class MemServerFactoryTest extends PoolServersTestBase {

    public MemServerFactoryTest() {
        super("mem");
    }

    @Test public void reRegister() {
        assertFalse(MemServerFactory.register("mem"));
        assertFalse(MemServerFactory.register());
        assertTrue(MemServerFactory.register("foo"));
    }
}
