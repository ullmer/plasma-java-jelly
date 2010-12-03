// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
        name = (TextView)findViewById(R.id.pool_name_entry);
        final TextView poolNo = (TextView)findViewById(R.id.pool_no_entry);
        final ListView poolList = (ListView)findViewById(R.id.pool_list);
        final View title =
            getLayoutInflater().inflate(R.layout.pools_title, null);
        if (title != null) poolList.addHeaderView(title);
        table = new PoolTable(poolList,
                              poolNo,
                              new AdapterView.OnItemClickListener () {
                                  public void onItemClick(AdapterView<?> p,
                                                          View v,
                                                          int position,
                                                          long i) {
                                      launchProteinBrowser(position);
                                  }
                              });
    }

    @Override public void onStart() {
        super.onStart();
        name.setText(serverInfo.name());
        host.setText(serverInfo.server().address().toString());
        table.update(serverInfo);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pool_list, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.refresh:
            serverInfo.clearPools();
            table.update(serverInfo);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void launchProteinBrowser(int position) {
        // TODO
    }

    private static ServerInfo serverInfo;

    private TextView host;
    private TextView name;
    private PoolTable table;
}