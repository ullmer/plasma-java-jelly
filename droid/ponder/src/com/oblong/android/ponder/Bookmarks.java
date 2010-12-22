// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;

/**
 *
 * Created: Wed Dec 22 01:12:47 2010
 *
 * @author jao
 */
public final class Bookmarks extends ListActivity {

    static void launch(Activity launcher) {
        launcher.startActivity(new Intent(launcher, Bookmarks.class));
    }

    static void add(Activity adder, ServerInfo info) {
    }

    static void add(Activity adder, ServerInfo info, String poolName) {
    }


}
