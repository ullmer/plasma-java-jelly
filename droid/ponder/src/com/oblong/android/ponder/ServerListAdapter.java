// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import java.util.Iterator;
import java.util.NoSuchElementException;
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

final class ServerListAdapter extends ArrayAdapter<ServerInfo>
    implements Iterable<ServerInfo> {

    @Override public Iterator<ServerInfo> iterator() {
        class SIterator implements Iterator<ServerInfo> {
            public SIterator(ServerListAdapter a) { adapter = a; }
            public boolean hasNext() { return n < adapter.getCount(); }
            public ServerInfo next() {
                if (n >= adapter.getCount())
                    throw new NoSuchElementException();
                return adapter.getItem(n++);
            }
            public void remove() {
                adapter.remove(adapter.getItem(n));
            }
            private int n = 0;
            private final ServerListAdapter adapter;
        };
        return new SIterator(this);
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

    ServerListAdapter(ListActivity parent) {
        super(parent, R.layout.server_item, R.id.server_name);
    }

    boolean contains(ServerInfo i) { return getPosition(i) >= 0; }

    ServerInfo getItem(PoolServer s) {
        for (int i = 0, c = getCount(); i < c; ++i)
            if (getItem(i).server().equals(s)) return getItem(i);
        return null;
    }

    static void fillView(View v, ServerInfo i) {
        final String st = i.server().subtype();
        final String name = String.format("%s (%s)", i.name(),
                                          st.length() == 0 ? "generic" : st);
        ((TextView)v.findViewById(R.id.server_name)).setText(name);
        final TextView pcv = (TextView)v.findViewById(R.id.pool_count);
        if (i.connectionError()) {
            pcv.setError("Error connecting to the pool");
        } else {
            pcv.setText(i.poolNumberStr());
        }
    }
}
