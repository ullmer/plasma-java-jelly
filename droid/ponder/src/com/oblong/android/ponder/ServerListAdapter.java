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

final class ServerInfoRow {

    ServerInfoRow(PoolServer s) {
        this(s, s.name());
    }

    ServerInfoRow(PoolServer s, String n) {
        info = new ServerInfo(s, n);
        view = null;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof ServerInfoRow)) return false;
        return info.equals(((ServerInfoRow)o).info);
    }

    ServerInfo info() { return info; }

    View view() { return view; }
    void view(View v) { view = v; }

    void updatePoolNumber(final Handler hdl, final int msg) {
        new Thread (new Runnable () {
                @Override public void run() {
                    info.updatePools();
                    hdl.sendMessage(Message.obtain(hdl,
                                                   msg,
                                                   ServerInfoRow.this));
                }
            }).start();
    }

    private final ServerInfo info;
    private volatile View view;
}

final class ServerListAdapter extends ArrayAdapter<ServerInfoRow> {

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

    static void fillView(View v, ServerInfoRow i) {
        final String host =
            i.info().name() + " (" + i.info().server().address().host() + ")";
        final TextView hv = (TextView)v.findViewById(R.id.server_host);
        hv.setText(host);
        final TextView pcv = (TextView)v.findViewById(R.id.pool_count);
        if (i.info().connectionError()) {
            pcv.setError("Error connecting to the pool");
        } else {
            pcv.setText(i.info().poolNumberStr());
        }
        i.view(v);
    }
}
