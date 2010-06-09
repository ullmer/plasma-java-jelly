// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw;

import java.nio.ByteBuffer;

import com.oblong.jelly.Slaw;

/*
 * Created: Sun Apr 18 01:46:42 2010
 *
 * @author jao
 */
public interface SlawExternalizer {

    ByteBuffer extern(Slaw s);
    int extern(Slaw s, ByteBuffer b);
    int externSize(Slaw s);
}
