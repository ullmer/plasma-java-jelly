// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * Created: Fri Apr 16 16:35:16 2010
 *
 * @author jao
 */
public interface NumericSlaw extends Slaw {
    enum Ilk {
        FLOAT64(true, false, 64), FLOAT32(true, false, 32),
        UNT64(false, true, 64), INT64(true, true, 64),
        UNT32(false, true, 32), INT32(true, true, 32),
        UNT16(false, true, 16), INT16(true, true, 16),
        UNT8(false, true, 8), INT8(true,true, 8);

        public final int width() { return width; }
        public final boolean isSigned() { return signed; }
        public final boolean isIntegral() { return integral; }

        public static final Ilk dominantIlk(List<Ilk> ilks) {
            Collections.sort(ilks);
            return ilks.get(0);
        }

        public static final Ilk dominantIlk(Ilk... ilks) {
            return dominantIlk(Arrays.asList(ilks));
        }

        public static final Ilk dominantIlk(NumericSlaw... nss) {
            List<Ilk> is = new ArrayList<Ilk>();
            for (NumericSlaw s : nss) is.add(s.ilk());
            return dominantIlk(is);
        }

        public static final <E extends NumericSlaw>
        List<E> withDominantIlk(E... nss) {
            Ilk di = dominantIlk(nss);
            List<E> is = new ArrayList<E>();
            for (E s : nss) {
                @SuppressWarnings("unchecked") E ss = (E)s.withIlk(di);
                is.add(ss);
            }
            return is;
        }

        private Ilk(boolean s, boolean i, int w) {
            signed = s;
            integral = i;
            width = w;
        }
        private final boolean signed;
        private final boolean integral;
        private final int width;
    }

    Ilk ilk();
    NumericSlaw withIlk(Ilk newIlk);
}
