// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Test;

import static com.oblong.jelly.Slaw.*;

/**
 * Unit Test for class SlawProtein
 *
 * Created: Mon May 31 15:12:32 2010
 *
 * @author jao
 */
public class SlawProteinTest {

    @Test public void empty() {
        Protein p =  protein(null, null, null);
        assertEquals(null, p.ingests());
        assertEquals(null, p.descrips());
        assertEquals(0, p.data().length);
        assertEquals(protein(null, null, null), p);
    }

}
