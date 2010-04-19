// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

/**
 *
 * Created: Fri Apr 16 21:09:00 2010
 *
 * @author jao
 */
public interface SlawArray<E extends NumericSlaw> extends NumericSlaw {
    int count();
    E[] getElements();
}
