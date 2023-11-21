package com.example.bitmaptopdf;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraState;
import androidx.camera.core.ExperimentalUseCaseGroup;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.VideoCapture;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.camera.view.TransformExperimental;
import androidx.camera.view.transform.CoordinateTransform;
import androidx.camera.view.transform.FileTransformFactory;
import androidx.camera.view.transform.ImageProxyTransformFactory;
import androidx.camera.view.transform.OutputTransform;
import androidx.lifecycle.Observer;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

@ExperimentalUseCaseGroup @TransformExperimental public class CameraActivity extends AppCompatActivity {
    public static final String TAG =">>>>>" + CameraActivity.class.getSimpleName();

    private Camera cam = null;

    private FrameLayout frameLayout, previewView;

    private CameraPreview cameraPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        frameLayout = findViewById(R.id.frame);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = width*16/9;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        //Xét preview cho camera
        cam = openCamera();
        previewView = findViewById(R.id.previewCamera);
        previewView.setLayoutParams(layoutParams);
        cameraPreview = new CameraPreview(this, cam);
        cameraPreview.setScaleX(-1f);
        previewView.addView(cameraPreview);
        Camera.Parameters parameters = cam.getParameters();
        Log.d(TAG, "Img width: " + parameters.getPictureSize().width);
        Log.d(TAG, "Img height: " + parameters.getPictureSize().height);
        parameters.setPreviewSize(1920, 1080);
        parameters.setPictureSize(1920, 1080);
        cam.setParameters(parameters);
        cam.setDisplayOrientation(270);
    }

    private Camera openCamera(){
        Camera  cam = null;
            try {
                cam = Camera.open(1); // attempt to get a Camera instance
            }
            catch (Exception e){
                // Camera is not available (in use or does not exist)
                Log.d(TAG, "openCamera: " +e.getMessage());
            }
            return cam;
    }

    public void onClickCapture(View view) {
//        capturePhoto();
        takePicture();
    }

    private void takePicture(){
        cam.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                Matrix matrix = new Matrix();
                matrix.setRotate(90f);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
                Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0,0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                Rect rect = new Rect();
                frameLayout.getGlobalVisibleRect(rect);
                Bitmap bitmap2 = Bitmap.createBitmap(bitmap1, Math.round(frameLayout.getX()), Math.round(frameLayout.getY()), rect.width(), rect.height(), null, true);

                MainActivity.bit = bitmap2;
                MainActivity.bitReal = bitmap1;
                Intent intent = new Intent(CameraActivity.this, MainActivity.class);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cam.stopPreview();
        cam.release();
    }
}