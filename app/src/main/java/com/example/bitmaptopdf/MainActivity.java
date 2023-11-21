package com.example.bitmaptopdf;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.camera.view.TransformExperimental;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@TransformExperimental public class MainActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayout;
    private LinearLayoutCompat content, containerContent;
    public  static Bitmap bit, bitReal;

    private ImageView img, imageReal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        constraintLayout = findViewById(R.id.container);
        content = findViewById(R.id.content);
        img = findViewById(R.id.img);
        imageReal = findViewById(R.id.imgReal);
    }

    private void convertToPdf(@NonNull List<Bitmap> imgPath){
        PdfDocument document = new PdfDocument();
        float pageWidth = imgPath.get(0).getWidth();
        float pageHeight = pageWidth * 297/210;

        for (int i = 0; i < imgPath.size(); i++) {
            //Khởi tạo page
            Bitmap bitmap = imgPath.get(i);
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), i+1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            canvas.drawBitmap(bitmap, 0f, 0f, null);
            document.finishPage(page);
        }

        long timeStamp = System.currentTimeMillis();

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "DOCUMENT_01" + ".pdf");
        if(file.exists()){
            file.delete();
        }
        try {
            //Write pdf save to downloads folder
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "Make pdf success", Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            e.printStackTrace();
        }

        document.close();
    }

    public void onClickConvert(View view) {
        @SuppressLint("UnsafeOptInUsageError") Intent intent = new Intent(this, CameraActivity.class);
//        startActivity(intent);
        startActivityForResult(intent, 10);

        //TODO: TẠO PDF
//        List<Bitmap> bitmaps = new ArrayList<>();
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img);
//        bitmaps.add(bitmap);
//        bitmaps.add(bitmap);
//        bitmaps.add(bitmap);
////        bitmaps.add(bitmap);
//        convertToPdf(bitmaps);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        img.setImageBitmap(bit);
        imageReal.setImageBitmap(bitReal);
//        if(requestCode == 10 && resultCode == RESULT_OK){
//            Log.d("Nhay vao main nhe", "onActivityResult: ");
//            img.setImageBitmap(bit);
//            imageReal.setImageBitmap(bitReal);
//
//            if(bitReal != null){
//                File fileReal = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "imgReal.jpg");
//                if(fileReal.exists()){
//                    fileReal.delete();
//                }
//                try {
//                    FileOutputStream out = new FileOutputStream(fileReal);
//                    bitReal.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                    out.flush();
//                    out.close();
//
//                } catch (FileNotFoundException e) {
//                    throw new RuntimeException(e);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            if(bit != null){
//                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "imgCrop.jpg");
//                if(file.exists()){
//                    file.delete();
//                }
//                try {
//                    FileOutputStream out = new FileOutputStream(file);
//                    bit.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                    out.flush();
//                    out.close();
//
//                } catch (FileNotFoundException e) {
//                    throw new RuntimeException(e);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
    }
}