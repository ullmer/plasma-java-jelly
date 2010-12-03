// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import java.util.Set;

import android.os.Handler;
import android.os.Message;
import android.view.View;

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

    void updatePools(final Handler hdl, final int msg) {
        new Thread (new Runnable () {
                @Override public void run() {
                    info.updatePools();
                    if (info.poolNumber() > 0) {
                        for (String p : info.pools())
                            hdl.sendMessage(Message.obtain(hdl, msg, p));
                    }
                }
            }).start();
    }

    ServerInfo info() { return info; }

    String name() { return info.name(); }
    Set<String> pools() { return info.pools(); }
    int poolNumber() { return info.poolNumber(); }
    PoolServer server() { return info.server(); }
    boolean connectionError() { return info.connectionError(); }
    void clearPools() { info.clearPools(); }
    void nextName(int n) { info.nextName(n); }

    View view() { return view; }
    void view(View v) { view = v; }


    private final ServerInfo info;
    private volatile View view;
}

