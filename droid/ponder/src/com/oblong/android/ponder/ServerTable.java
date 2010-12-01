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

    private final static class RowInfo {
        final PoolServer server;
        int poolNumber;
        View view;

        RowInfo(PoolServer s) {
            server = s;
            poolNumber = -1;
            view = null;
        }

        boolean updatePoolNumber() {
            try {
                poolNumber = server.pools().size();
            } catch (Exception e) {
                poolNumber = -1;
                return false;
            }
            return true;
        }

        String name() {
            return server.name() + " (" + server.address().host() + ")";
        }

        String pools() {
            return poolNumber < 0
                ? ""
                : poolNumber + " pool" + (poolNumber == 1 ? "" : "s");
        }

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
            ((TextView)v.findViewById(R.id.pool_count)).setText(i.pools());
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
                    case 0: addServer((RowInfo)m.obj); break;
                    case 1: delServer((PoolServer)m.obj); break;
                    case 2: updateServer((RowInfo)m.obj); break;
                    }
                }
            };
        PoolServers.addRemoteListener(new PoolServers.Listener() {
                public void serverAdded(PoolServer s) {
                    final RowInfo info = new RowInfo(s);
                    handler.sendMessage(Message.obtain(handler, 0, info));
                    if (info.updatePoolNumber())
                        handler.sendMessage(Message.obtain(handler, 2, info));
                }
                public void serverRemoved(PoolServer s) {
                    handler.sendMessage(Message.obtain(handler, 1, s));
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
