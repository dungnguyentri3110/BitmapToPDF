package com.example.bitmaptopdf;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ExperimentalUseCaseGroup;
import androidx.camera.view.TransformExperimental;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ExperimentalUseCaseGroup
@TransformExperimental
public class CameraActivity extends AppCompatActivity {
    public static final String TAG = ">>>>>" + CameraActivity.class.getSimpleName();

    private final Camera cam = null;
    private final List<FrameLayout> frames = new ArrayList<>();
    PaperSize currentSize = PaperSize.ALL;
    List<Bitmap> bitmaps = new ArrayList<>();
    private FrameLayout frameLayoutA4, previewView;
    private FrameLayout frameLayoutA5;
    private FrameLayout frameLayoutA6;
    private FrameLayout frameLayoutCCCD;
    private CameraPreview cameraPreview;

    // Hàm để thay đổi lần lượt giá trị của enum PaperSize
    private static PaperSize changePaperSizeSequentially(PaperSize currentSize) {
        // Lấy danh sách tất cả các giá trị của enum
        PaperSize[] sizes = PaperSize.values();

        // Tìm vị trí của giá trị hiện tại trong mảng
        int currentIndex = -1;
        for (int i = 0; i < sizes.length; i++) {
            if (sizes[i] == currentSize) {
                currentIndex = i;
                break;
            }
        }

        // Chuyển đến giá trị tiếp theo trong mảng (lặp lại nếu đã ở cuối mảng)
        int nextIndex = (currentIndex + 1) % sizes.length;

        // Trả về giá trị mới
        return sizes[nextIndex];
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setUpViewFrame();
        configShowFrame(PaperSize.ALL);

//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//
//        int width = displayMetrics.widthPixels;
//        int height = width*16/9;
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        //Xét preview cho camera
//        cam = openCamera();
//        previewView = findViewById(R.id.previewCamera);
//        previewView.setLayoutParams(layoutParams);
//        cameraPreview = new CameraPreview(this, cam);
//        cameraPreview.setScaleX(-1f);
//        previewView.addView(cameraPreview);
//        Camera.Parameters parameters = cam.getParameters();
//        Log.d(TAG, "Img width: " + parameters.getPictureSize().width);
//        Log.d(TAG, "Img height: " + parameters.getPictureSize().height);
//        cam.setParameters(parameters);
//        cam.setDisplayOrientation(270);
    }

    //Hàm để cài đặt view cho các Frame
    protected void setUpViewFrame() {
        frameLayoutA4 = findViewById(R.id.frameA4);
        frameLayoutA5 = findViewById(R.id.frameA5);
        frameLayoutA6 = findViewById(R.id.frameA6);
        frameLayoutCCCD = findViewById(R.id.frameCCCD);
        frames.add(frameLayoutA4);
        frames.add(frameLayoutA5);
        frames.add(frameLayoutA6);
        frames.add(frameLayoutCCCD);

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        // Lấy thông tin về màn hình
        Display display = windowManager.getDefaultDisplay();
        // Lấy kích thước của màn hình
        int screenWidth = display.getWidth();

        frameLayoutA4.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int widthA4 = screenWidth - 100 * 2;
            int heightA4 = widthA4 * 297 / 210;
            int widthA5 = widthA4 - 2 * 100;
            int heightA5 = widthA5 * 210 / 148;
            int widthA6 = widthA5 - 2 * 80;
            int heightA6 = widthA6 * 148 / 105;
            int widthCCCD = widthA6 - 2 * 100;
            int heightCCCD = (int) (widthCCCD * 85.6 / 53.98);
            ViewGroup.LayoutParams layoutParamsA4 = frameLayoutA4.getLayoutParams();
            ViewGroup.LayoutParams layoutParamsA5 = frameLayoutA5.getLayoutParams();
            ViewGroup.LayoutParams layoutParamsA6 = frameLayoutA6.getLayoutParams();
            ViewGroup.LayoutParams layoutParamsCCCD = frameLayoutCCCD.getLayoutParams();
            layoutParamsA4.width = widthA4;
            layoutParamsA4.height = heightA4;
            frameLayoutA4.setLayoutParams(layoutParamsA4);

            layoutParamsA5.width = widthA5;
            layoutParamsA5.height = heightA5;
            frameLayoutA5.setLayoutParams(layoutParamsA5);

            layoutParamsA6.width = widthA6;
            layoutParamsA6.height = heightA6;
            frameLayoutA6.setLayoutParams(layoutParamsA6);

            layoutParamsCCCD.width = widthCCCD;
            layoutParamsCCCD.height = heightCCCD;
            frameLayoutCCCD.setLayoutParams(layoutParamsCCCD);
        });
    }

    //Hàm để config ẩn/hiện các frame
    protected void configShowFrame(PaperSize paperSize) {
        switch (paperSize) {
            case ALL:
                // Xử lý khi chọn "all"
                frames.forEach(frameLayout -> {
                    frameLayout.setVisibility(View.VISIBLE);
                });
                break;
            case A4:
                // Xử lý khi chọn "a4"
                showOnlyFrame(frameLayoutA4);
                break;
            case A5:
                // Xử lý khi chọn "a5"
                showOnlyFrame(frameLayoutA5);
                break;
            case A6:
                // Xử lý khi chọn "a6"
                showOnlyFrame(frameLayoutA6);
                break;
            case CCCD:
                // Xử lý khi chọn "cccd"
                showOnlyFrame(frameLayoutCCCD);
                break;
            case NONE:
                // Xử lý khi chọn "None"
                frames.forEach(frameLayout -> {
                    frameLayout.setVisibility(View.INVISIBLE);
                });
                break;
            default:
                // Xử lý khi không trùng với bất kỳ case nào
                break;
        }
    }

    //Hàm để hiển thị duy nhất 1 frame chỉ định
    protected void showOnlyFrame(FrameLayout fL) {
        frames.forEach(frameLayout -> {
            if (frameLayout == fL) {
                frameLayout.setVisibility(View.VISIBLE);
            } else {
                frameLayout.setVisibility(View.INVISIBLE);
            }
        });
    }

    private Camera openCamera() {
        Camera cam = null;
        try {
            cam = Camera.open(0); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.d(TAG, "openCamera: " + e.getMessage());
        }
        return cam;
    }

    public void onClickCapture(View view) {
//        capturePhoto();
//        takePicture();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img);
        bitmaps.add(bitmap);

    }

    public void onClickDone(View view) {
        PDFGenerator pdfGenerator = new PDFGenerator();
        PdfDocument pdfDocument = pdfGenerator.initFromBitmaps(bitmaps);
        try {
            pdfGenerator.saveDocumentToStorage(pdfDocument);
            Toast.makeText(this, "Make pdf success", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onClickChangePaperSize(View view) {
        currentSize = changePaperSizeSequentially(currentSize);
        configShowFrame(currentSize);
    }

    private void takePicture() {
        cam.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                Matrix matrix = new Matrix();
                matrix.setRotate(90f);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
                Bitmap bitmap1 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                Rect rect = new Rect();
                frameLayoutA4.getGlobalVisibleRect(rect);
                Bitmap bitmap2 = Bitmap.createBitmap(bitmap1, Math.round(frameLayoutA4.getX()), Math.round(frameLayoutA4.getY()), rect.width(), rect.height(), null, true);

                MainActivity.bit = bitmap2;
                MainActivity.bitReal = bitmap1;
                Intent intent = new Intent(CameraActivity.this, MainActivity.class);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    //Hàm để fake action tạo paper size
    protected PaperSize getRandomSize() {
        PaperSize[] sizes = PaperSize.values();
        Random random = new Random();

        // Chọn ngẫu nhiên một giá trị từ enum
        PaperSize randomSize = sizes[random.nextInt(sizes.length)];
        return randomSize;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cam.stopPreview();
        cam.release();
    }
}

