// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw;

import java.io.IOException;
import java.io.OutputStream;

import com.oblong.jelly.Slaw;

/*
 * Created: Sun Apr 18 01:46:42 2010
 *
 * @author jao
 */
public interface SlawExternalizer {

    long extern(Slaw s, OutputStream os) throws IOException;

}
