// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.pool;

import java.io.IOException;

import com.oblong.jelly.Protein;

/**
 *
 *
 * Created: Thu Jun 17 23:17:49 2010
 *
 * @author jao
 */
public interface ProteinOutputStream {

    void write(Protein p) throws IOException;
    void close() throws IOException;
}
