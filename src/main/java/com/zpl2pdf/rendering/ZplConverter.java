package com.zpl2pdf.rendering;

import java.util.List;

public interface ZplConverter {
    /**
     * Converts ZPL data to a list of images.
     *
     * @param zplData  The ZPL string.
     * @param widthMm  Label width in millimeters.
     * @param heightMm Label height in millimeters.
     * @param dpmm     Dots per millimeter.
     * @return List of images as byte arrays.
     */
    List<byte[]> convert(String zplData, double widthMm, double heightMm, int dpmm);
}
