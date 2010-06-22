// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool;

import java.io.IOException;

import com.oblong.jelly.Protein;
import com.oblong.jelly.slaw.SlawParseError;

/**
 *
 *
 * Created: Fri Jun 18 22:48:32 2010
 *
 * @author jao
 */
public interface ProteinStream
    extends ProteinInputStream, ProteinOutputStream {

    Protein query(Protein p) throws SlawParseError, IOException;
    void close() throws IOException;
}
