// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.oblong.jelly.NumericIlk;
import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawIlk;

final class SlawByteArray extends JavaSlaw {

    static Slaw valueOf(boolean signed, byte[] data) {
        return new SlawByteArray(signed, data);
    }

    static Slaw valueOf(boolean signed, List<Slaw> cmps) {
        final byte[] data = new byte[cmps.size()];
        for (int i = 0; i < data.length; ++i)
            data[i] = cmps.get(i).emitByte();
        return valueOf(signed, data);
    }

    @Override public SlawIlk ilk() { return SlawIlk.NUMBER_ARRAY; }

    @Override public NumericIlk numericIlk() { return ilk; }

    @Override public byte[] unsafeEmitByteArray() { return data; }

    @Override public Slaw car() { return nth(0); }

    @Override public Slaw cdr() {
        ArrayList<Slaw> els = new ArrayList<Slaw>(data.length - 1);
        for (int i = 1; i < data.length; ++i) els.add(nth(i));
        return SlawList.valueOf(els, false);
    }

    @Override public int dimension() { return 0; }

    @Override public int count() { return data.length; }

    @Override public Slaw nth(int n) {
        return SlawNumber.valueOf(ilk, data[n]);
    }

    @Override public Slaw find(Slaw k) { return null; }

    @Override public boolean slawEquals(Slaw s) {
        return Arrays.equals(data, s.unsafeEmitByteArray());
    }

    @Override public int hashCode() { return Arrays.hashCode(data); }

    SlawByteArray(boolean signed, byte[] els) {
        assert els != null;
        ilk = signed ? NumericIlk.INT8 : NumericIlk.UNT8;
        data = els;
    }

    private final NumericIlk ilk;
    private final byte[] data;
}
