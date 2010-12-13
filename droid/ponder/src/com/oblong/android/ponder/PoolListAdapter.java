// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import android.app.ListActivity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.oblong.jelly.PoolAddress;
import com.oblong.jelly.PoolServerAddress;

final class PoolListRow {
    String name;
    PoolAddress address;

    PoolListRow(String n, PoolAddress a) {
        name = n;
        address = a;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof PoolListRow)) return false;
        return address.equals(((PoolListRow)o).address);
    }
}

final class PoolListAdapter extends ArrayAdapter<PoolListRow> {

    PoolListAdapter(Context context) {
        super(context, R.layout.pool_item, R.id.pool_name);
    }

    void add(PoolServerAddress addr, String name) {
        try {
            final PoolListRow r =
                new PoolListRow(name, new PoolAddress(addr, name));
            if (getPosition(r) < 0) add(r);
        } catch (Exception e) {
            Ponder.logger().severe("Error creating address: " + e);
        }
    }

    @Override public View getView(int n, View v, ViewGroup g) {
        if (v == null || v.getId() != R.layout.pool_item) {
            final Context c = g.getContext();
            final LayoutInflater i = (LayoutInflater)
                c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = i.inflate(R.layout.pool_item, null);
        }
        fillView(v, getItem(n));
        return v;
    }

    static void fillView(View v, PoolListRow r) {
        final TextView name = (TextView)v.findViewById(R.id.pool_name);
        name.setText(r.name);
        final PoolInfo i = PoolInfo.tryGet(r.address);
        if (i != null) {
            final TextView pv = (TextView)v.findViewById(R.id.protein_no);
            pv.setText(String.format("%6d p", i.cursor().getCount()));
            final String s = Utils.formatSize(i.metadata().size());
            final String us = Utils.formatSize(i.metadata().usedSize());
            final TextView sv = (TextView)v.findViewById(R.id.pool_size);
            sv.setText(String.format("%s/%s", us, s));
        }
    }

}
