// Copyright (c) 2010 Oblong Industries

package com.oblong.android.imagine;

import java.io.FileInputStream;

import android.app.Activity;
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
    }

    @Override public void onStart() {
    	super.onStart();
        final int len = getIntent().getIntExtra(IMAGE_LEN, 0);
        imageData = readImageData(IMAGE_FILE, len);
        if (imageData != null) makeBitmap(imageData, imageData.length);
    }

    static final String IMAGE_FILE = "Image.file";
    static final String IMAGE_LEN = "Image.length";

    private byte[] readImageData(String fileName, int size) {
        try {
            final FileInputStream is = openFileInput(fileName);
            byte[] buffer = new byte[size];
            int offset = 0;
            int rem = size;
            while (rem > 0) {
                int read = is.read(buffer, offset, size - offset);
                if (read < 0) return null;
                offset += read;
                rem -= read;
            };
            is.close();
            return buffer;
        } catch (Exception e) {
            Log.v("Sender", "Error reading image data");
            Log.v("Sender", e.getMessage());
            return null;
        }
    }

    private void makeBitmap(byte[] data, int len) {
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

    private ImageView view;
    private byte[] imageData;
}
