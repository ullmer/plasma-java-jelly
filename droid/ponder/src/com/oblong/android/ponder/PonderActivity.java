// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;

import com.oblong.jelly.PoolException;

/**
 *
 * Created: Wed Dec 15 22:22:45 2010
 *
 * @author jao
 */
class PonderActivity extends Activity {

    PonderActivity(String errorTitle) {
        this.errorTitle = errorTitle;
        this.infoLayout = -1;
        this.infoPreparer = null;
    }

    @Override protected Dialog onCreateDialog(int id, Bundle b) {
        switch (id) {
        case POOL_EXCEPTION_DIALOG:
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(errorTitle);
            return builder.create();
        case INFO_DIALOG:
            if (infoPreparer != null) return createDialog(infoLayout, "Info");
        }
        return super.onCreateDialog(id, b);
    }

    @Override protected void onPrepareDialog(int id, Dialog d, Bundle b) {
        if (id == POOL_EXCEPTION_DIALOG && lastException != null)
            ((AlertDialog)d).setMessage(lastException.toString());
        else if (id == INFO_DIALOG && infoPreparer != null)
            infoPreparer.prepare(d);
        else super.onPrepareDialog(id, d, b);
    }

    protected void showError(PoolException e) {
        Ponder.logger().severe(errorTitle + ": " + e.getMessage());
        lastException = e;
        showDialog(POOL_EXCEPTION_DIALOG, new Bundle());
    }

    protected AlertDialog createDialog(int lid, String title) {
        final LayoutInflater inflater = (LayoutInflater)
            getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(lid, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        builder.setTitle(title);
        // builder.setPositiveButton("Close", null);
        return builder.create();
    }

    protected interface InfoPreparer {
        void prepare(Dialog d);
    }

    protected void infoSetup(int layout, InfoPreparer up) {
        infoLayout = layout;
        infoPreparer = up == null ?
            new InfoPreparer () {
                public void prepare(Dialog d) { prepareInfo(d); }
            } : up;
    }

    protected void infoSetup(int layout) { infoSetup(layout, null); }

    protected void prepareInfo(Dialog d) {}

    protected void displayInfo() {
        if (infoPreparer != null)
            showDialog(INFO_DIALOG, new Bundle());
    }

    protected final View.OnClickListener infoListener =
        new View.OnClickListener () {
            public void onClick(View n) { displayInfo(); }
        };


    protected interface Task {
        Object run() throws PoolException;
    }

    protected interface Acceptor {
         void accept(Object r);
    }

    protected void launchAsyncTask(final Task task,
                                   final Acceptor handler,
                                   final String msg) {
        final Dialog dlg = ProgressDialog.show(this, "", msg);

        final Handler hdl = new Handler() {
                public void handleMessage(Message m) {
                    if (m.obj != null) {
                        dlg.dismiss();
                        if (m.what == 0) handler.accept(m.obj);
                        else showError((PoolException)m.obj);
                    }
                }
            };

        new Thread(new Runnable() {
                public void run() {
                    try {
                        hdl.sendMessage(Message.obtain(hdl, 0, task.run()));
                    } catch (PoolException e) {
                        hdl.sendMessage(Message.obtain(hdl, 1, e));
                    }
                }
            }).start();
    }

    protected static final int POOL_EXCEPTION_DIALOG = 1;
    protected static final int INFO_DIALOG = 2;

    private  PoolException lastException;
    private final String errorTitle;
    private int infoLayout;
    private InfoPreparer infoPreparer;
}
