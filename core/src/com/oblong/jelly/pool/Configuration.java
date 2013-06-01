
/* (c)  oblong industries */

package com.oblong.jelly.pool;

/**
 * Created this as a place to put the GREENHOUSE flag.  Maybe someday
 * other stuff will go here too?
 */
public final class Configuration {
    /* This is the line you need to change, to make Jelly refuse
     * to connect to non-Greenhouse servers, as discussed in bug 8353.
     * Additionally, it will make the (now semi-supported) Jelly server
     * allow connections from Greenhouse.
     *
     * Note: You'll need to do "ant clean" after changing this constant
     * because ant doesn't understand dependencies on static final
     * constants. */
    public static final boolean GREENHOUSE = false;
}
