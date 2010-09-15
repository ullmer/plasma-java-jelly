// Copyright (c) 2010 Oblong Industries

package com.oblong.android.imagine;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 *
 * Created: Wed Sep 15 17:00:34 2010
 *
 * @author jao
 */
class ImageStore {

    static void storeImage(byte[] jpg) {
        imageData = jpg;
    }

    static void clear() {
        imageData = null;
    }

    static byte[] imageData() {
        return imageData;
    }

    static int dataLength() {
        return imageData == null ? 0 : imageData.length;
    }

    static Bitmap imageBitmap() {
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
    }

    static byte[] toData(Bitmap orig, Bitmap.CompressFormat fmt) {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        if (orig.compress(fmt, 0, data)) orig.recycle();
        return data.toByteArray();
    }

    static byte[] toPNG(Bitmap orig) {
        return toData(orig, Bitmap.CompressFormat.PNG);
    }

    private static byte[] imageData = null;


    private ImageStore() { }

}
