// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServers;

public final class Pools extends Activity {

    static final String NAME_KEY = "com.oblong.android.ponder.Name";
    static final String URI_KEY = "com.oblong.android.ponder.URI";


    static void launch(Activity launcher, ServerInfoRow info) {
        final Intent intent = new Intent(launcher, Pools.class);
        intent.putExtra(NAME_KEY, info.name());
        intent.putExtra(URI_KEY, info.server().address().toString());
        launcher.startActivity(intent);
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pools);
        host = (TextView)findViewById(R.id.hostname_entry);
        port = (TextView)findViewById(R.id.port_entry);
        name = (TextView)findViewById(R.id.name_entry);
        poolList = (ListView)findViewById(R.id.pool_list);
    }

    @Override public void onStart() {
        super.onStart();
        final Intent intent = getIntent();
        final PoolServer srv =
            PoolServers.get(intent.getStringExtra(URI_KEY));
        host.setText(srv.address().host());
        port.setText(srv.address().port() + "");
        name.setText(intent.getStringExtra(NAME_KEY));
    }


    private TextView host;
    private TextView port;
    private TextView name;
    private ListView poolList;
}