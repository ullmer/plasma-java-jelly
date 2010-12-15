// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import com.oblong.jelly.PoolException;

/**
 *
 * Created: Wed Dec 15 22:22:45 2010
 *
 * @author jao
 */
final class PoolExceptionAlert {

    static Dialog create(Activity a, int id, String title, Bundle b) {
        if (id != POOL_EXCEPTION_DIALOG) return null;
        final AlertDialog.Builder builder = new AlertDialog.Builder(a);
        builder.setTitle(title);
        builder.setCancelable(true);
        return builder.create();
    }

    static boolean prepare(int id, Dialog d, Bundle b) {
        if (id != POOL_EXCEPTION_DIALOG) return false;
        ((AlertDialog)d).setMessage(b.getString(POOL_EXCEPTION_KEY));
        return true;
    }

    static void show(Activity a, PoolException e) {
        final Bundle b = new Bundle();
        b.putString(POOL_EXCEPTION_KEY, e.getMessage());
        a.showDialog(POOL_EXCEPTION_DIALOG, b);
    }

    private static final int POOL_EXCEPTION_DIALOG = 1;
    private static final String POOL_EXCEPTION_KEY = "poolException";

    private PoolExceptionAlert() {}
}
