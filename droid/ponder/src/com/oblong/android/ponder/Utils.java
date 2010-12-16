// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import java.util.Set;

/**
 *
 * Created: Mon Dec 13 20:41:46 2010
 *
 * @author jao
 */
final class Utils {

    static String join(Set<String> s, String sep) {
        if (s.size() == 0) return "";
        final String[] es = s.toArray(new String[0]);
        final StringBuilder b = new StringBuilder();
        b.append(es[0]);
        for (int i = 1; i < es.length; ++i) b.append(sep).append(es[i]);
        return b.toString();
    }

    static String formatNumber(long number, String str) {
        return number + " " + (number == 1 ? str : str + "s");
    }

    static String formatSize(long number) {
        float result = number;
        String suffix = "b";
        if (result > 900) {
            suffix = "K";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "M";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "G";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "T";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "P";
            result = result / 1024;
        }
        String value;
        if (result < 1) {
            value = String.format("%.2f %s", result, suffix);
        } else if (result < 10) {
            value = String.format("%.1f %s", result, suffix);
        } else if (result < 100) {
            value = String.format("%.0f %s", result, suffix);
        } else {
            value = String.format("%.0f %s", result, suffix);
        }
        return value;
    }

    private Utils() {}
}
