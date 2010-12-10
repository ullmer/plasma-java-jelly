// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import android.app.Activity;
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
        name = (TextView)findViewById(R.id.server_name_entry);
        final TextView poolNo = (TextView)findViewById(R.id.pool_no_entry);
        final ListView poolList = (ListView)findViewById(R.id.pool_list);
        final View title =
            getLayoutInflater().inflate(R.layout.pools_title, null);
        if (title != null) poolList.addHeaderView(title, null, false);
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

    private void launchProteinBrowser(int pos) {
        if (pos < 1) return;
        final String pool = table.getPool(pos - 1);
        final String msg = String.format("Connecting to '%s' ...", pool);
        try {
            final PoolAddress address =
                new PoolAddress(serverInfo.server().address(), pool);
            launcher(address, ProgressDialog.show(this, "", msg)).start();
        } catch (PoolException e) {
            Ponder.logger().severe("Error launching Pool: " + e);
        }
    }

    private Thread launcher(final PoolAddress address,
                            final ProgressDialog dlg) {
        final Handler hdl = new Handler() {
            public void handleMessage(Message m) {
                if (m.obj != null) {
                    dlg.dismiss();
                    Pool.launch(Pools.this, (PoolAddress)m.obj);
                }
            }
        };
        return new Thread(new Runnable() {
                public void run() {
                    final PoolCursor c = PoolCursor.get(address);
                    c.prepareForAdapter();
                    hdl.sendMessage(Message.obtain(hdl, 0, address));
                }
            });
    }

    private static ServerInfo serverInfo;

    private TextView host;
    private TextView name;
    private PoolTable table;
}