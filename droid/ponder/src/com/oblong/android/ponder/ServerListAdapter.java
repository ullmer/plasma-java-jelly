// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import android.app.ListActivity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.oblong.jelly.PoolServer;

final class ServerListAdapter extends ArrayAdapter<RowInfo> {
    ServerListAdapter(ListActivity parent) {
        super(parent, R.layout.server_item, R.id.server_host);
    }

    @Override public View getView(int n, View v, ViewGroup g) {
        if (v == null || v.getId() != R.layout.server_item) {
            final Context c = g.getContext();
            final LayoutInflater i = (LayoutInflater)
                c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = i.inflate(R.layout.server_item, null);
        }
        fillView(v, getItem(n));
        return v;
    }

    static void fillView(View v, RowInfo i) {
        ((TextView)v.findViewById(R.id.server_host)).setText(i.name());
        final TextView pcv = (TextView)v.findViewById(R.id.pool_count);
        if (i.connectionError())
            pcv.setError("Error connecting to the pool");
        else
            pcv.setText(i.pools());
        i.view = v;
    }
}

final class RowInfo {
    final PoolServer server;
    volatile int poolNumber;
    volatile View view;

    RowInfo(PoolServer s) {
        server = s;
        poolNumber = UNINITIALIZED;
        view = null;
    }

    void updatePoolNumber(final Handler hdl, final int msg) {
        Thread th = new Thread (new Runnable () {
                @Override public void run() {
                    try {
                        poolNumber = server.pools().size();
                    } catch (Exception e) {
                        poolNumber = CON_ERR;
                    }
                    hdl.sendMessage(
                        Message.obtain(hdl, msg, RowInfo.this));
                }
            });
        th.start();
    }

    String name() {
        return server.name() + " (" + server.address().host() + ")";
    }

    String pools() {
        return poolNumber < 0
            ? ""
            : poolNumber + " pool" + (poolNumber == 1 ? "" : "s");
    }

    boolean connectionError() {
        return poolNumber == CON_ERR;
    }

    private static final int CON_ERR = -2;
    private static final int UNINITIALIZED = -1;
}

