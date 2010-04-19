// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.List;

/**
 *
 * Created: Fri Apr 16 03:23:31 2010
 *
 * @author jao
 */
public interface SlawVector<E extends SlawComplex> extends NumericSlaw {
    int dimension();
    E get(int n);
    List<E> asList();
}
