// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.oblong.jelly.PoolException;
import com.oblong.jelly.PoolServer;
import com.oblong.jelly.PoolServerAddress;
import com.oblong.jelly.PoolServers;

/**
 *
 * Created: Fri Sep 10 03:57:32 2010
 *
 * @author jao
 */
final class ServerDialog implements DialogInterface.OnClickListener,
                                    DialogInterface.OnDismissListener {

    ServerDialog(Ponder parent) {
        this.parent = parent;
        createDialog();
    }


    void show() {
        server = null;
        dialog.show();
    }

    @Override public void onClick(DialogInterface dialog, int id) {
        switch (id) {
        case DialogInterface.BUTTON_POSITIVE:
            server = getPoolServer();
            break;
        case DialogInterface.BUTTON_NEGATIVE:
            server = null;
            break;
        }
        dialog.dismiss();
    }

    @Override public void onDismiss(DialogInterface dlg) {
        if (server != null) {
            parent.registerServer(server, name.getText().toString());
            server = null;
        }
    }

    private PoolServer getPoolServer() {
        try {
            final int p = Integer.parseInt(port.getText().toString());
            if (p < 0 || p > 65456) return null;
            final PoolServerAddress sa =
                new PoolServerAddress("tcp", host.getText().toString(), p);
            return PoolServers.get(sa);
        } catch (PoolException e) {
            return null;
        }
    }

    private void createDialog() {
        LayoutInflater inflater = (LayoutInflater)
            parent.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final ViewGroup group =
            (ViewGroup) parent.findViewById(R.id.server_dialog);
        final View layout = inflater.inflate(R.layout.server_dialog, group);


        host = (EditText) layout.findViewById(R.id.hostname_entry);
        port = (EditText) layout.findViewById(R.id.port_entry);
        name = (EditText) layout.findViewById(R.id.name_entry);

        AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        builder.setView(layout);
        builder.setTitle("New Pool Server");
        builder.setPositiveButton("Add", this);
        builder.setNegativeButton("Cancel", this);

        dialog = builder.create();
        dialog.setOnDismissListener(this);
    }

    private final Ponder parent;
    private AlertDialog dialog;
    private EditText host;
    private EditText port;
    private EditText name;
    private PoolServer server;

}
