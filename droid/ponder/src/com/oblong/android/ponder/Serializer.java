// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

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
        final int no = prefs.getInt(noKey(K_SRV), 0);
        for (int i = 0; i < no; ++i) {
            final ServerInfo s = readServer(prefs, K_SRV, i);
            if (s != null) result.add(s);
        }
        return result;
    }

    static void saveServers(SharedPreferences prefs, List<ServerInfo> svr) {
        SharedPreferences.Editor ed = prefs.edit();
        final int oldSize = prefs.getInt(noKey(K_SRV), 0);
        final int newSize = svr.size();
        ed.putInt(noKey(K_SRV), newSize);
        for (int i = 0; i < newSize; ++i) save(ed, K_SRV, i, svr.get(i));
        for (int i = newSize; i < oldSize; ++i) removeServer(ed, K_SRV, i);
        ed.commit();
    }

    static List<Bookmark> readBookmarks(SharedPreferences prefs) {
        final List<Bookmark> result = new ArrayList<Bookmark>();
        final int no = prefs.getInt(noKey(K_POL), 0);
        for (int i = 0; i < no; ++i) {
            final ServerInfo s = readServer(prefs, K_POL, i);
            final String p = prefs.getString(poolNameKey(K_POL, i), null);
            if (s != null) result.add(new Bookmark(s, p));
        }
        Collections.sort(result, CMP);
        return result;
    }

    static void saveBookmarks(SharedPreferences prefs, List<Bookmark> bmk) {
        SharedPreferences.Editor ed = prefs.edit();
        final int oldSize = prefs.getInt(noKey(K_POL), 0);
        final int newSize = bmk.size();
        ed.putInt(noKey(K_POL), newSize);
        for (int i = 0; i < newSize; ++i) {
            save(ed, K_POL, i, bmk.get(i).info);
            ed.putString(poolNameKey(K_POL, i), bmk.get(i).pool);
        }
        for (int i = newSize; i < oldSize; ++i) {
            removeServer(ed, K_POL, i);
            ed.remove(poolNameKey(K_POL, i));
        }
        ed.commit();
    }

    static boolean addBookmark(SharedPreferences prefs, Bookmark bmk) {
        List<Bookmark> bmks = readBookmarks(prefs);
        for (int i = 0, c = bmks.size(); i < c; ++i)
            if (bmks.get(i).equals(bmk)) return false;
        bmks.add(bmk);
        saveBookmarks(prefs, bmks);
        return true;
    }

    private static ServerInfo readServer(
        SharedPreferences prefs, String kind, int i) {
        final String uri = prefs.getString(serverUriKey(kind, i), null);
        final String name = prefs.getString(serverNameKey(kind, i), null);
        final String subt = prefs.getString(serverSubtypeKey(kind, i), null);
        final PoolServer srv = uri != null && name != null && subt != null ?
            PoolServers.get(uri, name, subt) : null;
        return srv == null ? null : new ServerInfo(srv, name, true);
    }

    private static void save(Editor ed, String kind, int i, ServerInfo info) {
        ed.putString(serverUriKey(kind, i),
                     info.server().address().toString());
        ed.putString(serverNameKey(kind, i), info.name());
        ed.putString(serverSubtypeKey(kind, i), info.server().subtype());
    }

    private static void removeServer(Editor ed, String kind, int i) {
        ed.remove(serverUriKey(kind, i));
        ed.remove(serverNameKey(kind, i));
        ed.remove(serverSubtypeKey(kind, i));
    }

    private static String noKey(String kind) {
        return String.format("%s.no", kind);
    }

    private static String serverUriKey(String kind, int n) {
        return String.format("%s.%d.uri", kind, n);
    }

    private static String serverNameKey(String kind, int n) {
        return String.format("%s.%d.name", kind, n);
    }

    private static String serverSubtypeKey(String kind, int n) {
        return String.format("%s.%d.subtype", kind, n);
    }

    private static String poolNameKey(String kind, int n) {
        return String.format("%s.%d.pool", kind, n);
    }

    private static final String K_SRV = "servers";
    private static final String K_POL = "pools";

    private static final Comparator<Bookmark> CMP =
        new Comparator<Bookmark>() {
          public int compare(Bookmark b0, Bookmark b1) {
              if (b0.pool == null && b1.pool == null)
                  return b0.info.name().compareTo(b1.info.name());
              if (b0.pool == null) return 1;
              if (b1.pool == null) return -1;
              int r = b0.pool.compareTo(b1.pool);
              return r == 0 ? b0.info.name().compareTo(b1.info.name()) : r;
          }
    };

    private Serializer() {}
}
