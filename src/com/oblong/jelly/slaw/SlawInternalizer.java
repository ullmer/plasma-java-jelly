// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw;

import java.nio.ByteBuffer;

import com.oblong.jelly.Slaw;
import com.oblong.jelly.SlawFactory;
import com.oblong.jelly.Protein;

/*
 * Created: Tue May 25 14:26:51 2010
 *
 * @author jao
 */
public interface SlawInternalizer {

    Protein internProtein(ByteBuffer b, SlawFactory f) throws SlawParseError;
    Slaw internSlaw(ByteBuffer b, SlawFactory f) throws SlawParseError;
}
