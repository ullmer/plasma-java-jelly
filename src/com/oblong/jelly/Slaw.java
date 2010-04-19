// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

/**
 * Created: Mon Apr 12 16:46:30 2010
 *
 * @author jao
 */
public interface Slaw extends ExternalizableSlaw {

    boolean equals(Slaw s);
    int hashCode();

    boolean isAtomic();

    boolean isNil();

    boolean isBool();
    SlawBool bool();

    boolean isString();
    SlawString string();

    boolean isNumeric();

    boolean isNumber();
    SlawNumber number();

    boolean isComplex();
    SlawComplex complex();

    boolean isCons();
    SlawCons cons();

    boolean isList();
    SlawList list();

    boolean isMap();
    SlawMap map();

    boolean isNumberArray();
    boolean isComplexArray();
    boolean isVectorArray();
    boolean isComplexVectorArray();
    boolean isMultiVectorArray();
    SlawNumberArray numberArray();
    SlawComplexArray complexArray();
    SlawNumberVectorArray numberVectorArray();
    SlawComplexVectorArray complexVectorArray();

    boolean isNumberVector();
    boolean isComplexVector();
    SlawNumberVector numberVector();
    SlawComplexVector complexVector();

    boolean isMultiVector();
    SlawMultiVector multiVector();
}


interface ExternalizableSlaw {
    byte[] externalize(SlawExternalizer e);
}
