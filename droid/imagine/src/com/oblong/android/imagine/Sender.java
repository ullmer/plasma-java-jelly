// Copyright (c) 2010 Oblong Industries

package com.oblong.android.imagine;


import java.io.FileInputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

/**
 *
 * Created: Mon Sep  6 16:35:31 2010
 *
 * @author jao
 */
public class Sender extends Activity {

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sender);
        view = (ImageView) findViewById(R.id.pic_view);
        //        progressDlg = new ProgressDialog(this);
        //        progressDlg.setMessage("Snapshotting. Please wait...");
    }

    @Override public void onStart() {
    	super.onStart();
        extractImage();
    }

    static final String IMAGE_FILE = "Image.file";
    static final String IMAGE_LEN = "Image.length";

    private void extractImage() {
        try {
            final FileInputStream is = openFileInput(IMAGE_FILE);
            final int size = getIntent().getIntExtra(IMAGE_LEN, 0);
            byte[] image = new byte[size];
            int offset = 0;
            int rem = size;
            while (rem > 0) {
                int read = is.read(image, offset, size - offset);
                if (read > 0) {
                    offset += read;
                    rem -= read;
                } else {
                    rem = 0;
                }
            };
            is.close();
            setImage (image, offset);
        } catch (Exception e) {
            Log.v("Sender", "Oh my god");
        }
    }

    private void setImage(byte[] image, int len) {
        Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, len, null);
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

    private ImageView view;
    // private ProgressDialog progressDlg;
}
