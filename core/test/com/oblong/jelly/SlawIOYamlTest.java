// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.io.IOException;

import org.junit.Test;
import static org.junit.Assert.*;

import com.oblong.jelly.SlawIO.YamlOptions;

import static com.oblong.jelly.Slaw.*;

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
        fileTest(oneProtein, opts);
    }

    @Test public void noTags() throws IOException {
        final YamlOptions opts = new YamlOptions(false, true);
        final Slaw nos[] = SlawTests.numbers();
        final String data = SlawIO.toString(nos, opts);
        int i = 0;
        for (Slaw n : SlawIO.fromString(data)) {
            final String m = n + " (" + nos[i] + ")";
            assertTrue(m, n.isNumber());
            if (nos[i].numericIlk().isIntegral()) {
                assertEquals(m, NumericIlk.INT64, n.numericIlk());
                assertEquals(m, nos[i].emitLong(), n.emitLong());
            } else {
                assertEquals(m, NumericIlk.FLOAT64, n.numericIlk());
                assertEquals(m, nos[i].emitDouble(), n.emitDouble(), 0.0);
            }
            ++i;
        }
        assertEquals(nos.length, i);
    }

    @Test public void noTagsMixed() throws IOException {
        final YamlOptions opts = new YamlOptions(false, true);
        final Slaw pr = protein(list(string("a"), unt8(255)),
                                map(nil(), float32(3.14f)),
                                null);
        final Slaw s =
            SlawIO.fromString(SlawIO.toString(pr, opts)).get(0);
        assertTrue(s.isProtein());
        final Protein pr2 = s.toProtein();
        assertEquals(list(string("a"), int64(255)), pr2.descrips());
        assertEquals(3.14, pr2.ingests().find(nil()).emitDouble(), 0.001);
        assertEquals(0, pr2.dataLength());
    }
}
