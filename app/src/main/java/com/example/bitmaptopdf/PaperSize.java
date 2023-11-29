package com.example.bitmaptopdf;

import android.graphics.Bitmap;

public enum PaperSize {
    ALL("All"),
    A4("A4"),
    A5("A5"),
    A6("A6"),
    CCCD("CCCD"),
    NONE("NONE");

    public String value;

    PaperSize(String value) {
        this.value = value;
    }

    public static PaperSize getValue(String str) {
        switch (str.toUpperCase()){
            case "ALL":
                return PaperSize.ALL;
            case "A4":
                return PaperSize.A4;
            case "A5":
                return PaperSize.A5;
            case "A6":
                return PaperSize.A6;
            case "CCCD":
                return PaperSize.CCCD;
            default:
                return PaperSize.NONE;
        }
    }

    public double getRatioHW() {
        return getHeight()/getWidth();
    }

    public double getWidth() {
        switch (this){
            case A4:
                return 210.0;
            case A5:
                return 148.0;
            case A6:
                return 105.0;
            case CCCD:
                return 53.98;
            default:
                return A4.getWidth();
        }
    }

    public double getHeight() {
        switch (this){
            case A4:
                return 297.0;
            case A5:
                return 210.0;
            case A6:
                return 148.0;
            case CCCD:
                return 85.6;
            default:
                return A4.getHeight();
        }
    }
}

class PaperDocument {
    PaperSize paperSize;
    Bitmap bitmap;

    public PaperDocument(PaperSize paperSize, Bitmap bitmap) {
        this.paperSize = paperSize;
        this.bitmap = bitmap;
    }
}