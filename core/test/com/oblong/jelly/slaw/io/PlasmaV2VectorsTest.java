// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.io;

import org.junit.Test;

import com.oblong.jelly.*;
import com.oblong.jelly.slaw.*;
import com.oblong.jelly.slaw.io.BinaryExternalizer;
import com.oblong.jelly.slaw.io.BinaryInternalizer;

import static com.oblong.jelly.Slaw.*;

/**
 * Unit Test for class PlasmaV2 serialization: vectors.
 *
 * Created: Wed May 19 13:27:14 2010
 *
 * @author jao
 */
public class PlasmaV2VectorsTest extends SerializationTestBase {

    public PlasmaV2VectorsTest() {
        super(new BinaryExternalizer(), new BinaryInternalizer());
    }

    @Test public void vector8() {
        short[][] bs = {{0x80, 0x40, 0x40, 0x00, 0x00, 0x00, 0x02, 0x2a},
                        {0x94, 0x40, 0xc0, 0x00, 0x12, 0x34, 0x56, 0x78},
                        {0x80, 0x80, 0x80, 0x00, 0x00, 0x03, 0x01, 0x02},
                        {0x90, 0xc0, 0xc0, 0x00, 0x04, 0x03, 0x01, 0x02},
                        {0x82, 0x40, 0xc0, 0x00, 0x16, 0xfd, 0x02, 0xff}};
        Slaw[] sx = {vector(int8(2), int8(42)),
                     vector(unt16(0x1234), unt16(0x5678)),
                     vector(int8(3), int8(1), int8(2)),
                     vector(unt8(4), unt8(3), unt8(1), unt8(2)),
                     vector(complex(int8(22), int8(-3)),
                            complex(int8(2), int8(-1)))};
        check(sx, bs);
    }

    @Test public void vector16() {
        short[][] bs = {{0x88, 0x41, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x2a},
                        {0x98, 0x41, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08},
                        {0xa8, 0x41, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x40, 0x46, 0x66, 0x66, 0xc1, 0x94, 0x7a, 0xe1},
                        {0x94, 0x81, 0x40, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x00, 0x12, 0x00, 0x34, 0x00, 0x56, 0x00, 0x00},
                        {0x84, 0xc1, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x00, 0x12, 0x00, 0x34, 0x00, 0x56, 0x00, 0x01},
                        {0x86, 0x41, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08}};
        Slaw[] sx = {vector(int32(2), int32(42)),
                     vector(unt32(0x01020304), unt32(0x05060708)),
                     vector(float32(3.1f), float32(-18.56f)),
                     vector(unt16(0x12), unt16(0x34), unt16(0x56)),
                     vector(int16(0x12), int16(0x34), int16(0x56), int16(1)),
                     vector(complex(int16(0x0102), int16(0x0304)),
                            complex(int16(0x0506), int16(0x0708)))};
        check(sx, bs);
    }

    @Test public void vector24() {
        short[][] bs = {{0x8c, 0x43, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                         0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10},
                        {0x9c, 0x43, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01},
                        {0xac, 0x43, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x40, 0x08, 0xcc, 0xcc, 0xcc, 0xcc, 0xcc, 0xcd,
                         0xc0, 0x32, 0x8f, 0x5c, 0x28, 0xf5, 0xc2, 0x8f},
                        {0xa8, 0x82, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x40, 0x20, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0xc2, 0xca, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
                        {0x88, 0xc3, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x02,
                         0x00, 0x00, 0x00, 0x03, 0x00, 0x00, 0x00, 0x04},
                        {0x9a, 0x43, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x00, 0x00, 0x00, 0x17, 0x00, 0x00, 0x00, 0x18,
                         0x00, 0x00, 0x00, 0x0c, 0x00, 0x00, 0x00, 0x2b},
                        {0x96, 0x82, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                         0x09, 0x0a, 0x0b, 0x0c, 0x00, 0x00, 0x00, 0x00},
                        {0x86, 0xc3, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                         0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10}};
        Slaw[] sx = {vector(int64(0x0102030405060708L),
                            int64(0x090a0b0c0d0e0f10L)),
                     vector(unt64(0), int64(1)),
                     vector(float64(3.1), float64(-18.56)),
                     vector(float32(2.5f), float32(0f), float32(-101f)),
                     vector(int32(1), int32(2), int32(3), int32(4)),
                     vector(complex(unt32(23), int8(24)),
                            complex(int16(12), unt16(43))),
                     vector(complex(int16(0x0102), int16(0x0304)),
                            complex(int16(0x0506), unt16(0x0708)),
                            complex(int16(0x090a), int16(0x0b0c))),
                     vector(complex(int16(0x0102), int16(0x0304)),
                            complex(int16(0x0506), int16(0x0708)),
                            complex(int16(0x090a), int16(0x0b0c)),
                            complex(int16(0x0d0e), int16(0x0f10)))};
        check(sx, bs);
    }

    @Test public void vector32() {
        short[][] bs = {{0x9c, 0x85, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                         0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10,
                         0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18},
                        {0x8c, 0x85, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03},
                        {0xac, 0x85, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x40, 0x01, 0x99, 0x99, 0x99, 0x99, 0x99, 0x9a,
                         0x40, 0xc8, 0x1b, 0xf8, 0xb4, 0x39, 0x58, 0x10,
                         0x3f, 0x84, 0x7a, 0xe1, 0x47, 0xae, 0x14, 0x7b},
                        {0xaa, 0x85, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x3f, 0x80, 0x00, 0x00, 0x40, 0x06, 0x66, 0x66,
                         0x40, 0x4c, 0xcc, 0xcd, 0x40, 0x89, 0x99, 0x9a,
                         0x40, 0xac, 0xcc, 0xcd, 0x40, 0xb0, 0x00, 0x00}};
        Slaw[] sx = {vector(unt64(0x0102030405060708L),
                            unt64(0x090a0b0c0d0e0f10L),
                            unt64(0x1112131415161718L)),
                     vector(int64(1), int64(2), int64(3)),
                     vector(float64(2.2), float64(12343.943), float64(0.01)),
                     vector(complex(float32(1.0f), float32(2.1f)),
                            complex(float32(3.2f), float32(4.3f)),
                            complex(float32(5.4f), float32(5.5f)))};
        check(sx, bs);
    }

    @Test public void vector40() {
        short[][] bs = {{0x8c, 0xc7, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                         0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f, 0x10,
                         0x11, 0x12, 0x13, 0x14, 0x15, 0x16, 0x17, 0x18,
                         0x19, 0x1a, 0x1b, 0x1c, 0x1d, 0x1e, 0x1f, 0x20},
                        {0x9c, 0xc7, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04},
                        {0xac, 0xc7, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x40, 0x01, 0x99, 0x99, 0x99, 0x99, 0x99, 0x9a,
                         0x40, 0xc8, 0x1b, 0xf8, 0xb4, 0x39, 0x58, 0x10,
                         0x3f, 0x84, 0x7a, 0xe1, 0x47, 0xae, 0x14, 0x7b,
                         0x3f, 0x94, 0x7a, 0xe1, 0x47, 0xae, 0x14, 0x7b},
                        {0xaa, 0xc7, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x3f, 0x80, 0x00, 0x00, 0x40, 0x06, 0x66, 0x66,
                         0x40, 0x4c, 0xcc, 0xcd, 0x40, 0x89, 0x99, 0x9a,
                         0x40, 0xac, 0xcc, 0xcd, 0x40, 0xb0, 0x00, 0x00,
                         0x40, 0xd0, 0x00, 0x00, 0x40, 0xd9, 0x99, 0x9a},
                        {0x8e, 0x47, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04}};
        Slaw[] sx = {vector(int64(0x0102030405060708L),
                            int64(0x090a0b0c0d0e0f10L),
                            int64(0x1112131415161718L),
                            int64(0x191a1b1c1d1e1f20L)),
                     vector(unt64(1), unt64(2), unt64(3), unt64(4)),
                     vector(float64(2.2), float64(12343.943),
                            float64(0.01), float64(0.02)),
                     vector(complex(float32(1.0f), float32(2.1f)),
                            complex(float32(3.2f), float32(4.3f)),
                            complex(float32(5.4f), float32(5.5f)),
                            complex(float32(6.5f), float32(6.8f))),
                     vector(complex(int64(1), int64(2)),
                            complex(int64(3), int64(4)))};
        check(sx, bs);
    }

    @Test public void vector56() {
        short[][] bs = {{0x8e, 0x8b, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x06},
                        {0x9e, 0x8b, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x06, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00},
                        {0xae, 0x8b, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x3f, 0xf0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x40, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x40, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x40, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x40, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x40, 0x14, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00}};
        Slaw[] sx = {vector(complex(int64(1), int64(2)),
                            complex(int64(3), int64(4)),
                            complex(int64(5), int64(6))),
                     vector(complex(int64(1L<<56), int64(2L<<56)),
                            complex(unt64(3L<<56), int64(4L<<56)),
                            complex(int64(5L<<56), int64(6L<<56))),
                     vector(complex(float64(1), float64(2)),
                            complex(float64(3), float64(4)),
                            complex(float64(4), float64(5)))};
        check(sx, bs);
    }

    @Test public void vector72() {
        short[][] bs = {{0x8e, 0xcf, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x04,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x06,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x07,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x08},
                        {0x9e, 0xcf, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x04, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x06, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01,
                         0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02},
                        {0xae, 0xcf, 0xc0, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x3f, 0xf0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x40, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x40, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x40, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x40, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x40, 0x14, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                         0x40, 0x09, 0x21, 0xfb, 0x54, 0x44, 0x2d, 0x18,
                         0x40, 0x05, 0xbf, 0x0a, 0x8b, 0x14, 0x57, 0x69}};
        Slaw[] sx = {vector(complex(int64(1), int64(2)),
                            complex(int64(3), int64(4)),
                            complex(int64(5), int64(6)),
                            complex(int64(7), int8(8))),
                     vector(complex(int64(1L<<56), int64(2L<<56)),
                            complex(unt64(3L<<56), int64(4L<<56)),
                            complex(int64(5L<<56), int64(6L<<56)),
                            complex(int32(1), unt16(2))),
                     vector(complex(float64(1), float64(2)),
                            complex(float64(3), float64(4)),
                            complex(float64(4), float64(5)),
                            complex(float64(3.141592653589793),
                                    float64(2.718281828459045)))};
        check(sx, bs);
    }
}
