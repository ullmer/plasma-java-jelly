// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

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
        rows = new ServerListAdapter(la);
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

    private MulticastLock setupMulticastLock () {
        final int address = wifiMngr.getDhcpInfo().ipAddress;
        System.setProperty("net.mdns.interface", getHost(address));
        MulticastLock lk = wifiMngr.createMulticastLock("_ponder-lock");
        lk.setReferenceCounted(true);
        lk.acquire();
        return lk;
    }

    private void setupListener() {
        final int ADD_MSG = 0;
        final int DEL_MSG = 1;
        final int UPD_MSG = 2;

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
                    info.updatePoolNumber(handler, UPD_MSG);
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
            ServerListAdapter.fillView(info.view, info);
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
    private final ServerListAdapter rows;
    private final MulticastLock mcLock;
    private final WifiManager wifiMngr;
}
