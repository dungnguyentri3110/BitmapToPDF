package com.example.bitmaptopdf;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
    private List<Camera.Size> cameraSupportPictureSize = new ArrayList<>();
    private List<Camera.Size> cameraSupportPreviewSize = new ArrayList<>();
    private Camera.Size cameraPreviewSize;
    private Camera.Size cameraPictureSize;

    private Camera cam = null;
    private final List<FrameLayout> frames = new ArrayList<>();
    PaperSize currentSize = PaperSize.A4;
    int papeNumber = 4;
    List<PaperDocument> pages = new ArrayList<>();
    private FrameLayout frameLayoutA4, previewView;
    private FrameLayout frameLayoutA5;
    private FrameLayout frameLayoutA6;
    private FrameLayout frameLayoutCCCD;
    private CameraPreview cameraPreview;
    private TextView textViewPageNumber;

    private int MARGIN_FRAME = 50;

    private ViewTreeObserver.OnGlobalLayoutListener lis = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            addListenerLoadViewFrame();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setUpCamera();
        setUpViewFrame();
        setTextCount(pages.size());
    }
    protected void setUpCamera(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        previewView = findViewById(R.id.previewCamera);
//        Xét preview cho camera
        cam = openCamera();
        if (cam == null) {
            Log.d("Init Camera", "Không thể khởi tạo camera");
            Toast.makeText(this,"Không thể khởi tạo camera",Toast.LENGTH_LONG).show();
            return;
        }
        cameraPreview = new CameraPreview(this, cam);
//        cameraPreview.setScaleX(-1f);
        previewView.addView(cameraPreview);
        Camera.Parameters parameters = cam.getParameters();
        cameraSupportPictureSize = parameters.getSupportedPictureSizes();
        cameraSupportPreviewSize = parameters.getSupportedPreviewSizes();
        cameraPictureSize = getSizeMax(cameraSupportPictureSize);
        cameraPreviewSize = getSizeMax(cameraSupportPreviewSize);
        if (cameraPictureSize != null) {
            parameters.setPictureSize(cameraPictureSize.width, cameraPictureSize.height);
        }
        if (cameraPreviewSize != null) {
            parameters.setPreviewSize(cameraPreviewSize.width, cameraPreviewSize.height);
        }
        parameters.setPreviewFrameRate(25);
        cam.setParameters(parameters);
        cam.enableShutterSound(true);
        cam.setDisplayOrientation(90);
    }

    //Hàm để cài đặt view cho các Frame
    protected void setUpViewFrame() {
        frameLayoutA4 = findViewById(R.id.frameA4);
        frameLayoutA5 = findViewById(R.id.frameA5);
        frameLayoutA6 = findViewById(R.id.frameA6);
        frameLayoutCCCD = findViewById(R.id.frameCCCD);
        textViewPageNumber = findViewById(R.id.txtPageNumber);
        frames.add(frameLayoutA4);
        frames.add(frameLayoutA5);
        frames.add(frameLayoutA6);
        frames.add(frameLayoutCCCD);

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        // Lấy thông tin về màn hình
        Display display = windowManager.getDefaultDisplay();
        // Lấy kích thước của màn hình
        int screenWidth = display.getWidth();

        previewView.getViewTreeObserver().addOnGlobalLayoutListener(lis);
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
        takePicture(() -> {
            int countPage = pages.size();
            setTextCount(countPage);
            if (pages.size() >= papeNumber) {
                onClickDone(view);
            } else if (cam != null) {
                cam.startPreview();
            }
        });
    }

    public void onClickDone(View view) {
        PDFGenerator pdfGenerator = new PDFGenerator();
        PdfDocument pdfDocument = pdfGenerator.initFromPaperDocument(pages);
        try {
            pdfGenerator.saveDocumentToStorage(pdfDocument);
            Toast.makeText(this, "Make pdf success", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CameraActivity.this, MainActivity.class);
            setResult(RESULT_OK, intent);
            finish();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onClickChangePaperSize(View view) {
        showPaperSizeDialog();
    }

    private void takePicture(CallBackCaptureDone callBackCaptureDone) {
        try {
            final int PADDING = 20;
            cam.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] bytes, Camera camera) {


                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
                    Matrix matrix = new Matrix();
                    matrix.setRotate(-90f);

                Bitmap bitmap10 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                Bitmap bitmap1 = flipVerticalBitmap(bitmap10);
                Rect rect = new Rect();
                FrameLayout frameLayout = getFrameFromSize(currentSize);
                frameLayout.getGlobalVisibleRect(rect);
                float ratioX = (float)cameraPreviewSize.width/(float)previewView.getHeight();
                float ratioY = (float) cameraPreviewSize.height/(float)previewView.getWidth();

                int top = Math.round(ratioY*rect.top) - PADDING;
                int left = Math.round(ratioX*rect.left) - PADDING ;
                int width = Math.round(rect.width()*ratioY) + 2*PADDING;
                int height = Math.round(rect.height()*ratioX) + 2*PADDING;
                Bitmap bitmap2 = Bitmap.createBitmap(bitmap1, left, top, width, height, null, true);

                Bitmap bitmap3 = flipHorizontalBitmap(bitmap2);
                pages.add(new PaperDocument(currentSize, bitmap3));
                callBackCaptureDone.done();
                }
            });
        }catch (Exception e){
            Log.d(TAG, "takePicture: " + e);
        }
        Log.d(TAG, "takePicture: " + cam);

    }

    private void showPaperSizeDialog() {
        // Sử dụng LayoutInflater để nạp layout tùy chỉnh cho dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_paper_size, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Paper Size")
                .setView(dialogView);
        AlertDialog dialog = builder.create();
        PaperSize paperSize = currentSize;
        final Button buttonSubmit = dialogView.findViewById(R.id.btnSubmitPaperSize);
        final Button buttonCancel = dialogView.findViewById(R.id.btnCancelPaperSize);
        final RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupPaperSize);
        final RadioButton radioButtonA4 = radioGroup.findViewById(R.id.radioBtnA4);
        final RadioButton radioButtonA5 = radioGroup.findViewById(R.id.radioBtnA5);
        final RadioButton radioButtonA6 = radioGroup.findViewById(R.id.radioBtnA6);
        final RadioButton radioButtonCCCD = radioGroup.findViewById(R.id.radioBtnCCCD);
        final EditText edtNumPage = dialogView.findViewById(R.id.pageNumberEditText);
        if (currentSize == PaperSize.A4){
            radioButtonA4.setChecked(true);
        }else if (currentSize == PaperSize.A5){
            radioButtonA5.setChecked(true);
        } else if (currentSize == PaperSize.A6){
            radioButtonA6.setChecked(true);
        } else if (currentSize == PaperSize.CCCD){
            radioButtonCCCD.setChecked(true);
        }
        edtNumPage.setText("" + papeNumber);
        buttonSubmit.setOnClickListener(view -> {
            Log.d("On Choosen Paper Size" , currentSize.value);
            try {
                papeNumber = Integer.parseInt(edtNumPage.getText().toString());
            } catch (Exception e){
                papeNumber = 1;
            }
            setTextCount(pages.size());
            configShowFrame(currentSize);
            dialog.dismiss();
        });
        buttonCancel.setOnClickListener(view -> {
            currentSize = paperSize;
            dialog.dismiss();
        });
        radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
            Log.d("Selected Paper Size " ,  " " + i);

            if (i == R.id.radioBtnA4) {
                currentSize = PaperSize.A4;
            } else if (i == R.id.radioBtnA5) {
                currentSize = PaperSize.A5;
            }if (i == R.id.radioBtnA6) {
                currentSize = PaperSize.A6;
            }if (i == R.id.radioBtnCCCD) {
                currentSize = PaperSize.CCCD;
            }

        });
        dialog.show();
    }

    protected FrameLayout getFrameFromSize(PaperSize paperSize) {
        switch (paperSize) {
            case A4:
                return frameLayoutA4;
            case A5:
                return frameLayoutA5;
            case A6:
                return frameLayoutA6;
            case CCCD:
                return frameLayoutCCCD;
        }
        return frameLayoutA4;
    }

    protected void addListenerLoadViewFrame(){
        int widthA4 = previewView.getWidth() - MARGIN_FRAME * 2;
        int heightA4 = (int)(widthA4 * PaperSize.A4.getRatioHW());
        int widthA5 = heightA4/2;
        int heightA5 = (int)(widthA5 * PaperSize.A5.getRatioHW());
        int widthA6 = heightA5/2;
        int heightA6 = (int)(widthA6 * PaperSize.A6.getRatioHW());
        int widthCCCD = (int)(widthA6*PDFGenerator.RATIO_HEIGHT_FRAME_CCCD_A6);
        int heightCCCD = (int) (widthCCCD * PaperSize.CCCD.getRatioHW());
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

        configShowFrame(currentSize);

        previewView.getViewTreeObserver().removeOnGlobalLayoutListener(lis);
    }

    protected void setTextCount(int countPage){
        textViewPageNumber.setText( countPage +"/" + papeNumber);
    }

    protected static Camera.Size getSizeMax(List<Camera.Size> sizes) {
        if (sizes == null || sizes.isEmpty()) {
            return null;
        }
        Camera.Size phanTuLonNhat = sizes.get(0);
        for (Camera.Size kichThuoc : sizes) {
            if (kichThuoc.width > phanTuLonNhat.width && kichThuoc.height > phanTuLonNhat.height) {
                phanTuLonNhat = kichThuoc;
            }
        }
        return phanTuLonNhat;
    }

    protected Bitmap flipHorizontalBitmap(Bitmap bitmap){
        Matrix matrix = new Matrix();
        matrix.setScale(1, -1);
        matrix.postTranslate(0,bitmap.getHeight());
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

    }
    protected Bitmap flipVerticalBitmap(Bitmap bitmap){
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        matrix.postTranslate(bitmap.getWidth(),0);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cam != null) {
            Log.d(TAG, "onDestroy: '");
            cam.stopPreview();
            cam.release();
        }
    }
}


interface CallBackCaptureDone {
    void done();
}