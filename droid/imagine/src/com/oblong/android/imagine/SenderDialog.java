// Copyright (c) 2010 Oblong Industries

package com.oblong.android.imagine;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.oblong.jelly.PoolAddress;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolServerAddress;

/**
 *
 * Created: Fri Sep 10 03:57:32 2010
 *
 * @author jao
 */
final class SenderDialog implements DialogInterface.OnClickListener,
                                    DialogInterface.OnDismissListener {

    SenderDialog(Sender parent) {
        this.parent = parent;
        createDialog();
        readPreferences();
    }


    void show() {
        address = null;
        dialog.show();
    }

    @Override public void onClick(DialogInterface dialog, int id) {
        switch (id) {
        case DialogInterface.BUTTON_POSITIVE:
            address = getPoolAddress();
            break;
        case DialogInterface.BUTTON_NEGATIVE:
            address = null;
            break;
        }
        dialog.dismiss();
    }

    @Override public void onDismiss(DialogInterface dlg) {
        if (address != null) {
        	savePreferences();
        	parent.send(address);
            address = null;
        }
    }

    private PoolAddress getPoolAddress() {
        try {
            final int p = Integer.parseInt(port.getText().toString());
            if (p < 0 || p > 65456) return null;
            final PoolServerAddress sa =
                new PoolServerAddress("tcp", host.getText().toString(), p);
            return new PoolAddress (sa, pool.getText().toString());
        } catch (PoolException e) {
            return null;
        }
    }

    private void createDialog() {
        LayoutInflater inflater = (LayoutInflater)
            parent.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final ViewGroup group =
            (ViewGroup) parent.findViewById(R.id.layout_root);
        final View layout = inflater.inflate(R.layout.sender_dialog, group);


        host = (EditText) layout.findViewById(R.id.hostname_entry);
        port = (EditText) layout.findViewById(R.id.port_entry);
        pool = (EditText) layout.findViewById(R.id.pool_entry);

        AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        builder.setView(layout);
        builder.setTitle("Select destination");
        builder.setPositiveButton("Send", this);
        builder.setNegativeButton("Cancel", this);

        dialog = builder.create();
        dialog.setOnDismissListener(this);
    }
    
    private void readPreferences() {
    	final SharedPreferences prefs = parent.getPreferences(0);
    	host.setText(prefs.getString(PREF_HOST, "192.168.1.1"));
    	port.setText(prefs.getString(PREF_PORT, "65456"));
    	pool.setText(prefs.getString(PREF_POOL, "imagine"));
    }

    private void savePreferences() {
    	final SharedPreferences prefs = parent.getPreferences(0);
    	final SharedPreferences.Editor ed = prefs.edit();
    	ed.putString(PREF_HOST, host.getText().toString());
    	ed.putString(PREF_PORT, port.getText().toString());
    	ed.putString(PREF_POOL, pool.getText().toString());
    	ed.commit();
    }
    
    private final Sender parent;
    private AlertDialog dialog;
    private EditText host;
    private EditText port;
    private EditText pool;
    private PoolAddress address;
    
    private static final String PREF_HOST = "host";
    private static final String PREF_PORT = "port";
    private static final String PREF_POOL = "pool";
}
