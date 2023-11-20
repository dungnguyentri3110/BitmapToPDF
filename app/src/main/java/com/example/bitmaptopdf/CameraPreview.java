package com.example.bitmaptopdf;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import java.io.IOException;

public class CameraPreview extends TextureView implements TextureView.SurfaceTextureListener {
    public static final String TAG = CameraPreview.class.getSimpleName();

    SurfaceTexture mHolder;

    private Camera mCamera;

    public CameraPreview(@NonNull Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mHolder = getSurfaceTexture();
        this.setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
        try {
            Log.d(TAG, "onSurfaceTextureAvailable: ");
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
        Log.d(TAG, "onSurfaceTextureDestroyed: ");
        surfaceTexture.release();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {
    }
}
