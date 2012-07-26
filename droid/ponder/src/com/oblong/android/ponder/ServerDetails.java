// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.oblong.jelly.PoolAddress;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolServer;

public final class ServerDetails
    extends PonderActivity implements AdapterView.OnItemClickListener {

    static void launch(Activity launcher, ServerInfo info) {
        if (info != null) {
            final Intent intent = new Intent(launcher, ServerDetails.class);
            serverInfo = info;
            launcher.startActivity(intent);
        }
    }

    public ServerDetails() {
        super("Error connecting to server");
        infoSetup(R.layout.server_info_dialog);
    }

     public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_details);
        host = (TextView)findViewById(R.id.hostname_entry);
        host.setOnClickListener(infoListener);
        name = (TextView)findViewById(R.id.server_name_entry);
        name.setOnClickListener(infoListener);
        final TextView poolNo = (TextView)findViewById(R.id.pool_no_entry);
        final ListView poolList = (ListView)findViewById(R.id.pool_list);
        final View title =
            getLayoutInflater().inflate(R.layout.pools_title, null);
        if (title != null) poolList.addHeaderView(title, null, false);
        table = new PoolTable(poolList, poolNo, this);
        findViewById(R.id.server_refresh_button).setOnClickListener(
            new View.OnClickListener () {
                public void onClick(View b) { refresh(); }
            });
    }

     public void onStart() {
        super.onStart();
        name.setText(serverInfo.server().name());
        host.setText(serverInfo.server().address().toString());
        table.update(serverInfo);
    }

     public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.server_details, menu);
        Bookmarks.prepareMenu(this, menu, serverInfo, null);
        return true;
    }

     public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.refresh:
            refresh();
            return true;
        case R.id.details:
            displayInfo();
            return true;
        case R.id.bookmark:
            Bookmarks.toggle(this, item, serverInfo, null);
            return true;
        case R.id.goto_bookmarks:
            Bookmarks.launch(this);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    
    public void onItemClick(AdapterView<?> p, View v, int position, long i) {
        showPoolDetails(position);
    }

     protected void prepareInfo(Dialog d) {
        final PoolServer srv = serverInfo.server();
        d.setTitle(srv.name());
        ((TextView)d.findViewById(R.id.host)).setText(srv.address().host());
        ((TextView)d.findViewById(R.id.port)).setText(
            String.format("%d", srv.address().port()));
        ((TextView)d.findViewById(R.id.subtypes)).setText(srv.subtype());
        ((TextView)d.findViewById(R.id.poolno)).setText(
            String.format("%d", serverInfo.poolNumber()));
    }

    private void showPoolDetails(int pos) {
        if (pos < 1) return;
        final String pool = table.getPool(pos - 1);
        try {
            final PoolAddress address =
                new PoolAddress(serverInfo.server().address(), pool);
            PoolDetails.launch(ServerDetails.this, serverInfo, address);
        } catch (PoolException e) {
            showError(e);
        }
    }

    private void refresh() {
        serverInfo.clearPools();
        table.update(serverInfo);
    }

    private static ServerInfo serverInfo;

    private TextView host;
    private TextView name;
    private PoolTable table;
}