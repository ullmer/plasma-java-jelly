// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

/**
 * Base class for all Slaw implementations answering false to all type
 * queries and returning an error (or, optionally, throwing it) for
 * all type coercions.
 *
 * @author jao
*/
abstract class AbstractSlaw implements Slaw {

    @Override public final boolean equals(Object o) {
        if (!(o instanceof Slaw)) return false;
        return equals((Slaw)o);
    }

    @Override public boolean isAtomic() { return false; }

    @Override public boolean isNil() { return false; }

    @Override public boolean isBool() { return false; }
    @Override public SlawBool bool() { return error("SlawBool"); }

    @Override public boolean isString() { return false; }
    @Override public SlawString string() { return error("SlawString"); }

    @Override public boolean isNumeric() { return false; }

    @Override public boolean isNumber() { return false; }
    @Override public SlawNumber number() { return error("SlawNumber"); }

    @Override public boolean isComplex() { return false; }
    @Override public SlawComplex complex() { return error("SlawComplex"); }

    @Override public boolean isCons() { return false; }
    @Override public SlawCons cons() { return error("SlawCons"); }

    @Override public boolean isList() { return false; }
    @Override public SlawList list() { return error("SlawList"); }

    @Override public boolean isMap() { return false; }
    @Override public SlawMap map() { return error("SlawMap"); }

    @Override public boolean isNumberArray() { return false; }
    @Override public boolean isComplexArray() { return false; }
    @Override public boolean isVectorArray() { return false; }
    @Override public boolean isComplexVectorArray() { return false; }
    @Override public boolean isMultiVectorArray() { return false; }
    @Override public <E extends NumericSlaw> SlawArray<E> array() {
        return error("SlawArray");
    }

    @Override public boolean isNumberVector() { return false; }
    @Override public boolean isComplexVector() { return false; }
    @Override public SlawNumberVector numberVector() {
        return error("SlawNumberVector");
    }
    @Override public SlawComplexVector complexVector() {
        return error("SlawComplexVector");
    }

    @Override public boolean isMultiVector() { return false; }
    @Override public SlawMultiVector multiVector() {
        return error("SlawMultiVector");
    }

    private <E> E error(String s) {
        SlawError.maybeThrow("Invalid conversion from " + getClass() +
                             " to " + s);
        return null;
    }
}