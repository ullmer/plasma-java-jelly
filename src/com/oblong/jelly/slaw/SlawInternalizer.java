// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly.slaw;

import java.io.IOException;
import java.io.InputStream;

import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;

/*
 * Created: Tue May 25 14:26:51 2010
 *
 * @author jao
 */
public interface SlawInternalizer {

    Protein internProtein(InputStream s, SlawFactory f)
        throws SlawParseError, IOException;
    Slaw internSlaw(InputStream s, SlawFactory f)
        throws SlawParseError, IOException;

}
