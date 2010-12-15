// Copyright (c) 2010 Oblong Industries

package com.oblong.android.ponder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

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
    }

    @Override protected Dialog onCreateDialog(int id, Bundle b) {
        if (id != POOL_EXCEPTION_DIALOG) return super.onCreateDialog(id, b);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(errorTitle);
        return builder.create();
    }

    @Override protected void onPrepareDialog(int id, Dialog d, Bundle b) {
        if (id == POOL_EXCEPTION_DIALOG && lastException != null)
            ((AlertDialog)d).setMessage(lastException.toString());
        else
            super.onPrepareDialog(id, d, b);
    }

    protected void showError(PoolException e) {
        Ponder.logger().severe(errorTitle + ": " + e.getMessage());
        lastException = e;
        showDialog(POOL_EXCEPTION_DIALOG, new Bundle());
    }


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

    private static final int POOL_EXCEPTION_DIALOG = 1;

    private  PoolException lastException;
    private final String errorTitle;
}
