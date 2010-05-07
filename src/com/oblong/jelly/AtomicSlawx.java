// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


abstract class AtomicSlaw extends Slaw {

    @Override public final int count() { return 1; }

    @Override public final Slaw first() { return this; }

    @Override public final List<Slaw> emitList() {
        List<Slaw> result = new ArrayList<Slaw>(1);
        result.add(this);
        return result;
    }

    @Override public Slaw get(int n) {
        return n == 0 ? this : super.get(n);
    }

    @Override public int indexOf(Slaw elem) {
        return equals(elem) ? 0 : -1;
    }
}

final class SlawNil extends AtomicSlaw {

    static final SlawNil INSTANCE = new SlawNil();

    @Override public SlawIlk ilk() { return SlawIlk.NIL; }
    @Override public NumericIlk numericIlk() { return NumericIlk.NAN; }

    @Override boolean equals(Slaw o) { return true; }

    private SlawNil () {}
}

final class SlawBool extends AtomicSlaw {

    static Slaw valueOf(boolean b) { return b ? TRUE : FALSE; }

    @Override public SlawIlk ilk() { return SlawIlk.BOOL; }
    @Override public NumericIlk numericIlk() { return NumericIlk.NAN; }

    @Override public boolean emitBoolean() { return this == TRUE; }

    @Override boolean equals(Slaw o) {
        return o.emitBoolean() == emitBoolean();
    }

    private SlawBool () {}

    private static final SlawBool TRUE = new SlawBool();
    private static final SlawBool FALSE = new SlawBool();
}

final class SlawString extends AtomicSlaw {

    static Slaw valueOf(String s) { return new SlawString(s); }

    @Override public SlawIlk ilk() { return SlawIlk.STRING; }
    @Override public NumericIlk numericIlk() { return NumericIlk.NAN; }

    @Override public String emitString() { return val; }

    @Override public boolean equals(Slaw o) {
        return o.emitString() == val;
    }

    @Override public int hashCode() { return val.hashCode(); }

    private SlawString(String s) { val = s; }
    private final String val;
}
