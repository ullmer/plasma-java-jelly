package com.oblong.jelly;

abstract class NativeAtomicSlaw extends AbstractSlaw {
    @Override public boolean isAtomic() { return true; }
}

final class NativeSlawNil extends NativeAtomicSlaw {
    static final NativeSlawNil INSTANCE = new NativeSlawNil();

    @Override public boolean isNil() { return true; }

    @Override public boolean equals(Slaw other) { return other.isNil(); }

    @Override public int hashCode() {return 17; }

    @Override public byte[] externalize(SlawExternalizer e) {
        return e.externalize(this);
    }

    private NativeSlawNil () {}
}

final class NativeSlawBool extends NativeAtomicSlaw implements SlawBool {
    static SlawBool valueOf(boolean b) { return b ? TRUE : FALSE; }

    @Override public boolean isBool() { return true; }
    @Override public SlawBool bool() { return this; }

    @Override public boolean value() { return this == TRUE; }

    @Override public boolean equals(Slaw other) {
        if (!(other instanceof SlawBool)) return false;
        return !(value() ^ ((SlawBool)other).value());
    }

    @Override public int hashCode() { return this == TRUE ? 18 : 19; }

    @Override public byte[] externalize(SlawExternalizer e) {
        return e.externalize(this);
    }

    private NativeSlawBool () {}
    private static final NativeSlawBool TRUE = new NativeSlawBool();
    private static final NativeSlawBool FALSE = new NativeSlawBool();
}

final class NativeSlawString extends NativeAtomicSlaw implements SlawString {
    static SlawString valueOf(String s) {
        return new NativeSlawString(s);
    }

    @Override public boolean isString() { return true; }
    @Override public SlawString string() { return this; }

    @Override public String value() { return this.val; }

    @Override public boolean equals(Slaw other) {
        if (!(other instanceof SlawString)) return false;
        return this.val.equals(((SlawString)other).value());
    }

    @Override public int hashCode() { return this.val.hashCode(); }

    @Override public byte[] externalize(SlawExternalizer e) {
        return e.externalize(this);
    }

    private NativeSlawString(String s) { this.val = s; }
    private final String val;
}
