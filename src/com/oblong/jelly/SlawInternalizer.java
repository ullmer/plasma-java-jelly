// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.nio.ByteBuffer;

/*
 * Created: Tue May 25 14:26:51 2010
 *
 * @author jao
 */
interface SlawInternalizer {

    Protein internProtein(ByteBuffer b, SlawFactory f) throws SlawParseError;
    Slaw internSlaw(ByteBuffer b, SlawFactory f) throws SlawParseError;
}
