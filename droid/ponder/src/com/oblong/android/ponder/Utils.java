// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

/**
 *
 * Created: Mon Dec 13 20:41:46 2010
 *
 * @author jao
 */
final class Utils {

    static String formatNumber(long number, String str) {
        return number + " " + (number == 1 ? str : str + "s");
    }

    static String formatSize(long number) {
        float result = number;
        String suffix = "b";
        if (result > 900) {
            suffix = "Kb";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "Mb";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "Gb";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "Tb";
            result = result / 1024;
        }
        if (result > 900) {
            suffix = "Pb";
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
