// Copyright (c) 2010 Oblong Industries

package com.oblong.jelly;

/**
 *
 *
 *
 * Created: Thu Apr 15 00:53:15 2010
 *
 * @author jao
 */
public class SlawError extends Error {

    public static void setRuntimeErrors (boolean on) {
        errorsOn = on;
    }

    public static boolean errorsAreOn() {
        return errorsOn;
    }

    public SlawError(String msg) {
        super(msg);
    }

    public SlawError(String msg, Throwable e) {
        super(msg, e);
    }

    static void maybeThrow(String msg) {
        if (errorsOn) {
            throw new SlawError(msg);
        }
    }

    private static boolean errorsOn =
        new Boolean (System.getProperty("com.oblong.jelly.Slaw.errorsOn"));

}
