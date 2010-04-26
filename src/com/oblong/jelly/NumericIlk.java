package com.oblong.jelly;

/**
 * Created: Fri Apr 23 13:34:13 2010
 *
 * @author jao
 */
public enum NumericIlk {

    FLOAT64(true, false, 64), FLOAT32(true, false, 32),
    UNT64(false, true, 64), INT64(true, true, 64),
    UNT32(false, true, 32), INT32(true, true, 32),
    UNT16(false, true, 16), INT16(true, true, 16),
    UNT8(false, true, 8), INT8(true,true, 8),
    NAN(false, false, 0);

    public int width() { return width; }
    public int bytes() { return bsize; }
    public boolean isSigned() { return signed; }
    public boolean isIntegral() { return integral; }

    public static NumericIlk dominantIlk(List<NumericIlk> ilks) {
        try {
            return Collections.max(ilks);
        } catch (NoSuchElementException e) {
            return INT8;
        }
    }

    public static NumericIlk dominantIlk(NumericIlk... ilks) {
        return dominantIlk(Arrays.asList(ilks));
    }

    public static NumericIlk dominantIlk(Slaw... nss) {
        List<NumericIlk> is = new ArrayList<NumericIlk>();
        for (Slaw s : nss) is.add(s.ilk());
        return dominantIlk(is);
    }

    private NumericIlk(boolean s, boolean i, int w) {
        signed = s;
        integral = i;
        width = w;
        bsize = (byte)(w >>> 3);
    }

    private final boolean signed;
    private final boolean integral;
    private final int width;
    private final byte bsize;
}
