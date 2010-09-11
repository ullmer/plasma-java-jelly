// Copyright (c) 2010 Oblong Industries

package com.oblong.android.imagine;

import java.io.FileInputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
        sendButton = (Button) findViewById(R.id.send_image);
        sendButton.setOnClickListener(this);
        senderDialog = new SenderDialog(this);
    }

    @Override public void onStart() {
    	super.onStart();
        final int len = getIntent().getIntExtra(IMAGE_LEN, 0);
        imageData = readImageData(IMAGE_FILE, len);
        if (imageData != null) setBitmap(imageData, imageData.length);
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
    static final String IMAGE_LEN = "Image.length";

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
    };

    private byte[] readImageData(final String fileName, final int size) {
        try {
            final FileInputStream is = openFileInput(fileName);
            final byte[] buffer = new byte[size];
            int offset = 0;
            try {
                while (size > offset) {
                    int read = is.read(buffer, offset, size - offset);
                    if (read < 0) return null;
                    offset += read;
                };
            } finally {
                is.close();
            }
            return buffer;
        } catch (Exception e) {
            Log.e("Sender", "Error reading image file", e);
            return null;
        }
    }

    private void setBitmap(byte[] data, int len) {
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, len, null);
        if (bmp != null) {
            int w = bmp.getWidth();
            int h = bmp.getHeight();
            Matrix mtx = new Matrix();
            mtx.postRotate(90);
            Bitmap rotatedBMP =
                Bitmap.createBitmap(bmp, 0, 0, w, h, mtx, true);
            BitmapDrawable bmd = new BitmapDrawable(rotatedBMP);
            view.setImageDrawable(bmd);
        }
    }


    private Protein ensureProtein() {
        if (protein == null)
            protein = Slaw.protein(DESCRIPS, null, imageData);
        return protein;
    }

    private ImageView view;
    private Button sendButton;
    private SenderDialog senderDialog;
    private byte[] imageData;
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
