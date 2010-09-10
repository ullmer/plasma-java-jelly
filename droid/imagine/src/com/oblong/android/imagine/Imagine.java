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
        progressDialog.show();
        if (saveImageData(jpg)) {
            Intent i = new Intent(this, Sender.class);
            startActivity(i.putExtra(Sender.IMAGE_LEN, jpg.length));
        } else {
            progressDialog.dismiss();
            cameraView.restartPreview();
            showDialog(NO_IMAGE_DLG);
        }
    }

    private boolean saveImageData(byte[] jpg) {
        if (jpg != null) {
            try {
                FileOutputStream os =
                    openFileOutput(Sender.IMAGE_FILE, Context.MODE_PRIVATE);
                os.write(jpg);
                os.close();
                return true;
            } catch (Exception e) {
                Log.e("Imagine", "Error writing image file", e);
            }
        }
        return false;
    }

    private CameraView cameraView;
    private ProgressDialog progressDialog;

    private static final int NO_IMAGE_DLG = 0;
}
