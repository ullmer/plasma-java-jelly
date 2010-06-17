// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.io.IOException;
import java.io.OutputStream;
import net.jcip.annotations.Immutable;

/**
 *
 * Created: Mon May 17 14:25:51 2010
 *
 * @author jao
 */
@Immutable
public abstract class Protein extends Slaw {

    public abstract Slaw ingests();
    public abstract Slaw descrips();

    public abstract byte data(int n);
    public abstract int dataLength();
    public abstract int putData(OutputStream os) throws IOException;
}
