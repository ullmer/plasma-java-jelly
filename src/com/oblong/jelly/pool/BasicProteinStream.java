// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool;

import java.io.IOException;

import com.oblong.jelly.Protein;
import com.oblong.jelly.slaw.SlawParseError;

/**
 *
 * Created: Fri Jun 18 22:50:56 2010
 *
 * @author jao
 */
public final class BasicProteinStream implements ProteinStream {

    public BasicProteinStream(ProteinInputStream in,
                              ProteinOutputStream out) {
        input = in;
        output = out;
    }

    @Override public Protein read() throws SlawParseError, IOException {
        return input.read();
    }

    @Override public void write(Protein p) throws IOException {
        output.write(p);
    }

    @Override public Protein query(Protein p)
        throws SlawParseError, IOException {
        output.write(p);
        return input.read();
    }

    private final ProteinInputStream input;
    private final ProteinOutputStream output;
}
