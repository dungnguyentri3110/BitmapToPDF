package com.example.bitmaptopdf;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PDFGenerator {

    public PdfDocument initFromBitmaps(@NonNull List<Bitmap> imgPath) {
        PdfDocument document = new PdfDocument();
        int pageWidth = imgPath.get(0).getWidth();
        int pageHeight = (int) (pageWidth * 297 / 210);

        for (int i = 0; i < imgPath.size(); i++) {
            //Khởi tạo page
            Bitmap bitmap = imgPath.get(i);
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, i + 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            int top = (pageHeight - bitmap.getHeight()) / 2;
            canvas.drawBitmap(bitmap, 0f, top, null);
            document.finishPage(page);
        }
        return document;
    }

    public void saveDocumentToStorage(PdfDocument document) throws IOException {
        long timeStamp = System.currentTimeMillis();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "DOCUMENT_01_"+timeStamp + ".pdf");
        if (file.exists()) {
            file.delete();
        }
        document.writeTo(new FileOutputStream(file));
        document.close();
    }
}
