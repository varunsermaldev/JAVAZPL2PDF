package com.zpl2pdf.shared;

public class ApplicationConstants {
    
    public static final String DEFAULT_UNIT = "mm";
    public static final int DEFAULT_DPI = 203;
    public static final double DPI_TO_DPMM_FACTOR = 25.4;

    public static double convertDpiToDpmm(int dpi) {
        return dpi / DPI_TO_DPMM_FACTOR;
    }
}
