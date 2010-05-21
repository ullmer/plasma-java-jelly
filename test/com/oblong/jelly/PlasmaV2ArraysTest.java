// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;

import static com.oblong.jelly.Slaw.*;

/**
 *  Unit Test for class PlasmaExternalizerV2: arrays.
 *
 *  Created: Thu May 20 23:47:26 2010
 *
 *  @author jao
 */
public class PlasmaV2ArraysTest extends ExternalizerTestBase {

    public PlasmaV2ArraysTest() { super(new PlasmaExternalizerV2()); }

    @Test public void empty() {
        for (SlawIlk i : SlawIlk.arrayIlks()) {
            for (NumericIlk ni : NumericIlk.values()) {
                final int minDim =
                    i == SlawIlk.ARRAY || i == SlawIlk.COMPLEX_ARRAY ? 1 : 2;
                final int maxDim = minDim == 1 ?
                    1 : i == SlawIlk.MULTI_VECTOR ? 5 : 4;
                for (int d = minDim; d <= maxDim; d++) {
                    String msg = i + "/" + ni + "/" + d;
                    Slaw a = array(i, ni, d);
                    assertEquals(msg, i, a.ilk());
                    assertEquals(msg, ni, a.numericIlk());
                    assertEquals(msg, d, a.dimension());
                    assertEquals(msg, 0, a.count());
                    assertEquals(msg, 8, externalizer.externSize(a));
                    byte[] bs = externalizer.extern(a).array();
                    msg = msg + "/" + arrayStr(bs);
                    assertEquals(msg, 8, bs.length);
                    checkHeading(bs, i, ni, d, 0, msg);
                }
            }
        }
    }

    private static void checkHeading(byte[] bs,
                                     SlawIlk ilk,
                                     NumericIlk ni,
                                     int dim,
                                     int breadth,
                                     String msg) {
        final int b0 = ((int)bs[0]) & 0xff;
        assertEquals(msg, 3, b0>>>6);
        assertEquals(msg, ni.isIntegral() ? 0 : 1, (b0>>>5) & 1);
        assertEquals(msg, ni.isSigned() ? 0 : 1, (b0>>>4) & 1);
        final int[] widths = {8, 16, 32, 64};
        final int wbits = (b0>>>2) & 3;
        assertTrue(msg, wbits <= 3);
        assertEquals(msg, ni.width(), widths[wbits]);
        assertEquals(msg, ilk.isComplexNumeric() ? 1 : 0, (b0>>>1) & 1);
        assertEquals(msg, ilk == SlawIlk.MULTI_VECTOR_ARRAY ? 1 : 0, b0 & 1);

        final int b1 = ((int)bs[1]) & 0xff;
        assertEquals(msg,
                     ilk == SlawIlk.MULTI_VECTOR_ARRAY ? dim - 2 : dim - 1,
                     (b1>>>6) & 3);

        final int cs = ilk == SlawIlk.MULTI_VECTOR_ARRAY ? 1<<dim : dim;
        int ebsize = ni.bytes() * cs;
        if (ilk.isComplexNumeric()) ebsize *= 2;
        final int b2 = ((int)bs[2]) & 0xff;
        final int bsize = ((b1<<2) & 0xff) | (b2>>>6);
        assertEquals(msg, ebsize - 1, bsize);

        int cnt = (int)(bs[2]) & 0x3f;
        for (int j = 3; j < 8; j++) cnt = cnt<<8 + ((int)bs[j]) & 0xff;
        assertEquals(msg, breadth, cnt);
    }
}
