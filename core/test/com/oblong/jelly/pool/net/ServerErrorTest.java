// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool.net;

import static org.junit.Assert.*;
import org.junit.Test;

import com.oblong.jelly.pool.ServerErrorCode;

/**
 *  Unit Test for class ServerError
 *
 *
 * @author jao
 */
public class ServerErrorTest {

    @Test public void completeness() {
        for (ServerErrorCode c : ServerErrorCode.values()) {
            if (c.isPoolError()) {
                ServerError e = ServerError.getError(3, c.code());
                if (e == ServerError.UNREGISTERED)
                    e = ServerError.getError(2, c.code());
                final String m = c + " -> " + e;
                assertTrue(m, e != ServerError.UNREGISTERED);
            }
        }
    }

}
