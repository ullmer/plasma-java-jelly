// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.List;

/**
 *
 *
 * Created: Mon Apr 19 14:11:28 2010
 *
 * @author jao
 */
public interface SlawComplexVector extends NumericSlaw {
    int dimension();
    SlawComplex get(int n);
    List<SlawComplex> asList();
}
