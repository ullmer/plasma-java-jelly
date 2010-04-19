// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.math.BigInteger;

/**
 *
 * Created: Fri Apr 16 02:57:38 2010
 *
 * @author jao
 */
public interface SlawNumber extends SlawComplex {

    SlawNumber withIlk(Ilk ilk);

    byte byteValue();
    short shortValue();
    int intValue();
    long longValue();
    float floatValue();
    double doubleValue();
    BigInteger bigIntegerValue();
}
