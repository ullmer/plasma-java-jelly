// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import android.app.ListActivity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 *
 * Created: Tue Nov 23 15:57:08 2010
 *
 * @author jao
 */
public class Ponder extends ListActivity {

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final WifiManager wifi =
            (WifiManager)getSystemService(Context.WIFI_SERVICE);
        table = new ServerTable(this, wifi);

        final ListView lv = getListView();
        lv.setTextFilterEnabled(true);
    }

    @Override public void onResume() {
        super.onResume();
        table.activate();
    }

    @Override public void onStop() {
        super.onStop();
        table.deactivate();
    }

    private ServerTable table;
}
