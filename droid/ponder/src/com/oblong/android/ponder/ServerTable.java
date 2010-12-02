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
        infos = new ConcurrentHashMap<String, RowInfo>();
        localInfos = new HashSet<RowInfo>();
        adapter = new ServerListAdapter(la);
        la.setListAdapter(adapter);
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
        infos.clear();
        adapter.clear();
        final Thread th = new Thread (new Runnable () {
                @Override public void run() {
                    TCPServerFactory.reset();
                    for (RowInfo i : localInfos) notifyNewServer(i);
                    for (PoolServer s : PoolServers.remoteServers())
                        notifyNewServer(s);
                    setupListener();
                }
            });
        th.start();
    }

    void registerServer(RowInfo info) {
        int k = 1;
        while (infos.get(info.name()) != null) info.nextName(++k);
        info.clearPools();
        localInfos.add(info);
        addServer(info);
        updatePoolNumber(info);
    }

    void refreshServer(int position) {
        final RowInfo info = adapter.getItem(position);
        if (info != null) {
            info.clearPools();
            adapter.notifyDataSetChanged();
            updatePoolNumber(info);
        }
    }

    void delServer(int position) {
        final RowInfo info = adapter.getItem(position);
        if (info != null) {
            infos.remove(info.name());
            localInfos.remove(info);
            adapter.remove(info);
            adapter.notifyDataSetChanged();
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

    private void updatePoolNumber(RowInfo i) {
        i.updatePoolNumber(handler, UPD_MSG);
    }

    private void notifyNewServer(PoolServer server) {
        notifyNewServer(new RowInfo(server));
    }

    private void notifyNewServer(RowInfo info) {
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

    private void addServer(RowInfo info) {
        if (infos.get(info.name()) == null) {
            infos.put(info.name(), info);
            adapter.add(info);
            adapter.notifyDataSetChanged();
        }
    }

    private void delServer(PoolServer s) {
        final RowInfo info = infos.remove(s.name());
        if (info != null) {
            adapter.remove(info);
            adapter.notifyDataSetChanged();
        }
    }

    private void updateServer(RowInfo info) {
        if (info.view() != null) {
            ServerListAdapter.fillView(info.view(), info);
            adapter.notifyDataSetChanged();
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

    private static final int ADD_MSG = 0;
    private static final int DEL_MSG = 1;
    private static final int UPD_MSG = 2;

    private final Handler handler = new Handler () {
        public void handleMessage(Message m) {
            switch (m.what) {
            case ADD_MSG: addServer((RowInfo)m.obj); break;
            case DEL_MSG: delServer((PoolServer)m.obj); break;
            case UPD_MSG: updateServer((RowInfo)m.obj); break;
            }
        }
    };

    private final ConcurrentHashMap<String, RowInfo> infos;
    private final Set<RowInfo> localInfos;
    private final ServerListAdapter adapter;
    private final MulticastLock mcLock;
    private final WifiManager wifiMngr;
}
