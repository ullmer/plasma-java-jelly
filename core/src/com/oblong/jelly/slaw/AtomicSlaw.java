// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw;

import com.oblong.jelly.Slaw;

abstract class AtomicSlaw extends JavaSlaw {

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
