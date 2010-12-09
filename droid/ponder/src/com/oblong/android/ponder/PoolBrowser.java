// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PoolBrowser extends ListActivity {

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String[] foo = {"foo", "bar"};
        pools = new ArrayAdapter<String>(this, R.layout.pool_item, foo);
        setListAdapter(pools);

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

    }

    private ArrayAdapter<String> pools;
}
