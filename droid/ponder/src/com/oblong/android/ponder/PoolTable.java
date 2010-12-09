// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import android.os.Handler;
import android.os.Message;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

final class PoolTable {

    PoolTable(ListView v, TextView nv, AdapterView.OnItemClickListener lst) {
        poolView = v;
        noView = nv;
        adapter =
            new ArrayAdapter<String>(v.getContext(), R.layout.pool_item);
        v.setAdapter(adapter);
        v.setOnItemClickListener(lst);
    }

    void update(final ServerInfo info) {
        fill(info);
        final Handler hdl = new Handler () {
                @Override public void handleMessage(Message m) {
                    if (m.what == 0) fill(info);
                }
            };
        new Thread(new Runnable () {
                @Override public void run() {
                    info.updatePools();
                    hdl.sendMessage(Message.obtain(hdl, 0, null));
                }
            }).start();
    }

    String getPool(int position) { return adapter.getItem(position); }

    private void fill(ServerInfo info) {
        adapter.clear();
        if (info != null) {
            if (info.connectionError()) {
                noView.setError("Error connecting to the pool");
            } else {
                noView.setText(info.poolNumberStr());
                if (info.pools() != null) {
                    for (String p : info.pools()) {
                        if (adapter.getPosition(p) < 0) adapter.add(p);
                    }
                }
            }
            noView.invalidate();
            adapter.notifyDataSetChanged();
        }
    }

    private final ListView poolView;
    private final TextView noView;
    private final ArrayAdapter<String> adapter;
}
