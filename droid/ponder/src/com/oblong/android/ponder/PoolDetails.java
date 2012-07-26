// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.oblong.jelly.NoSuchPoolException;
import com.oblong.jelly.PoolAddress;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolMetadata;
import com.oblong.jelly.ProteinMetadata;

/**
 *
 * Created: Thu Dec  9 11:03:45 2010
 *
 * @author jao
 */
public class PoolDetails extends PonderActivity
    implements AdapterView.OnItemClickListener {

    static void launch(Activity launcher,
                       ServerInfo info,
                       PoolAddress address) {
        final Intent intent = new Intent(launcher, PoolDetails.class);
        poolAddress = address;
        serverInfo = info;
        launcher.startActivity(intent);
    }

    public PoolDetails() {
        super("Error retrieving protein");
        infoSetup(R.layout.pool_info_dialog);
    }

     public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pool_details);
        jumpDialog = new JumpDialog(this);
        name = (TextView)findViewById(R.id.pool_name_entry);
        name.setOnClickListener(infoListener);
        proteins = (ListView)findViewById(R.id.protein_list);
        proteinsTitle = (TextView)findViewById(R.id.protein_no);
        adapter = new SimpleCursorAdapter(proteins.getContext(),
                                          R.layout.protein_item,
                                          null,
                                          COLUMNS,
                                          IDS);
        proteins.setOnItemClickListener(this);
        proteins.setAdapter(adapter);
        findViewById(R.id.pool_goto).setOnClickListener(
            new View.OnClickListener () {
                public void onClick(View b) { jumpDialog.show(poolAddress); }
            });
    }

     public void onStart() {
        super.onStart();
        name.setText(poolAddress.poolName());
        refresh();
    }

     public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pool_details, menu);
        Bookmarks.prepareMenu(this, menu, serverInfo, poolAddress.poolName());
        return true;
    }

     public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.refresh:
            refresh();
            return true;
        case R.id.bookmark:
            Bookmarks.toggle(this, item, serverInfo, poolAddress.poolName());
            return true;
        case R.id.goto_bookmarks:
            Bookmarks.launch(this);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    
    public void onItemClick(AdapterView<?> p, View v, int pos, long i) {
        final PoolInfo info = PoolInfo.tryGet(poolAddress);
        if (info != null) showProtein(info.cursor().lastIndex() - pos, pos);
    }

     protected void prepareInfo(Dialog d) {
        d.setTitle(poolAddress.poolName());
        final PoolInfo info = PoolInfo.tryGet(poolAddress);
        final String prots = String.format("From %d to %d",
                                           info.cursor().firstIndex(),
                                           info.cursor().lastIndex());
        ((TextView)d.findViewById(R.id.proteins)).setText(prots);
        final PoolMetadata md = info.metadata();
        final long icap = md.indexCapacity();
        final String idx = icap > 0
            ? String.format("%d / %d", icap, md.usedIndexCapacity())
            : "No index";
        ((TextView)d.findViewById(R.id.index)).setText(idx);
        final String sstr = String.format("%s / %s",
                                          Utils.formatSize(md.usedSize()),
                                          Utils.formatSize(md.size()));
        ((TextView)d.findViewById(R.id.size)).setText(sstr);
    }

    void showProtein(final long idx, final int position) {
        final Task task = new Task() {
                public Object run() throws PoolException {
                    final PoolInfo info = PoolInfo.tryGet(poolAddress);
                    if (info == null)
                        throw new NoSuchPoolException("Server disappeared");
                    return info.metadata(idx);
                }
            };
        final String name = poolAddress.poolName();
        final Acceptor handler = new Acceptor() {
                public void accept(Object md) {
                    ProteinDetails.launch(PoolDetails.this,
                                          (ProteinMetadata)md,
                                          name);
                }
            };
        final String m = String.format("Retrieving protein no. %d ...", idx);
        launchAsyncTask(task, handler, m);
    }

    private void refresh() {
        final Task task = new Task() {
                public Object run() throws PoolException {
                    final PoolCursor cursor =
                        PoolInfo.get(poolAddress).cursor();
                    cursor.prepareForAdapter();
                    return cursor;
                }
            };
        final Acceptor hdl = new Acceptor() {
                public void accept(Object o) { accept((PoolCursor)o); }
                private void accept(PoolCursor cursor) {
                    if (adapter.getCursor() != cursor)
                        adapter.changeCursor(cursor);
                    if (cursor != null && proteinsTitle != null) {
                        proteinsTitle.setText(
                            Utils.formatNumber(cursor.getCount(), "protein"));
                    }
                }
            };
        final String msg = String.format("Connecting to '%s' ...",
                                         poolAddress.poolName());
        launchAsyncTask(task, hdl, msg);
    }

    private static ServerInfo serverInfo;
    private static PoolAddress poolAddress;

    private static final String[] COLUMNS = {
        "_id", "info"
    };

    private static final int[] IDS = {
        R.id.protein_id,
        R.id.protein_info,
    };

    private TextView name;
    private ListView proteins;
    private TextView proteinsTitle;
    private SimpleCursorAdapter adapter;
    private JumpDialog jumpDialog;
}
