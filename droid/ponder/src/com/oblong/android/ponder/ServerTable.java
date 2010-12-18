// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import android.app.ListActivity;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;

import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServers;
import com.oblong.jelly.pool.PoolServerCache;
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
        servers = new PoolServerCache();
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
        new Thread (new Runnable () {
                @Override public void run() {
                    TCPServerFactory.reset();
                    for (PoolServer s : PoolServers.remoteServers()) {
                        notifyNewServer(s);
                    }
                    for (ServerInfoRow i : infos.values())
                        updatePoolNumber(i);
                    setupListener();
                }
            }).start();
    }

    void deleteUnreachable() {
        for (ServerInfoRow i : infos.values())
            if (i.info().connectionError()) delServer(i);
    }

    void registerServer(ServerInfoRow row) {
        int k = 1;
        while (infos.get(row.info().name()) != null)
            row.info().nextName(++k);
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
            infos.remove(row.key());
            localInfos.remove(row);
            servers.remove(row.info().server());
            adapter.remove(row);
            adapter.notifyDataSetChanged();
        }
    }

    ServerInfoRow getItem(int position) {
        return adapter.getItem(position);
    }

    private MulticastLock setupMulticastLock () {
        final int address = wifiMngr.getDhcpInfo().ipAddress;
        System.setProperty("net.mdns.interface",
                           Formatter.formatIpAddress(address));
        MulticastLock lk = wifiMngr.createMulticastLock("_ponder-lock");
        lk.setReferenceCounted(false);
        lk.acquire();
        return lk;
    }

    private void updatePoolNumber(ServerInfoRow row) {
        row.info().clearPools();
        row.updatePoolNumber(handler, UPD_MSG);
    }

    private void sendMessage(int msg, Object arg) {
        Ponder.logger().info("Sending message " + msg);
        Ponder.logger().info(" ... with arg: " + arg);
        handler.sendMessage(Message.obtain(handler, msg, arg));
    }

    private void notifyNewServer(PoolServer server) {
        final ServerInfoRow row = new ServerInfoRow(server);
        sendMessage(ADD_MSG, row);
    }

    private void notifyGoneServer(PoolServer server) {
        sendMessage(DEL_MSG, server);
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

    private void checkRow(ServerInfoRow row) {
        if (row != null) {
            row.info().clearPools();
            adapter.notifyDataSetChanged();
            row.updatePoolNumber(handler, CHK_MSG);
        }
    }

    private void addServer(ServerInfoRow row) {
        if (infos.get(row.key()) == null) {
            infos.put(row.key(), row);
            servers.add(row.info().server());
            adapter.add(row);
            checkRow(row);
        }
    }

    private void delServer(PoolServer server) {
        checkRow(infos.get(server.qualifiedName()));
    }

    private void onCheckServer(ServerInfoRow row) {
        if (row.info().connectionError()) {
            for (PoolServer s :
                     servers.get(null, row.info().server().name(), null)) {
                ServerInfoRow r = infos.get(s.qualifiedName());
                if (r != null && r.info().connectionError()) {
                    servers.remove(s);
                    adapter.remove(infos.remove(s.qualifiedName()));
                }
            }
            adapter.notifyDataSetChanged();
        } else {
            updateServer(row);
        }
    }

    private void updateServer(ServerInfoRow info) {
        if (info.view() != null) {
            ServerListAdapter.fillView(info.view(), info);
        }
        adapter.notifyDataSetChanged();
    }

    private static final int ADD_MSG = 0;
    private static final int DEL_MSG = 2;
    private static final int UPD_MSG = 4;
    private static final int CHK_MSG = 6;

    private final Handler handler = new Handler () {
        public void handleMessage(Message m) {
            Ponder.logger().info("Received message no. " + m.what);
            switch (m.what) {
            case ADD_MSG: addServer((ServerInfoRow)m.obj); break;
            case DEL_MSG: delServer((PoolServer)m.obj); break;
            case UPD_MSG: updateServer((ServerInfoRow)m.obj); break;
            case CHK_MSG: onCheckServer((ServerInfoRow)m.obj); break;
            default:
                Ponder.logger().warning(
                    "Unexpected message to ServerTable: " + m.what);
                break;
            }
        }
    };

    private final ConcurrentHashMap<String, ServerInfoRow> infos;
    private final Set<ServerInfoRow> localInfos;
    private final PoolServerCache servers;
    private final ServerListAdapter adapter;
    private final MulticastLock mcLock;
    private final WifiManager wifiMngr;
}
