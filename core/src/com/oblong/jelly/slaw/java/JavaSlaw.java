// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.java;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.Slaw;

/**
 *
 * Created: Fri Jun 11 00:22:04 2010
 *
 * @author jao
 */
abstract class JavaSlaw extends Slaw {

    @Override public boolean emitBoolean() {
        throw new UnsupportedOperationException(ilk() + " as boolean");
    }

    @Override public String emitString() {
        throw new UnsupportedOperationException(ilk() + " as string");
    }

    @Override public long emitLong() {
        throw new UnsupportedOperationException(ilk() + " as long");
    }

    @Override public int emitInt() {
        throw new UnsupportedOperationException(ilk() + " as int");
    }

    @Override public short emitShort() {
        throw new UnsupportedOperationException(ilk() + " as short");
    }

    @Override public byte emitByte() {
        throw new UnsupportedOperationException(ilk() + " as byte");
    }

    @Override public byte[] unsafeEmitByteArray() {
        throw new UnsupportedOperationException(ilk() + " as byte[]");
    }

    @Override public double emitDouble() {
        throw new UnsupportedOperationException(ilk() + " as double");
    }

    @Override public float emitFloat() {
        throw new UnsupportedOperationException(ilk() + " as float");
    }

    @Override public BigInteger emitBigInteger() {
        throw new UnsupportedOperationException(ilk() + " as big integer");
    }

    @Override public Slaw car() {
        throw new UnsupportedOperationException(ilk() + "as pair");
    }

    @Override public Slaw cdr() {
        throw new UnsupportedOperationException(ilk() + "as pair");
    }

    @Override public Map<Slaw,Slaw> emitMap() {
	    throw new UnsupportedOperationException(ilk() + "as map");
//        return new HashMap<Slaw,Slaw>(); // TODO: this might be hiding bugs
    }

    @Override public Slaw withNumericIlk(NumericIlk ilk) {
        throw new UnsupportedOperationException(ilk() + " not numeric");
    }
}
