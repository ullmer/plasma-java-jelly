// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServers;

/**
 *
 * Created: Mon Nov 29 16:38:43 2010
 *
 * @author jao
 */
final class ServerTable {

    ServerTable(WifiManager wifi, TableLayout layout) {
        rows = new ConcurrentHashMap<String, RowInfo>();
        table = layout;
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
        final RowInfo info = rows.remove(s.name());
        if (info != null) {
            table.removeView(info.view);
            table.invalidate();
        }
    }

    private void addServer(PoolServer s) {
        if (rows.get(s.name()) == null) {
            final RowInfo info = new RowInfo(table, s);
            rows.put(s.name(), info);
            table.invalidate();
        }
    }

    private static class RowInfo {
        View view;
        PoolServer server;

        RowInfo(TableLayout table, PoolServer s) {
            server = s;
            final TextView txt = new TextView (table.getContext());
            txt.setText(s.address().toString());
            table.addView(txt);
            view = txt;
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

    private final ConcurrentHashMap<String, RowInfo> rows;
    private final TableLayout table;
    private final MulticastLock mcLock;
    private final WifiManager wifiMngr;
}
