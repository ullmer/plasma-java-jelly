// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import android.app.ListActivity;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Handler;
import android.os.Message;

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
        infos = new ConcurrentHashMap<String, ServerInfoRow>();
        localInfos = new HashSet<ServerInfoRow>();
        adapter = new ServerListAdapter(la);
        la.setListAdapter(adapter);
        wifiMngr = wifi;
        mcLock = setupMulticastLock();
        setupListener();
    }

    void activate() {
        mcLock.acquire();
        rescan();
    }

    void deactivate() {
        mcLock.release();
    }

    void rescan() {
        final Thread th = new Thread (new Runnable () {
                @Override public void run() {
                    for (PoolServer s : PoolServers.remoteServers())
                        notifyNewServer(s);
                }
            });
        th.start();
    }

    void reset() {
        for (ServerInfoRow i : infos.values()) i.info().clearPools();
        adapter.notifyDataSetChanged();
        final Thread th = new Thread (new Runnable () {
                @Override public void run() {
                    TCPServerFactory.reset();
                    for (PoolServer s : PoolServers.remoteServers()) {
                        notifyNewServer(s);
                    }
                    for (ServerInfoRow i : infos.values())
                        updatePoolNumber(i);
                    setupListener();
                }
            });
        th.start();
    }

    void deleteUnreachable() {
        for (ServerInfoRow i : infos.values())
            if (i.info().connectionError()) delServer(i);
    }

    void registerServer(ServerInfoRow row) {
        int k = 1;
        while (infos.get(row.info().name()) != null) row.info().nextName(++k);
        row.info().clearPools();
        localInfos.add(row);
        addServer(row);
        updatePoolNumber(row);
    }

    void refreshServer(int position) {
        final ServerInfoRow row = adapter.getItem(position);
        if (row != null) {
            row.info().clearPools();
            adapter.notifyDataSetChanged();
            updatePoolNumber(row);
        }
    }

    void delServer(int position) {
        delServer(adapter.getItem(position));
    }

    void delServer(ServerInfoRow row) {
        if (row != null) {
            infos.remove(row.info().name());
            localInfos.remove(row);
            adapter.remove(row);
            adapter.notifyDataSetChanged();
        }
    }

    ServerInfoRow getItem(int position) {
        return adapter.getItem(position);
    }

    private MulticastLock setupMulticastLock () {
        final int address = wifiMngr.getDhcpInfo().ipAddress;
        System.setProperty("net.mdns.interface", getHost(address));
        MulticastLock lk = wifiMngr.createMulticastLock("_ponder-lock");
        lk.setReferenceCounted(false);
        lk.acquire();
        return lk;
    }

    private void updatePoolNumber(ServerInfoRow i) {
        i.updatePoolNumber(handler, UPD_MSG);
    }

    private ServerInfoRow notifyNewServer(PoolServer server) {
        final ServerInfoRow info = new ServerInfoRow(server);
        notifyNewServer(info);
        return info;
    }

    private void notifyNewServer(ServerInfoRow info) {
        handler.sendMessage(Message.obtain(handler, ADD_MSG, info));
        updatePoolNumber(info);
    }

    private void notifyGoneServer(PoolServer server) {
        handler.sendMessage(Message.obtain(handler, DEL_MSG, server));
    }

    private void setupListener() {
        PoolServers.addRemoteListener(new PoolServers.Listener() {
                public void serverAdded(PoolServer s) {
                    notifyNewServer(s);
                }
                public void serverRemoved(PoolServer s) {
                    notifyGoneServer(s);
                }
            });
    }

    private void addServer(ServerInfoRow row) {
        if (infos.get(row.info().name()) == null) {
            infos.put(row.info().name(), row);
            adapter.add(row);
            adapter.notifyDataSetChanged();
        }
    }

    private void delServer(PoolServer s) {
        final ServerInfoRow info = infos.remove(s.name());
        if (info != null) {
            adapter.remove(info);
            adapter.notifyDataSetChanged();
        }
    }

    private void updateServer(ServerInfoRow info) {
        if (info.view() != null) {
            ServerListAdapter.fillView(info.view(), info);
        }
        adapter.notifyDataSetChanged();
    }

    private static String getHost(int addr) {
        StringBuffer buf = new StringBuffer();
        buf.append(addr  & 0xff).append('.').
            append((addr >>>= 8) & 0xff).append('.').
            append((addr >>>= 8) & 0xff).append('.').
            append((addr >>>= 8) & 0xff);
        return buf.toString();
    }

    private static final int ADD_MSG = 0;
    private static final int DEL_MSG = 1;
    private static final int UPD_MSG = 2;

    private final Handler handler = new Handler () {
        public void handleMessage(Message m) {
            switch (m.what) {
            case ADD_MSG: addServer((ServerInfoRow)m.obj); break;
            case DEL_MSG: delServer((PoolServer)m.obj); break;
            case UPD_MSG: updateServer((ServerInfoRow)m.obj); break;
            }
        }
    };

    private final ConcurrentHashMap<String, ServerInfoRow> infos;
    private final Set<ServerInfoRow> localInfos;
    private final ServerListAdapter adapter;
    private final MulticastLock mcLock;
    private final WifiManager wifiMngr;
}
