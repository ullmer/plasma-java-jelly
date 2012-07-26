// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import android.os.Handler;
import android.os.Message;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.oblong.jelly.PoolServerAddress;

final class PoolTable {

    PoolTable(ListView v, TextView nv, AdapterView.OnItemClickListener lst) {
        poolView = v;
        noView = nv;
        adapter = new PoolListAdapter(v.getContext());
        v.setAdapter(adapter);
        v.setOnItemClickListener(lst);
    }

    void update(final ServerInfo info) {
        fill(info);
        final Handler hdl = new Handler () {
                 public void handleMessage(Message m) {
                    if (m.what == 0) fill(info);
                }
            };
        new Thread(new Runnable () {
                 public void run() {
                    info.updatePools();
                    hdl.sendMessage(Message.obtain(hdl, 0, null));
                }
            }).start();
    }

    String getPool(int position) { return adapter.getItem(position).name; }

    private void fill(ServerInfo info) {
        adapter.clear();
        if (info != null) {
            if (info.connectionError()) {
                noView.setError("Error connecting to the pool");
            } else {
                noView.setText(info.poolNumberStr());
                if (info.pools() != null) {
                    final PoolServerAddress a = info.server().address();
                    for (String p : info.pools()) adapter.add(a, p);
                }
            }
            noView.invalidate();
            adapter.notifyDataSetChanged();
        }
    }

    private final ListView poolView;
    private final TextView noView;
    private final PoolListAdapter adapter;
}
