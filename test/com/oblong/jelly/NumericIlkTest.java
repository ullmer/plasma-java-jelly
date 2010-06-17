// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import static org.junit.Assert.*;
import org.junit.Test;

import static com.oblong.jelly.NumericIlk.*;

/**
 *
 * Created: Sat Apr 17 05:38:06 2010
 *
 * @author jao
 */
public class NumericIlkTest {

    @Test public void dominatIlk() {
        NumericIlk[] is = {UNT8, FLOAT32, UNT64};
        assertEquals(FLOAT32, dominantIlk(is));
        assertEquals(INT16, dominantIlk(INT8, INT8, UNT8, INT16));
        assertEquals(null, dominantIlk(FLOAT64, INT16, INT8, null, INT16));
    }
}
