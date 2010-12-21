// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;

import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServers;

/**
 *
 * Created: Tue Dec 21 12:49:41 2010
 *
 * @author jao
 */
final class Serializer {

    static List<ServerInfo> readServers(SharedPreferences prefs) {
        final List<ServerInfo> result = new ArrayList<ServerInfo>();
        final int no = prefs.getInt(SRV_NO_K, 0);
        for (int i = 0; i < no; ++i) {
            final String uri = prefs.getString(serverUriKey(i), null);
            final String name = prefs.getString(serverNameKey(i), null);
            final String subt = prefs.getString(serverSubtypeKey(i), null);
            if (uri != null && name != null && subt != null) {
                final PoolServer s = PoolServers.get(uri, name, subt);
                if (s != null) result.add(new ServerInfo(s, name, true));
            }
        }
        return result;
    }

    static void saveServers(SharedPreferences prefs, List<ServerInfo> svr) {
        SharedPreferences.Editor ed = prefs.edit();
        final int oldSize = prefs.getInt(SRV_NO_K, 0);
        final int newSize = svr.size();
        ed.putInt(SRV_NO_K, newSize);
        for (int i = 0; i < newSize; ++i) {
            final ServerInfo info = svr.get(i);
            ed.putString(serverUriKey(i), info.server().address().toString());
            ed.putString(serverNameKey(i), info.name());
            ed.putString(serverSubtypeKey(i), info.server().subtype());
        }
        for (int i = newSize; i < oldSize; ++i) {
            ed.remove(serverUriKey(i));
            ed.remove(serverNameKey(i));
            ed.remove(serverSubtypeKey(i));
        }
        ed.commit();
    }


    private static String serverUriKey(int n) {
        return String.format(SRV_URI_FMT, n);
    }

    private static String serverNameKey(int n) {
        return String.format(SRV_NAME_FMT, n);
    }

    private static String serverSubtypeKey(int n) {
        return String.format(SRV_SUBT_FMT, n);
    }

    private static final String SRV_NO_K = "servers.no";
    private static final String SRV_URI_FMT = "servers.%d.uri";
    private static final String SRV_NAME_FMT = "servers.%d.name";
    private static final String SRV_SUBT_FMT = "servers.%d.subtype";

    private Serializer() {}
}
