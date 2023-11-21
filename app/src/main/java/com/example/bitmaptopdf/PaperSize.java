package com.example.bitmaptopdf;

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
}
