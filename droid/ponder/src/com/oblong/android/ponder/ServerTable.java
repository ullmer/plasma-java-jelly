// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import android.app.ListActivity;
import android.os.Handler;
import android.os.Message;

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

    ServerTable(ListActivity la) {
        servers = new PoolServerCache();
        adapter = new ServerListAdapter(la);
        la.setListAdapter(adapter);
        setupListener();
    }

    void rescan() {
        new Thread (new Runnable () {
                 public void run() {
                    for (PoolServer s : PoolServers.remoteServers())
                        if (!servers.contains(s)) notifyNewServer(s);
                }
            }).start();
    }

    void reset() {
        for (ServerInfo i : adapter) i.clearPools();
        adapter.notifyDataSetChanged();
        new Thread (new Runnable () {
                 public void run() {
                    TCPServerFactory.reset();
                    for (PoolServer s : PoolServers.remoteServers()) {
                        notifyNewServer(s);
                    }
                    for (ServerInfo i : adapter) updatePoolNumber(i);
                    setupListener();
                }
            }).start();
    }

    void deleteUnreachable() {
        for (ServerInfo i : adapter)
            if (i.connectionError()) removeServer(i, true);
    }

    void registerServer(ServerInfo info) {
        int k = 1;
        info.clearPools();
        adapter.insert(info, 0);
        adapter.notifyDataSetChanged();
        checkInfo(info);
    }

    List<ServerInfo> registeredServers() {
        final List<ServerInfo> r = new ArrayList<ServerInfo>();
        for (ServerInfo i : adapter) if (i.userDefined()) r.add(i);
        return r;
    }

    void refreshServer(int position) {
        final ServerInfo info = adapter.getItem(position);
        if (info != null) {
            info.clearPools();
            adapter.notifyDataSetChanged();
            updatePoolNumber(info);
        }
    }

    void removeServer(int position, boolean force) {
        removeServer(adapter.getItem(position), force);
    }

    ServerInfo getItem(int position) {
        return adapter.getItem(position);
    }

    private static boolean isGeneric(PoolServer s) {
        return s.subtype().length() == 0;
    }

    private void removeServer(ServerInfo info, boolean force) {
        if (info != null) {
            servers.remove(info.server());
            if (force || !info.userDefined()) {
                adapter.remove(info);
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void updatePoolNumber(ServerInfo info) {
        info.clearPools();
        info.updatePoolNumber(handler, UPD_MSG);
    }

    private void sendMessage(int msg, Object arg) {
        handler.sendMessage(Message.obtain(handler, msg, arg));
    }

    private void notifyNewServer(PoolServer server) {
        sendMessage(ADD_MSG, server);
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

    private void checkInfo(ServerInfo info) {
        if (info != null) {
            info.clearPools();
            adapter.notifyDataSetChanged();
            info.updatePoolNumber(handler, CHK_MSG);
            if (!isGeneric(info.server())) {
                final PoolServer gs = servers.get(info.server().name(), "");
                removeServer(adapter.getItem(gs), false);
            }
        }
    }

    private void addServer(PoolServer server) {
        if (!servers.contains(server)) {
            final boolean c =
                servers.contains(server.address(), server.name(), null);
            servers.add(server);
            if (!(isGeneric(server) && c)) {
                final ServerInfo info = new ServerInfo(server);
                adapter.add(info);
                checkInfo(info);
            }
        }
    }

    private void removeServer(PoolServer server) {
        checkInfo(adapter.getItem(server));
    }

    private void onCheckServer(ServerInfo info) {
        if (info.connectionError() && !info.userDefined()) {
            for (PoolServer s :
                     servers.get(null, info.server().name(), null)) {
                ServerInfo i = adapter.getItem(s);
                if (i != null && i.connectionError()) removeServer(i, false);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void updateServer(ServerInfo info) {
        adapter.notifyDataSetChanged();
    }

    private static final int ADD_MSG = 0;
    private static final int DEL_MSG = 2;
    private static final int UPD_MSG = 4;
    private static final int CHK_MSG = 6;

    private final Handler handler = new Handler () {
        public void handleMessage(Message m) {
            switch (m.what) {
            case ADD_MSG: addServer((PoolServer)m.obj); break;
            case DEL_MSG: removeServer((PoolServer)m.obj); break;
            case UPD_MSG: updateServer((ServerInfo)m.obj); break;
            case CHK_MSG: onCheckServer((ServerInfo)m.obj); break;
            default:
                Ponder.logger().warning(
                    "Unexpected message to ServerTable: " + m.what);
                break;
            }
        }
    };

    private final PoolServerCache servers;
    private final ServerListAdapter adapter;
}
