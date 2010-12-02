// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import java.util.logging.Logger;

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
        final String host =
            i.name() + " (" + i.server().address().host() + ")";
        final TextView hv = (TextView)v.findViewById(R.id.server_host);
        hv.setText(host);
        final TextView pcv = (TextView)v.findViewById(R.id.pool_count);
        if (i.connectionError()) {
            pcv.setError("Error connecting to the pool");
        } else {
            final int pn = i.poolNumber();
            String txt;
            switch (pn) {
            case RowInfo.UNINITIALIZED: txt = "scanning"; break;
            case 1: txt = "1 pool"; break;
            default: txt = pn + " pools"; break;
            }
            pcv.setText(txt);
        }
        i.view(v);
    }
}

final class RowInfo {

    RowInfo(PoolServer s) {
        this(s, s.name());
    }

    RowInfo(PoolServer s, String n) {
        server = s;
        name = n;
        poolNumber = UNINITIALIZED;
        view = null;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof RowInfo)) return false;
        return name.equals(((RowInfo)o).name());
    }

    void updatePoolNumber(final Handler hdl, final int msg) {
        final Thread th = new Thread (new Runnable () {
                @Override public void run() {
                    try {
                        poolNumber = server.pools().size();
                    } catch (Exception e) {
                        Logger.getLogger("Ponder").info(
                            "Error connection to server: " + e.getMessage());
                        poolNumber = CON_ERR;
                    }
                    hdl.sendMessage(
                        Message.obtain(hdl, msg, RowInfo.this));
                }
            });
        th.start();
    }

    static final int CON_ERR = -2;
    static final int UNINITIALIZED = -1;

    String name() { return name; }
    int poolNumber() { return poolNumber; }
    View view() { return view; }
    void view(View v) { view = v; }
    PoolServer server() { return server; }
    boolean connectionError() { return poolNumber == CON_ERR; }
    void clearPools() { poolNumber = UNINITIALIZED; }
    void nextName(int n) { name = name + " #" + n; }

    private final PoolServer server;
    private volatile int poolNumber;
    private volatile View view;
    private volatile String name;
}

