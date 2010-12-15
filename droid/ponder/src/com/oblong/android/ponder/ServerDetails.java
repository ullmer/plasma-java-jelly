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

public final class ServerDetails
    extends Activity implements AdapterView.OnItemClickListener {

    static void launch(Activity launcher, ServerInfo info) {
        if (info != null) {
            final Intent intent = new Intent(launcher, ServerDetails.class);
            serverInfo = info;
            launcher.startActivity(intent);
        }
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_details);
        host = (TextView)findViewById(R.id.hostname_entry);
        name = (TextView)findViewById(R.id.server_name_entry);
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
            refresh();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> p, View v, int position, long i) {
        showPoolDetails(position);
    }

    private void refresh() {
        serverInfo.clearPools();
        table.update(serverInfo);
    }

    private void showPoolDetails(int pos) {
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
                    if (m.what == 0)
                        PoolDetails.launch(ServerDetails.this,
                                           (PoolAddress)m.obj);
                    else
                        displayError(address, (PoolException)m.obj);
                }
            }
        };
        return new Thread(new Runnable() {
                public void run() {
                    try {
                        final PoolCursor c = PoolInfo.get(address).cursor();
                        c.prepareForAdapter();
                        hdl.sendMessage(Message.obtain(hdl, 0, address));
                    } catch (PoolException e) {
                        hdl.sendMessage(Message.obtain(hdl, 1, e));
                    }
                }
            });
    }

    private void displayError(PoolAddress addr, PoolException e) {
        // TODO
        Ponder.logger().severe("Error connecting pool " + addr
                               + ": " + e.getMessage());
    }

    private static ServerInfo serverInfo;

    private TextView host;
    private TextView name;
    private PoolTable table;
}