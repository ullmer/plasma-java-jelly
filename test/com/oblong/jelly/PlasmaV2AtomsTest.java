// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;

import static com.oblong.jelly.Slaw.*;

/**
 *  Unit Test for class PlasmaExternalizerV2: atomic slawx.
 *
 * Created: Tue Apr 20 13:07:13 2010
 *
 * @author jao
 */
public class PlasmaV2AtomsTest extends ExternalizerTestBase {

    public PlasmaV2AtomsTest() {
        super(new PlasmaExternalizerV2(), new PlasmaInternalizerV2());
    }

    @Test public void nils() {
        byte[] bs = {0x20, 0, 0, 0, 0, 0, 0, 0x02};
        check(nil(), bs, "nil slaw");
    }

    @Test public void booleans() {
        short[][] bs = {{0x20, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01},
                        {0x20, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}};
        Slaw[] sx = {bool(true), bool(false)};
        check(sx, bs);
    }

    @Test public void weeStrings() {
        short[][] bs = {{0x31, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
                        {0x35,  0x00, 0x00, 0x73, 0x74, 0x72, 0x30, 0x00},
                        {0x37, 0xE2, 0x86, 0x91, 0xE2, 0x86, 0x93, 0x00}};
        Slaw[] sx = {string(""), string("str0"), string("↑↓")};
        check(sx, bs);
    }

    @Test public void strings() {
        byte[] bs = {0x77, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05,
                     0x74, 0x68, 0x69, 0x73, 0x20, 0x69, 0x73, 0x20,
                     0x6e, 0x6f, 0x74, 0x20, 0x61, 0x20, 0x77, 0x65,
                     0x65, 0x20, 0x73, 0x74, 0x72, 0x69, 0x6e, 0x67,
                     0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        check(string("this is not a wee string"), bs, "");
    }

    @Test public void int8s() {
        short[][] bs = {{0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
                        {0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x96},
                        {0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x7f}};
        Slaw[] sx = {int8(0), int8(0x96), int8(0x7f)};
        check(sx, bs);
    }

    @Test public void unt8s() {
        short[][] bs = {{0x90, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
                        {0x90, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x6a},
                        {0x90, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x7f}};
        Slaw[] sx = {unt8(0), unt8(0x6a), unt8(0x7f)};
        check(sx, bs);
    }

    @Test public void int16s() {
        short[][] bs = {{0x84, 0x00, 0x40, 0x00, 0x00, 0x00, 0x00, 0x00},
                        {0x84, 0x00, 0x40, 0x00, 0x00, 0x00, 0x80, 0x00},
                        {0x84, 0x00, 0x40, 0x00, 0x00, 0x00, 0x7f, 0xff}};

        Slaw[] sx = {int16(0), int16(-32768), int16(32767)};
        check(sx, bs);
    }

    @Test public void unt16s() {
        short[][] bs = {{0x94, 0x00, 0x40, 0x00, 0x00, 0x00, 0x00, 0x00},
                        {0x94, 0x00, 0x40, 0x00, 0x00, 0x00, 0x00, 0x16},
                        {0x94, 0x00, 0x40, 0x00, 0x00, 0x00, 0xff, 0xff},
                        {0x94, 0x00, 0x40, 0x00, 0x00, 0x00, 0xff, 0xd5}};
        Slaw[] sx = {unt16(0), unt16(22), unt16(65535), unt16(65493)};
        check(sx, bs);
    }

    @Test public void int32s() {
        short[][] bs = {{0x88, 0x00, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00},
                        {0x88, 0x00, 0xc0, 0x00, 0x7f, 0xff, 0xff, 0xd5},
                        {0x88, 0x00, 0xc0, 0x00, 0x80, 0x00, 0x00, 0x16}};
        Slaw[] sx = {int32(0), int32(2147483605), int32(-2147483626)};
        check(sx, bs);
    }

    @Test public void unt32s() {
        short[][] bs = {{0x98, 0x00, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00},
                        {0x98, 0x00, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x16},
                        {0x98, 0x00, 0xc0, 0x00, 0xff, 0xff, 0xff, 0xff}};
        Slaw[] sx = {unt32(0), unt32(22), unt32(4294967295L)};
        check(sx, bs);
    }

    @Test public void int64s() {
        short[][] bs = {{0x8c, 0x01, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
                        {0x8c, 0x01, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x7f, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff},
                        {0x8c, 0x01, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x80, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x16}};
        Slaw[] sx = {int64(0), int64(9223372036854775807L),
                     int64(-9223372036854775786L)};
        check(sx, bs);
    }

    @Test public void unt64s() {
        short[][] bs = {{0x9c, 0x01, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
                        {0x9c, 0x01, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff},
                        {0x9c, 0x01, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0xa0, 0xb2, 0xff, 0xc3, 0xff, 0xaa, 0xbb, 0x16}};
        Slaw[] sx = {unt64(0), unt64(0xffffffffffffffffL),
                     unt64(0xa0b2ffc3ffaabb16L)};
        check(sx, bs);
    }

    @Test public void float32s() {
        short[][] bs = {{0xa8, 0x00, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00},
                        {0xa8, 0x00, 0xc0, 0x00, 0x00, 0x80, 0x00, 0x00},
                        {0xa8, 0x00, 0xc0, 0x00, 0x7f, 0x7f, 0xff, 0xff},
                        {0xa8, 0x00, 0xc0, 0x00, 0x41, 0xb0, 0x00, 0x00}};
        Slaw[] sx = {float32(0), float32(1.1754944e-38f),
                     float32(3.4028235e+38f), float32(22)};
        check(sx, bs);
    }

    @Test public void float64s() {
        short[][] bs = {{0xac, 0x01, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
                        {0xac, 0x01, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x7f, 0xef, 0xff, 0xff, 0xff, 0xff, 0xff, 0xff},
                        {0xac, 0x01, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x40, 0x36, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}};
        Slaw[] sx = {float64(0), float64(1.7976931348623157e+308),
                     float64(22)};
        check(sx, bs);
    }
}
