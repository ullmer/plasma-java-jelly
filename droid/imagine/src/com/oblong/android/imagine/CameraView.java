// Copyright (c) 2010 Oblong Industries

package com.oblong.android.imagine;

import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.oblong.jelly.util.ExceptionHandler;

/**
 *
 * Created: Thu Sep  2 16:34:19 2010
 *
 * @author jao
 */
public class CameraView extends SurfaceView
    implements SurfaceHolder.Callback, Camera.PictureCallback {

    @SuppressWarnings("deprecation")
	public CameraView(Context context, AttributeSet attr) {
        super(context, attr);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try {
            camera = Camera.open();
            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(holder);
        } catch (Throwable e) {
            ExceptionHandler.handleException(e);
            if (camera != null) camera.release();
            camera = null;
            Log.e("CameraView", "Error accessing camera", e);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the
        // preview. Because the CameraDevice object is not a shared
        // resource, it's very important to release it when the
        // activity is paused.
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public void surfaceChanged(SurfaceHolder holder,
                                         int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();

            List<Size> sizes = parameters.getSupportedPreviewSizes();
            Size optimalSize = getOptimalPreviewSize(sizes, w, h);
            parameters.setPreviewSize(optimalSize.width, optimalSize.height);
            parameters.set("rotation", 90);

            camera.setParameters(parameters);
            camera.startPreview();
        }
    }

    // PictureCallback
    public void onPictureTaken(byte[] jpeg, Camera camera) {
        if (pictHandler != null && jpeg != null) {
            pictHandler.handleImage(jpeg);
        }
    }

    void takePicture(PictureHandler next) {
        if (next != null) pictHandler = next;
        if (camera != null) camera.takePicture(null, null, this);
    }

    void restartPreview() {
        if (camera != null) camera.startPreview();
    }

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private PictureHandler pictHandler;
}
