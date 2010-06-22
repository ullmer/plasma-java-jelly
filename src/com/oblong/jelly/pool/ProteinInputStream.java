// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool;

import java.io.IOException;

import com.oblong.jelly.Protein;
import com.oblong.jelly.slaw.SlawFactory;
import com.oblong.jelly.slaw.SlawParseError;

/**
 *
 *
 * Created: Fri Jun 18 21:29:57 2010
 *
 * @author jao
 */
public interface ProteinInputStream {

    Protein read() throws SlawParseError, IOException;
    SlawFactory factory();
    void close() throws IOException;
}
