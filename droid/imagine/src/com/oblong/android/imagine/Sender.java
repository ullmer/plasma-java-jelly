// Copyright (c) 2010 Oblong Industries

package com.oblong.android.imagine;

import java.io.FileInputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.oblong.jelly.Hose;
import com.oblong.jelly.Pool;
import com.oblong.jelly.PoolAddress;
import com.oblong.jelly.PoolException;
import com.oblong.jelly.Protein;
import com.oblong.jelly.Slaw;

/**
 *
 * Created: Mon Sep  6 16:35:31 2010
 *
 * @author jao
 */
public class Sender extends Activity implements View.OnClickListener {

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sender);

        view = (ImageView) findViewById(R.id.pic_view);
        findViewById(R.id.send_image).setOnClickListener(this);

        senderDialog = new SenderDialog(this);
    }

    @Override public void onStart() {
    	super.onStart();
        readImage();
    }

    @Override public void onResume() {
    	super.onResume();
        readImage();
    }

    @Override public void onPause() {
        super.onPause();
        recycleBitmap();
    }

    @Override public void onStop() {
        super.onStop();
        recycleBitmap();
    }

    @Override public void onClick(View button) {
        senderDialog.show();
    }

    @Override public Dialog onCreateDialog(int d) {
        switch (d) {
        case SENT_DLG_BAD_ADDR: case SENT_DLG_OK: case SENT_DLG_KO:
            return makeSentDialog(d);
        case PROGRESS_DLG:
            return makeProgressDialog();
        default:
            return super.onCreateDialog(d);
        }
    }

    private Dialog makeSentDialog(int id) {
        class DlgListener implements DialogInterface.OnClickListener {
            public DlgListener(int id) { this.id = id; }
            public void onClick(DialogInterface dlg, int n) {
                dlg.dismiss();
                if (id == SENT_DLG_BAD_ADDR) senderDialog.show();
            }
            private final int id;
        };

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(SENT_DLG_MSGS[id]);
        builder.setNeutralButton("OK", new DlgListener(id));
        return builder.create();
    }

    private Dialog makeProgressDialog() {
        ProgressDialog result = new ProgressDialog(this);
        result.setCancelable(false);
        result.setMessage("Sending image...");
        return result;
    }

    static final String IMAGE_FILE = "Image.file";

    void send(PoolAddress addr) {
        if (addr != null) {
            showDialog(PROGRESS_DLG);
            final Handler handler = new Handler () {
                    public void handleMessage(Message m) {
                        Sender.this.dismissDialog(PROGRESS_DLG);
                        Sender.this.showDialog(m.what);
                    }
                };
            new SenderThread(addr, handler).start();
        } else
            showDialog(SENT_DLG_BAD_ADDR);
    }

    private class SenderThread extends Thread {
        SenderThread(PoolAddress addr, Handler h) {
            address = addr;
            handler = h;
        }

        public void run() {
            int dlg = SENT_DLG_OK;
            Hose h = null;
            try {
                h = Pool.participate(address);
                h.deposit(Sender.this.ensureProtein());
            } catch (PoolException e) {
                Log.e("SenderThread", "Error depositing protein", e);
                dlg = SENT_DLG_KO;
                SENT_DLG_MSGS[SENT_DLG_KO] =
                    "Error connecting to pool:\n" + e.getMessage();
            } finally {
                if (h != null) h.withdraw();
            }
            handler.sendMessage(Message.obtain(handler, dlg));
        }

        private final PoolAddress address;
        private final Handler handler;
    }

    private void readImage() {
        recycleBitmap();
        try {
            bitmap = ImageStore.imageBitmap();
            view.setImageBitmap(bitmap);
        } catch (Throwable e) {
            Log.e("Sender", "Error reading image file", e);
            finish();
        }
    }

    private Protein ensureProtein() {
        if (protein == null && bitmap != null) {
            try {
                protein =
                    Slaw.protein(DESCRIPS, null, ImageStore.toPNG(bitmap));
                recycleBitmap();
            } catch (Throwable e) {
                Log.e("Sender", "Error converting image", e);
                return null;
            }4
        }
        return protein;
    }

    private void recycleBitmap() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    private ImageView view;
    private SenderDialog senderDialog;
    private Bitmap bitmap;
    private Protein protein;

    private static final Slaw DESCRIPS = Slaw.list(Slaw.string("imagine"));
    private static final int PROGRESS_DLG = -1;
    private static final int SENT_DLG_OK = 0;
    private static final int SENT_DLG_KO = 1;
    private static final int SENT_DLG_BAD_ADDR = 2;
    private static final String[] SENT_DLG_MSGS = {
        "Image sent",
        "Could not connect to pool",
        "The pool address is incorrect"
    };
}
