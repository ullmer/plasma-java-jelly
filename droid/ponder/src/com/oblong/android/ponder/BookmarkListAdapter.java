// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import android.app.ListActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

final class Bookmark {
    ServerInfo info;
    String pool;

    Bookmark(ServerInfo i, String p) {
        info = i;
        pool = p;
    }

    boolean isPool() { return pool != null; }

    @Override public boolean equals(Object o) {
        if (!(o instanceof Bookmark)) return false;
        final Bookmark b = (Bookmark)o;
        if (pool == null && b.pool != null) return false;
        return (pool == null || pool.equals(b.pool)) && info.equals(b.info);
    }
}

final class BookmarkListAdapter extends ArrayAdapter<Bookmark> {

    @Override public View getView(int n, View v, ViewGroup g) {
        if (v == null || v.getId() != R.layout.bookmark) {
            final Context c = g.getContext();
            final LayoutInflater i = (LayoutInflater)
                c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = i.inflate(R.layout.bookmark, null);
        }
        fillView(v, getItem(n));
        return v;
    }

    BookmarkListAdapter(ListActivity parent) {
        super(parent, R.layout.bookmark, R.id.bmk_name);
    }


    private static void fillView(View v, Bookmark bmk) {
        final String name = bmk.isPool() ? bmk.pool : bmk.info.name();
        final String prefix = bmk.isPool() ? bmk.info.name() + " - " : "";
        final String details =
            String.format("%s%s:%d",
                          prefix,
                          bmk.info.server().address().host(),
                          bmk.info.server().address().port());
        ((TextView)v.findViewById(R.id.bmk_name)).setText(name);
        ((TextView)v.findViewById(R.id.bmk_details)).setText(details);
    }
}
