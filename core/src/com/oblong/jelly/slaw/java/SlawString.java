
/* (c)  oblong industries */

package com.oblong.jelly.slaw.java;

import net.jcip.annotations.Immutable;

import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIlk;

@Immutable
public final class SlawString extends AtomicSlaw {

    static SlawString valueOf(String s) { return new SlawString(s); }

    @Override public SlawIlk ilk() { return SlawIlk.STRING; }
    @Override public NumericIlk numericIlk() { return null; }

    @Override public String emitString() { return val; }

    @Override public String toString() { return emitString(); }

    @Override public boolean slawEquals(Slaw o) {
        return o.emitString().equals(val);
    }

    @Override public int hashCode() { return val.hashCode(); }

    private SlawString(String s) { val = s; }
    private final String val;
}
