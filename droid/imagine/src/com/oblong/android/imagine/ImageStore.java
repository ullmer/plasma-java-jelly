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
        clear();
        imageData = jpg;
    }

    static void clear() {
        imageData = null;
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    static byte[] toJPG() {
        return imageData;
    }

    static int dataLength() {
        return imageData == null ? 0 : imageData.length;
    }

    static Bitmap imageBitmap() {
        if (bitmap == null && imageData != null) {
            bitmap =
                BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        }
        return bitmap;
    }

    static byte[] toPNG() {
        final ByteArrayOutputStream data = new ByteArrayOutputStream();
        final Bitmap bmp = imageBitmap();
        if (bmp != null && bmp.compress(Bitmap.CompressFormat.PNG, 0, data))
            {
                bitmap.recycle();
                bitmap = null;
            }
        return data.toByteArray();
    }

    private static byte[] imageData = null;
    private static Bitmap bitmap = null;


    private ImageStore() { }

}
