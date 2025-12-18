package com.zpl2pdf.rendering;

import com.zpl2pdf.domain.LabelDimensions;
import com.zpl2pdf.shared.ApplicationConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for processing labels, generating images in memory, and returning image data.
 */
public class LabelRenderer {

    private final ZplConverter zplConverter;
    private final double labelWidthMm;
    private final double labelHeightMm;
    private final int printDpi;

    private static final double INCHES_TO_MM = 25.4;
    private static final double CM_TO_MM = 10.0;
    private static final double DPI_TO_DPMM = 25.4;

    /**
     * Initializes a new instance of the LabelRenderer class.
     *
     * @param labelWidth Label width.
     * @param labelHeight Label height.
     * @param printDpi Print density in DPI.
     * @param unit Unit of measurement (mm, cm, in).
     * @param zplConverter The ZPL converter implementation.
     */
    public LabelRenderer(double labelWidth, double labelHeight, int printDpi, String unit, ZplConverter zplConverter) {
        this.zplConverter = zplConverter;
        
        switch (unit.toLowerCase()) {
            case "in":
                this.labelWidthMm = labelWidth * INCHES_TO_MM;
                this.labelHeightMm = labelHeight * INCHES_TO_MM;
                break;
            case "cm":
                this.labelWidthMm = labelWidth * CM_TO_MM;
                this.labelHeightMm = labelHeight * CM_TO_MM;
                break;
            case "mm":
                this.labelWidthMm = labelWidth;
                this.labelHeightMm = labelHeight;
                break;
            default:
                this.labelWidthMm = 60;   // 60 mm
                this.labelHeightMm = 120;  // 120 mm
                break;
        }
        
        this.printDpi = printDpi;
    }

    /**
     * Initializes a new instance using LabelDimensions.
     *
     * @param dimensions Label dimensions.
     * @param zplConverter The ZPL converter implementation.
     */
    public LabelRenderer(LabelDimensions dimensions, ZplConverter zplConverter) {
        this.zplConverter = zplConverter;
        LabelDimensions mmDims = dimensions.toMillimeters();
        this.labelWidthMm = mmDims.getWidth();
        this.labelHeightMm = mmDims.getHeight();
        this.printDpi = dimensions.getDpi();
    }

    /**
     * Processes a list of ZPL labels and returns a list of images (in byte[]).
     *
     * @param labels List of ZPL labels.
     * @return List of images in byte arrays.
     */
    public List<byte[]> renderLabels(List<String> labels) {
        List<byte[]> images = new ArrayList<>();
        
        int dpmm = (int) Math.round(printDpi / DPI_TO_DPMM);
        
        for (String labelText : labels) {
            List<byte[]> labelImages = zplConverter.convert(labelText, labelWidthMm, labelHeightMm, dpmm);
            images.addAll(labelImages);
        }
        return images;
    }

    /**
     * Saves the image data to a file.
     *
     * @param imageData Image data in byte array.
     * @param filePath Path to save the image file.
     */
    private void saveImageToFile(byte[] imageData, String filePath) {
        try (FileOutputStream fos = new FileOutputStream(new File(filePath))) {
            fos.write(imageData);
        } catch (IOException e) {
            e.printStackTrace(); // Or use a logger
        }
    }
}
