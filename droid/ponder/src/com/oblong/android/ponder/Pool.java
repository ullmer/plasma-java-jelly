// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.oblong.jelly.PoolAddress;

/**
 *
 * Created: Thu Dec  9 11:03:45 2010
 *
 * @author jao
 */
public class Pool extends Activity {

    static void launch(Activity launcher, PoolAddress address) {
        final Intent intent = new Intent(launcher, Pool.class);
        poolAddress = address;
        poolCursor = PoolCursor.get(address);
        launcher.startActivity(intent);
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pool);
        name = (TextView)findViewById(R.id.pool_name_entry);
        proteins = (ListView)findViewById(R.id.protein_list);
        proteinsTitle = (TextView)findViewById(R.id.protein_no);
        adapter = new SimpleCursorAdapter(proteins.getContext(),
                                          R.layout.protein_item,
                                          null,
                                          COLUMNS,
                                          IDS);
        proteins.setAdapter(adapter);
    }

    @Override public void onStart() {
        super.onStart();
        name.setText(poolAddress.poolName());
        if (adapter.getCursor() != poolCursor)
            adapter.changeCursor(poolCursor);
        if (poolCursor != null && proteinsTitle != null) {
            proteinsTitle.setText(poolCursor.getCount() + " protein"
                                  + (poolCursor.getCount() == 1 ? "" : "s"));
        }
    }

    private static PoolAddress poolAddress;
    private static PoolCursor poolCursor;

    private static final String[] COLUMNS = {
        "_id", "descrip_no", "ingest_no", "size"
    };
    private static final int[] IDS = {
        R.id.protein_id,
        R.id.protein_descrip_no,
        R.id.protein_ingest_no,
        R.id.protein_size
    };

    private TextView name;
    private ListView proteins;
    private TextView proteinsTitle;
    private SimpleCursorAdapter adapter;
}
