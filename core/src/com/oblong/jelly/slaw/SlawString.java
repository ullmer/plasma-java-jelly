package com.oblong.jelly.slaw;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIlk;

@Immutable
final class SlawString extends AtomicSlaw {

    static Slaw valueOf(String s) { return new SlawString(s); }

    @Override public SlawIlk ilk() { return SlawIlk.STRING; }
    @Override public NumericIlk numericIlk() { return null; }

    @Override public String emitString() { return val; }

    @Override public boolean slawEquals(Slaw o) {
        return o.emitString().equals(val);
    }

    @Override public int hashCode() { return val.hashCode(); }

    @Override public String debugString() { return val; }

    private SlawString(String s) { val = s; }
    private final String val;
}