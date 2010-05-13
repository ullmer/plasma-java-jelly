// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.nio.ByteOrder;

import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;

import static com.oblong.jelly.Slaw.*;

/**
 *  Unit Test for class PlasmaExternalizerV2
 *
 *
 * Created: Tue Apr 20 13:07:13 2010
 *
 * @author jao
 */
public class PlasmaExternalizerV2Test {
    static final SlawExternalizer externalizer = new PlasmaExternalizerV2();

    static String arrayStr(byte[] bs) {
        StringBuilder buf = new StringBuilder ("{ ");
        for (byte b : bs) buf.append(b + " ");
        buf.append("}");
        return buf.toString();
    }

    static void check(Slaw s, byte[] b, String msg) {
        final byte[] sb = externalizer.extern(s).array();
        String m = msg + ": " + arrayStr(sb) + " vs. expected " + arrayStr(b);
        assertEquals(sb.length, externalizer.externSize(s));
        assertArrayEquals(m, b, sb);
    }

    static void check(Slaw[] s, short[][] b) {
        for (int i = 0; i < s.length; i++) {
            byte[] bb = new byte[b[i].length];
            for (int j = 0; j < b[i].length; ++j) bb[j] = (byte)b[i][j];
            check(s[i], bb, i + "th iteration");
        }
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
                        {0x90, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x6A},
                        {0x90, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x7f}};
        Slaw[] sx = {unt8(0), unt8(0x6A), unt8(0x7f)};
        check(sx, bs);
    }

    @Test public void int16s() {
        short[][] bs = {{0x84, 0x00, 0x40, 0x00, 0x00, 0x00, 0x00, 0x00},
                        {0x84, 0x00, 0x40, 0x00, 0x00, 0x00, 0x80, 0x00},
                        {0x84, 0x00, 0x40, 0x00, 0x00, 0x00, 0x7f, 0xff}};

        Slaw[] sx = {int16(0), int16(-32768), int16(32767)};
        check(sx, bs);
    }
}
