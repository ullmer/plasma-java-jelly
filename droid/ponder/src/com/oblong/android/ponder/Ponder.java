// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.TableLayout;

/**
 *
 * Created: Tue Nov 23 15:57:08 2010
 *
 * @author jao
 */
public class Ponder extends Activity {

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final TableLayout layout = new TableLayout(getApplicationContext());
        setContentView(layout);

        final WifiManager wifi =
            (WifiManager)getSystemService(Context.WIFI_SERVICE);
        table = new ServerTable(wifi, layout);
    }

    @Override public void onStop() {
        super.onStop();
        table.deactivate();
    }

    @Override public void onResume() {
        super.onResume();
        table.activate();
    }

    private ServerTable table;
}
