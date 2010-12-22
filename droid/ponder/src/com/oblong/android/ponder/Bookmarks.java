// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.oblong.jelly.PoolAddress;

/**
 *
 * Created: Wed Dec 22 01:12:47 2010
 *
 * @author jao
 */
public final class Bookmarks extends ListActivity {

    static void launch(Activity launcher) {
        launcher.startActivity(new Intent(launcher, Bookmarks.class));
    }

    static void add(Activity adder, ServerInfo info) {
        add(adder, info, null);
    }

    static void add(Activity adder, ServerInfo info, String name) {
        final SharedPreferences p = adder.getSharedPreferences(BMK_FILE, 0);
        if (p != null) Serializer.addBookmark(p, new Bookmark(info, name));
        Toast.makeText(adder.getApplicationContext(),
                       "Bookmark added",
                       Toast.LENGTH_LONG).show();
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupListView();
        setupAdapter();
    }

    @Override public void onResume() {
        super.onResume();
        rescanBookmarks();
    }

    @Override public void onCreateContextMenu(ContextMenu menu,
                                              View v,
                                              ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bookmark_context, menu);
        final int pos = ((AdapterContextMenuInfo)menuInfo).position;
        // final String title = table.getItem(pos).server().name();
        // if (title != null) menu.setHeaderTitle(title);
    }

    @Override public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info =
            (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
        case R.id.delete_bmk:
            adapter.remove(adapter.getItem(info.position));
            Serializer.saveBookmarks(preferences,
                                     Serializer.readBookmarks(preferences));
            adapter.notifyDataSetChanged();
            return true;
        case R.id.open_bmk:
            openBookmark(info.position);
            return true;
        default:
            return super.onContextItemSelected(item);
        }
    }

    private void openBookmark(int pos) {
        final Bookmark b = adapter.getItem(pos);
        if (b != null && b.info != null) {
            if (b.isPool()) {
                try {
                    final PoolAddress a =
                        new PoolAddress(b.info.server().address(), b.pool);
                    PoolDetails.launch(this, b.info, a);
                } catch (Exception e) {
                    Ponder.logger().info("Error making address: " + e);
                }
            } else {
                ServerDetails.launch(this, b.info);
            }
        }
    }

    private void setupListView() {
        final ListView lv = getListView();
        registerForContextMenu(lv);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(
                    AdapterView<?> p, View view, int position, long id) {
                    openBookmark(position);
                }
            });
    }

    private void setupAdapter() {
        preferences = getSharedPreferences(BMK_FILE, 0);
        adapter = new BookmarkListAdapter(this);
        getListView().setAdapter(adapter);
        rescanBookmarks();
    }

    private void rescanBookmarks() {
        adapter.clear();
        List<Bookmark> bmks = Serializer.readBookmarks(preferences);
        for (int i = bmks.size() - 1; i >= 0; --i) adapter.add(bmks.get(i));
        adapter.notifyDataSetChanged();
    }

    private BookmarkListAdapter adapter;
    private SharedPreferences preferences;

    private static final String BMK_FILE = "Ponder.bookmarks";
}
