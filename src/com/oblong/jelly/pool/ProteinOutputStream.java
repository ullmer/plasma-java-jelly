// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool;

import java.io.IOException;

import com.oblong.jelly.Protein;
import com.oblong.jelly.slaw.SlawParseError;

/**
 *
 *
 * Created: Thu Jun 17 23:17:49 2010
 *
 * @author jao
 */
public interface ProteinOutputStream {

    void write(Protein p) throws SlawParseError, IOException;

}
