// Copyright (c) 2010 Oblong Industries

package com.oblong.android.imagine;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
        createDialog(parent);
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

    private void createDialog(Sender parent) {
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

    private final Sender parent;
    private AlertDialog dialog;
    private EditText host;
    private EditText port;
    private EditText pool;
    private PoolAddress address;
}
