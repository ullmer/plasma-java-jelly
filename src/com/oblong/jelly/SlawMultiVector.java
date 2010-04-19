// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.List;

/**
 *
 * Created: Fri Apr 16 03:27:02 2010
 *
 * @author jao
 */
public interface SlawMultiVector extends NumericSlaw {
    enum Component { UP, DOWN; }

    int dimension();

    SlawNumber get(Component c0, Component c1);
    SlawNumber get(Component c0, Component c1, Component c2);
    SlawNumber get(Component c0, Component c1, Component c2, Component c3);
    SlawNumber get(Component c0, Component c1, Component c2,
                   Component c3, Component c4);

    SlawMultiVector curry(Component c);

    List<SlawNumber> asList();
}
