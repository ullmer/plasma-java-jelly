// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.v2;

import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;

import com.oblong.jelly.*;
import static com.oblong.jelly.Slaw.*;

/**
 *  Unit Test for PlasmaV2 serialization: arrays.
 *
 *  Created: Thu May 20 23:47:26 2010
 *
 *  @author jao
 */
public class PlasmaV2ArraysTest extends ExternalizerTestBase {

    public PlasmaV2ArraysTest() {
        super(new Externalizer(), new Internalizer());
    }

    @Test public void empty() {
        for (SlawIlk i : SlawIlk.arrayIlks()) {
            for (NumericIlk ni : NumericIlk.values()) {
                final int minDim =
                    i == SlawIlk.ARRAY || i == SlawIlk.COMPLEX_ARRAY ? 1 : 2;
                final int maxDim = minDim == 1 ?
                    1 : i == SlawIlk.MULTI_VECTOR ? 5 : 4;
                for (int d = minDim; d <= maxDim; d++) {
                    String msg = i + "/" + ni + "/" + d;
                    final Slaw a = array(i, ni, d);
                    assertEquals(msg, i, a.ilk());
                    assertEquals(msg, ni, a.numericIlk());
                    assertEquals(msg, d, a.dimension());
                    assertEquals(msg, 0, a.count());
                    assertEquals(msg, 8, externalizer.externSize(a));
                    byte[] bs = externalizer.extern(a).array();
                    msg = msg + "/" + arrayStr(bs);
                    assertEquals(msg, 8, bs.length);
                    checkHeading(msg, bs, i, ni, d, 0);
                    checkIntern(msg, a, bs);
                }
            }
        }
    }

    @Test public void numbers() {
        for (NumericIlk ni : NumericIlk.values()) {
            final Slaw a = array(number(ni, 1L));
            final byte[] bs = externalizer.extern(a).array();
            final String msg = "Array of " + ni + "/" + arrayStr(bs);
            assertEquals(16, bs.length);
            checkHeading(msg, bs, SlawIlk.ARRAY, ni, 1, 1);
            if (ni.isIntegral()) {
                for (int i = 8, b = 7 + ni.bytes(); i < 16; i++)
                    assertEquals(msg, i == b ? 1 : 0, bs[i]);
            }
            checkIntern(msg, a, bs);
        }
    }

    @Test public void numbers2() {
        final Slaw[] ls = new Slaw[100];
        for (int i = 0; i < 100; i++) ls[i] = unt8(i);
        final Slaw a = array(ls);
        final byte[] ba = externalizer.extern(a).array();
        checkHeading("Big array", ba, SlawIlk.ARRAY, NumericIlk.UNT8, 1, 100);
        for (int i = 0; i < 100; i++)
            assertEquals(i + "th", i, ba[i + 8]);
        checkIntern("", a, ba);
    }

    @Test public void floats() {
        final short[][] bs={{0xe8, 0x00, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x03,
                             0x3f, 0xc0, 0x00, 0x00, 0x3f, 0xcc, 0xcc, 0xcd,
                             0x3f, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
                            {0xec, 0x01, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x02,
                             0x40, 0x08, 0xcc, 0xcc, 0xcc, 0xcc, 0xcc, 0xcd,
                             0x40, 0x01, 0x99, 0x99, 0x99, 0x99, 0x99, 0x9a}};
        final Slaw[] as = {array(float32(1.5f), float32(1.6f), float32(1.5f)),
                           array(float64(3.1), float64(2.2))};
        check(as, bs);
    }

    @Test public void complexes() {
        for (NumericIlk ni : NumericIlk.values()) {
            final Slaw ca = array(complex(number(ni, 1L), number(ni, 2L)),
                                  complex(number(ni, 3L), number(ni, 4L)));
            final byte[] bs = externalizer.extern(ca).array();
            final String msg = "Array of " + ni + "/" + arrayStr(bs);
            assertEquals(Protocol.roundUp(8 + 4 * ni.bytes()), bs.length);
            checkHeading(msg, bs, SlawIlk.COMPLEX_ARRAY, ni, 1, 2);
            if (ni.isIntegral()) {
                for (int j = 0; j < 4; j++) {
                    for (int i = 0; i < ni.bytes() - 1; i++)
                        assertEquals(msg, 0, bs[8 + j * ni.bytes() + i]);
                    assertEquals(msg, j + 1, bs[7 + (j + 1) * ni.bytes()]);
                }
            }
            checkIntern(msg, ca, bs);
        }
    }

    @Test public void vectors() {
        for (NumericIlk ni : NumericIlk.values()) {
            final Slaw v22 = array(vector(number(ni, 1L), number(ni, 2L)),
                                   vector(number(ni, 3L), number(ni, 4L)));
            final byte[] b22 = externalizer.extern(v22).array();
            final String msg = "Array of " + ni + "/" + arrayStr(b22);
            assertEquals(Protocol.roundUp(8 + 4 * ni.bytes()), b22.length);
            checkHeading(msg, b22, SlawIlk.VECTOR_ARRAY, ni, 2, 2);
            if (ni.isIntegral()) {
                for (int j = 0; j < 4; j++) {
                    for (int i = 0; i < ni.bytes() - 1; i++)
                        assertEquals(msg, 0, b22[8 + j * ni.bytes() + i]);
                    assertEquals(msg, j + 1, b22[7 + (j + 1) * ni.bytes()]);
                }
            }
            checkIntern(msg, v22, b22);
        }
    }

    private static void checkHeading(String msg,
                                     byte[] bs,
                                     SlawIlk ilk,
                                     NumericIlk ni,
                                     int dim,
                                     int breadth) {
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
        for (int j = 3; j < 8; j++) cnt = (cnt<<8) + ((int)bs[j]) & 0xff;
        assertEquals(msg, breadth, cnt);
    }
}
