// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import net.jcip.annotations.Immutable;

abstract class AtomicSlaw extends Slaw {

    @Override public int dimension() { return 1; }
    @Override public final int count() { return 1; }

    @Override public final Slaw nth(int n) {
        if (n != 0) throw new IndexOutOfBoundsException();
        return this;
    }

    @Override public final Slaw find(Slaw elem) { return null; }

    @Override public final Slaw car() { return this; }
    @Override public final Slaw cdr() { return SlawList.EMPTY_LIST; }

}

@Immutable
final class SlawNil extends AtomicSlaw {

    static final SlawNil INSTANCE = new SlawNil();

    @Override public int dimension() { return 0; }

    @Override public SlawIlk ilk() { return SlawIlk.NIL; }
    @Override public NumericIlk numericIlk() { return null; }

    @Override boolean slawEquals(Slaw o) { return true; }

    @Override public String debugString() { return "nil"; }

    private SlawNil () {}
}

@Immutable
final class SlawBool extends AtomicSlaw {

    static Slaw valueOf(boolean b) { return b ? TRUE : FALSE; }

    @Override public SlawIlk ilk() { return SlawIlk.BOOL; }
    @Override public NumericIlk numericIlk() { return null; }

    @Override public boolean emitBoolean() { return this == TRUE; }

    @Override boolean slawEquals(Slaw o) {
        return o.emitBoolean() == emitBoolean();
    }

    @Override public String debugString() {
        return (this == TRUE) ? "true" : "false";
    }

    private SlawBool () {}

    private static final SlawBool TRUE = new SlawBool();
    private static final SlawBool FALSE = new SlawBool();
}

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
