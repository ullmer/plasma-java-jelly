// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;

import com.oblong.jelly.NumericSlaw.Ilk;
import static com.oblong.jelly.NumericSlaw.Ilk.*;

/**
 *
 * Created: Sat Apr 17 05:38:06 2010
 *
 * @author jao
 */
public class NumericSlawTest {

    @Test public void dominatIlk() {
        Ilk[] is = {UNT8, FLOAT32, UNT64};
        assertEquals(FLOAT32, Ilk.dominantIlk(is));
        assertEquals(INT16, Ilk.dominantIlk(INT8, INT8, UNT8, INT16));
    }

}
