// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.oblong.jelly.PoolAddress;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.ProteinMetadata;

/**
 *
 * Created: Thu Dec  9 11:03:45 2010
 *
 * @author jao
 */
public class PoolDetails extends Activity
    implements AdapterView.OnItemClickListener {

    static void launch(Activity launcher, PoolAddress address) {
        final Intent intent = new Intent(launcher, PoolDetails.class);
        poolAddress = address;
        launcher.startActivity(intent);
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pool_details);
        jumpDialog = new JumpDialog(this);
        name = (TextView)findViewById(R.id.pool_name_entry);
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

    @Override public void onStart() {
        super.onStart();
        name.setText(poolAddress.poolName());
        try {
            final PoolCursor cursor = PoolInfo.get(poolAddress).cursor();
            if (adapter.getCursor() != cursor) adapter.changeCursor(cursor);
            if (cursor != null && proteinsTitle != null) {
                proteinsTitle.setText(
                    Utils.formatNumber(cursor.getCount(), "protein"));
            }
        } catch (PoolException e) {
            if (proteinsTitle != null) {
                proteinsTitle.setError("Error connecting to pool " + e);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> p, View v, int pos, long i) {
        final PoolInfo info = PoolInfo.tryGet(poolAddress);
        if (info != null)
            showProtein(info.cursor().getFirstIndex() + pos, pos);
    }

    void showProtein(final long idx, final int position) {
        final PoolInfo info = PoolInfo.tryGet(poolAddress);
        if (info != null) {
            final String m =
                String.format("Retrieving protein no. %d ...", idx);
            final ProgressDialog dlg = ProgressDialog.show(this, "", m);
            // final ListView noRealClosures = proteins;
            final String name = poolAddress.poolName();
            final Handler hdl = new Handler() {
                    public void handleMessage(Message m) {
                        if (m.obj != null) {
                            dlg.dismiss();
                            // noRealClosures.setSelection(position);
                            if (m.what == 0)
                                ProteinDetails.launch(PoolDetails.this,
                                                      (ProteinMetadata)m.obj,
                                                      name);
                            else
                                displayError(idx, (PoolException)m.obj);
                        }
                    }
                };
            new Thread(new Runnable() {
                    public void run() {
                        try {
                            hdl.sendMessage(
                                Message.obtain(hdl, 0, info.metadata(idx)));
                        } catch (PoolException e) {
                            hdl.sendMessage(Message.obtain(hdl, 1, e));
                        }
                    }
                }).start();
        }
    }

    private void displayInfo() {
    }

    private void displayError(long idx, PoolException e) {
        // TODO
        Ponder.logger().severe("Error retrieving protein " + idx
                               + ": " + e.getMessage());
    }

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
