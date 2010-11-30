// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;

import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServers;

/**
 *
 * Created: Mon Nov 29 16:38:43 2010
 *
 * @author jao
 */
final class ServerTable {

    final static class RowInfo {
        PoolServer server;

        RowInfo(PoolServer s) {
            server = s;
        }

        @Override public String toString() {
            return server.address().host() + " (" + server.name() + ")";
        }
    }

    ServerTable(WifiManager wifi, ArrayAdapter<RowInfo> adapter) {
        infos = new ConcurrentHashMap<String, RowInfo>();
        rows = adapter;
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

    private MulticastLock setupMulticastLock () {
        System.setProperty("net.mdns.interface",
                           getAddress(wifiMngr.getDhcpInfo().ipAddress));
        MulticastLock lk = wifiMngr.createMulticastLock("_ponder-lock");
        lk.setReferenceCounted(true);
        lk.acquire();
        return lk;
    }

    private void setupListener() {
        final Handler handler = new Handler () {
                public void handleMessage(Message m) {
                    if (m.what == 0) addServer((PoolServer)m.obj);
                    else delServer((PoolServer)m.obj);
                }
            };
        PoolServers.addRemoteListener(new PoolServers.Listener() {
                public void serverAdded(PoolServer s) {
                    handler.sendMessage(Message.obtain(handler, 0, s));
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

    private void addServer(PoolServer s) {
        if (infos.get(s.name()) == null) {
            final RowInfo info = new RowInfo(s);
            infos.put(s.name(), info);
            rows.add(info);
            rows.notifyDataSetChanged();
        }
    }

    private static String getAddress(int addr) {
        StringBuffer buf = new StringBuffer();
        buf.append(addr  & 0xff).append('.').
            append((addr >>>= 8) & 0xff).append('.').
            append((addr >>>= 8) & 0xff).append('.').
            append((addr >>>= 8) & 0xff);
        return buf.toString();
    }

    private final ConcurrentHashMap<String, RowInfo> infos;
    private final ArrayAdapter<RowInfo> rows;
    private final MulticastLock mcLock;
    private final WifiManager wifiMngr;
}
