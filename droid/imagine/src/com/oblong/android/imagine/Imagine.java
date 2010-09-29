// Copyright (c) 2010 Oblong Industries

package com.oblong.android.imagine;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
        ImageStore.clear();
    }

    @Override public void onResume() {
        super.onResume();
        progressDialog.dismiss();
        ImageStore.clear();
    }

    @Override public Dialog onCreateDialog(int d) {
        if (d == NO_IMAGE_DLG) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Sorry, could not get a snapshot")
                   .setCancelable(true);
            return builder.create();
        }
        return super.onCreateDialog(d);
    }

    @Override public void onClick(View button) {
        cameraView.takePicture(this);
    }

    @Override public void handleImage(byte[] jpg) {
        if (jpg != null) {
            ImageStore.storeImage(jpg);
            progressDialog.show();
            startActivity(new Intent(Imagine.this, Sender.class));
        } else {
            cameraView.restartPreview();
            showDialog(NO_IMAGE_DLG);
        }
    }

    private CameraView cameraView;
    private ProgressDialog progressDialog;

    private static final int NO_IMAGE_DLG = 0;
}
