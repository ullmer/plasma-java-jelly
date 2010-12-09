// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
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
        launcher.startActivity(intent);
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pool);
        name = (TextView)findViewById(R.id.pool_name_entry);
        proteins = (ListView)findViewById(R.id.protein_list);
    }

    @Override public void onStart() {
        super.onStart();
        name.setText(poolAddress.poolName());
    }

    private static PoolAddress poolAddress;

    private TextView name;
    private ListView proteins;
}
