// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;

import static com.oblong.jelly.SlawTests.*;

/**
 *  Unit Test for class atomic Slawx
 *
 *
 * Created: Fri May  7 00:51:21 2010
 *
 * @author jao
 */
public class AtomicSlawxTest {

    @Test public void nil() {
        Slaw nil = Slaw.nil();
        testIlk(nil, SlawIlk.NIL, NumericIlk.NAN);
        assertEquals(Slaw.nil(), nil);
        testAtomicEmissions(nil);
        testPairiness(nil);
        testListness(nil);
    }

    void testBool(boolean v) {
        Slaw s = Slaw.bool(v);
        testIlk(s, SlawIlk.BOOL, NumericIlk.NAN);
        assertEquals(Slaw.bool(v), s);
        assertEquals(v, s.emitBoolean());
        assertEquals(s, s.nth(0));
        testAtomicEmissions(s);
        testPairiness(s);
        testListness(s);
        testNotMap(s);
    }

    @Test public void bool() {
        testBool(true);
        testBool(false);
        assertFalse(Slaw.bool(true).equals(Slaw.bool(false)));
        assertFalse(Slaw.bool(false).equals(Slaw.bool(true)));
    }

    void testString(String str) {
        Slaw s = Slaw.string(str);
        testIlk(s, SlawIlk.STRING, NumericIlk.NAN);
        assertEquals(str, s.emitString());
        assertEquals(s, Slaw.string(str));
        testAtomicEmissions(s);
        testPairiness(s);
        testListness(s);
        testNotMap(s);
    }

    @Test public void string() {
        testString("foo");
        testString("");
    }
}
