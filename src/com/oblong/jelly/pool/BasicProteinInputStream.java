// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool;

import java.io.IOException;
import java.io.InputStream;

import com.oblong.jelly.Protein;
import com.oblong.jelly.slaw.SlawFactory;
import com.oblong.jelly.slaw.SlawInternalizer;
import com.oblong.jelly.slaw.SlawParseError;

/**
 *
 * Created: Thu Jun 17 22:36:48 2010
 *
 * @author jao
 */
public final class BasicProteinInputStream implements ProteinInputStream {

    public BasicProteinInputStream(InputStream s,
                                   SlawInternalizer i,
                                   SlawFactory f) {
        stream = s;
        internalizer = i;
        factory = f;
    }

    public Protein read() throws SlawParseError, IOException {
        return internalizer.internProtein(stream, factory);
    }

    private final InputStream stream;
    private final SlawInternalizer internalizer;
    private final SlawFactory factory;
}
