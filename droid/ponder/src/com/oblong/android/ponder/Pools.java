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

    static void launch(Activity launcher, ServerInfo info) {
        if (info != null) {
            final Intent intent = new Intent(launcher, Pools.class);
            serverInfo = info;
            launcher.startActivity(intent);
        }
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
        host.setText(serverInfo.server().address().host());
        port.setText(serverInfo.server().address().port() + "");
        name.setText(serverInfo.name());
    }

    private static ServerInfo serverInfo;

    private TextView host;
    private TextView port;
    private TextView name;
    private ListView poolList;
}