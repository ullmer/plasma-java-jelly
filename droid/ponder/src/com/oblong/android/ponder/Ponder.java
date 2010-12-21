// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import java.util.List;
import java.util.logging.Logger;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.oblong.jelly.PoolServer;

/**
 *
 * Created: Tue Nov 23 15:57:08 2010
 *
 * @author jao
 */
public final class Ponder extends ListActivity {

    static Logger logger() {
        return Logger.getLogger("Ponder");
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpListView();
        mcLock = setupMulticastLock();
        table = setupServerTable();

        refreshToast = Toast.makeText(getApplicationContext(),
                                      "Scanning in progress...",
                                      Toast.LENGTH_LONG);
        serverDialog = new ServerDialog(this);
    }


    @Override public void onResume() {
        super.onResume();
        final int MAX_VIEWS = 5;
        if (MAX_VIEWS < ++helpViews) getListView().removeFooterView(helpView);
        mcLock.acquire();
        table.rescan();
    }

    @Override public void onStop() {
        super.onStop();
        mcLock.release();
        Serializer.saveServers(preferences, table.registeredServers());
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.server_list, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.add_server:
            serverDialog.show();
            return true;
        case R.id.search_servers:
            refreshToast.show();
            table.reset();
            return true;
        case R.id.cleanup:
            table.deleteUnreachable();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override public void onCreateContextMenu(ContextMenu menu,
                                              View v,
                                              ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.server_context, menu);
        final int pos = ((AdapterContextMenuInfo)menuInfo).position;
        final String title = table.getItem(pos).server().name();
        if (title != null) menu.setHeaderTitle(title);
    }

    @Override public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info =
            (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
        case R.id.delete_server:
            table.removeServer(info.position, true);
            return true;
        case R.id.refresh_server:
            table.refreshServer(info.position);
            return true;
        case R.id.show_server:
            launchServerDetails(info.position);
            return true;
        case R.id.close_menu:
            return true;
        default:
            return super.onContextItemSelected(item);
        }
    }

    static final String PREFS_FILE = "Ponder.preferences";

    void registerServer(PoolServer server, String name) {
        table.registerServer(new ServerInfo(server, name, true));
    }

    private void launchServerDetails(int position) {
        ServerDetails.launch(this, table.getItem(position));
    }

    private void setUpListView() {
        final ListView lv = getListView();
        registerForContextMenu(lv);

        helpView =
            getLayoutInflater().inflate(R.layout.server_list_help, null);
        lv.addFooterView(helpView, null, false);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(
                    AdapterView<?> p, View view, int position, long id) {
                    launchServerDetails(position);
                }
            });
    }

    private MulticastLock setupMulticastLock () {
        final WifiManager wifiMngr =
            (WifiManager)getSystemService(Context.WIFI_SERVICE);
        final int address = wifiMngr.getDhcpInfo().ipAddress;
        System.setProperty("net.mdns.interface",
                           Formatter.formatIpAddress(address));
        MulticastLock lk = wifiMngr.createMulticastLock("_ponder-lock");
        lk.setReferenceCounted(false);
        lk.acquire();
        return lk;
    }

    private ServerTable setupServerTable() {
        preferences = getSharedPreferences(PREFS_FILE, 0);
        final ServerTable t = new ServerTable(this);
        final List<ServerInfo> infs = Serializer.readServers(preferences);
        for (ServerInfo i : infs) t.registerServer(i);
        return t;
    }

    private ServerTable table;
    private ServerDialog serverDialog;
    private Toast refreshToast;
    private View helpView;
    private int helpViews = 0;
    private MulticastLock mcLock;
    private SharedPreferences preferences;
}
