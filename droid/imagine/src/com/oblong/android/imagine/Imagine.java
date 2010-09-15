// Copyright (c) 2010 Oblong Industries

package com.oblong.android.imagine;

import java.io.FileOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

public class Imagine extends Activity
    implements View.OnClickListener, PictureHandler {

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        cameraView = (CameraView) findViewById(R.id.camera_view);
        findViewById(R.id.take_snapshot).setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Snapshotting...");
    }

    @Override public void onResume() {
        super.onResume();
        progressDialog.dismiss();
    }

    @Override public Dialog onCreateDialog(int d) {
        Dialog dlg = null;
        if (d == NO_IMAGE_DLG) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Sorry, could not get a snapshot")
                   .setCancelable(true);
            dlg = builder.create();
        }
        return dlg;
    }

    @Override public void onClick(View button) {
        cameraView.takePicture(this);
    }

    @Override public void handleImage(byte[] jpg) {
        final Handler h = new Handler() {
                public void handleMessage(Message m) {
                    if (m.what == 0) {
                        Intent i = new Intent(Imagine.this, Sender.class);
                        startActivity(i);
                    } else {
                        progressDialog.dismiss();
                        cameraView.restartPreview();
                        showDialog(NO_IMAGE_DLG);
                    }
                }
            };
        progressDialog.show();
        new Saver(jpg, h).start();
    }

    private class Saver extends Thread {
        Saver(byte[] jpg, Handler h) {
            data = jpg;
            handler = h;
        }

        public void run() {
            Message result = Message.obtain(handler, 0, 0, 0);
            if (data != null) {
                try {
                    FileOutputStream os =
                        openFileOutput(Sender.IMAGE_FILE,
                                       Context.MODE_PRIVATE);
                    os.write(data);
                    os.close();
                } catch (Exception e) {
                    Log.e("Imagine", "Error writing image file", e);
                    result.what = 1;
                }
            }
            handler.sendMessage(result);
        }

        private final byte[] data;
        private final Handler handler;
    }

    private CameraView cameraView;
    private ProgressDialog progressDialog;

    private static final int NO_IMAGE_DLG = 0;
}
