// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool;

import java.io.IOException;
import java.io.OutputStream;

import com.oblong.jelly.Protein;
import com.oblong.jelly.slaw.SlawExternalizer;

/**
 *
 * Created: Thu Jun 17 23:09:41 2010
 *
 * @author jao
 */
public final class BasicProteinOutputStream implements ProteinOutputStream {

    public BasicProteinOutputStream(OutputStream s, SlawExternalizer e) {
        stream = s;
        externalizer = e;
    }

    public void write(Protein protein) throws IOException {
        externalizer.extern(protein, stream);
    }

    private final OutputStream stream;
    private final SlawExternalizer externalizer;
}
