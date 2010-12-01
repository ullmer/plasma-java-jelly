// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import android.app.ListActivity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServers;
import com.oblong.jelly.pool.net.TCPServerFactory;

/**
 *
 * Created: Mon Nov 29 16:38:43 2010
 *
 * @author jao
 */
final class ServerTable {

    ServerTable(ListActivity la, WifiManager wifi) {
        infos = new ConcurrentHashMap<String, RowInfo>();
        rows = new ServerAdapter(la);
        la.setListAdapter(rows);
        wifiMngr = wifi;
        mcLock = setupMulticastLock();
        setupListener();
    }

    void activate() {
        if (!mcLock.isHeld()) mcLock.acquire();
    }

    void deactivate() {
        if (mcLock.isHeld()) mcLock.release();
    }

    void reset() {
        TCPServerFactory.reset();
        for (PoolServer s : PoolServers.remoteServers())
            addServer(new RowInfo(s));
        rows.notifyDataSetChanged();
        setupListener();
    }

    static final int ADD_MSG = 0;
    static final int DEL_MSG = 1;
    static final int UPD_MSG = 2;

    private final static class RowInfo {
        final PoolServer server;
        volatile int poolNumber;
        volatile View view;

        RowInfo(PoolServer s) {
            server = s;
            poolNumber = UNINITIALIZED;
            view = null;
        }

        void updatePoolNumber(final Handler hdl) {
            Thread th = new Thread (new Runnable () {
                    @Override public void run() {
                        try {
                            poolNumber = server.pools().size();
                        } catch (Exception e) {
                            poolNumber = CON_ERR;
                        }
                        hdl.sendMessage(
                            Message.obtain(hdl, UPD_MSG, RowInfo.this));
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

    private final static class ServerAdapter extends ArrayAdapter<RowInfo> {
        ServerAdapter(ListActivity parent) {
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

    private MulticastLock setupMulticastLock () {
        final int address = wifiMngr.getDhcpInfo().ipAddress;
        System.setProperty("net.mdns.interface", getHost(address));
        MulticastLock lk = wifiMngr.createMulticastLock("_ponder-lock");
        lk.setReferenceCounted(true);
        lk.acquire();
        return lk;
    }

    private void setupListener() {
        final Handler handler = new Handler () {
                public void handleMessage(Message m) {
                    switch (m.what) {
                    case ADD_MSG: addServer((RowInfo)m.obj); break;
                    case DEL_MSG: delServer((PoolServer)m.obj); break;
                    case UPD_MSG: updateServer((RowInfo)m.obj); break;
                    }
                }
            };
        PoolServers.addRemoteListener(new PoolServers.Listener() {
                public void serverAdded(PoolServer s) {
                    final RowInfo info = new RowInfo(s);
                    handler.sendMessage(
                        Message.obtain(handler, ADD_MSG, info));
                    info.updatePoolNumber(handler);
                }
                public void serverRemoved(PoolServer s) {
                    handler.sendMessage(Message.obtain(handler, DEL_MSG, s));
                }
            });
    }

    private void delServer(PoolServer s) {
        final RowInfo info = infos.remove(s.name());
        if (info != null) {
            rows.remove(info);
            rows.notifyDataSetChanged();
        }
    }

    private void addServer(RowInfo info) {
        if (infos.get(info.server.name()) == null) {
            infos.put(info.server.name(), info);
            rows.add(info);
            rows.notifyDataSetChanged();
        }
    }

    private void updateServer(RowInfo info) {
        if (info.view != null) {
            ServerAdapter.fillView(info.view, info);
            rows.notifyDataSetChanged();
        }
    }

    private static String getHost(int addr) {
        StringBuffer buf = new StringBuffer();
        buf.append(addr  & 0xff).append('.').
            append((addr >>>= 8) & 0xff).append('.').
            append((addr >>>= 8) & 0xff).append('.').
            append((addr >>>= 8) & 0xff);
        return buf.toString();
    }

    private final ConcurrentHashMap<String, RowInfo> infos;
    private final ServerAdapter rows;
    private final MulticastLock mcLock;
    private final WifiManager wifiMngr;
}
