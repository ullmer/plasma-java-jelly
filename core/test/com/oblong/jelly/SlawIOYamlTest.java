// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.io.IOException;

import org.junit.Test;

import com.oblong.jelly.SlawIO.Format;
import com.oblong.jelly.SlawIO.YamlOptions;

/**
 * Unit tests for YAML externalization.
 *
 * @author jao
 */
public class SlawIOYamlTest extends SlawIOTestBase {

    public SlawIOYamlTest() { super(SlawIO.Format.YAML); }

    @Test public void noDirectives() throws IOException {
        final YamlOptions opts = new YamlOptions(true, false);
        final Slaw[][] ss = {noSlaw, atomicSlawx, compSlawx, nestedSlawx};
        for (Slaw[] s : ss) readWriteTest(s, opts);
    }
}
