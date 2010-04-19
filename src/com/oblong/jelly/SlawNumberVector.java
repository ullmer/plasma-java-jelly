// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.List;

/**
 *
 * Created: Mon Apr 19 13:59:24 2010
 *
 * @author jao
 */
public interface SlawNumberVector extends NumericSlaw {
    int dimension();
    SlawNumber get(int n);
    List<SlawNumber> asList();
}
