package com.zpl2pdf.domain;

import com.zpl2pdf.shared.ApplicationConstants;

/**
 * Represents conversion options for ZPL to PDF conversion
 */
public class ConversionOptions {
    private double width;
    private double height;
    private String unit = "mm";
    private int dpi = 203;
    private String outputFileName = "output.pdf";
    private String outputFolderPath = "";
    private boolean useExplicitDimensions;
    private boolean extractDimensionsFromZpl = true;

    public ConversionOptions() {
    }

    public ConversionOptions(double width, double height, String unit, int dpi) {
        this.width = width;
        this.height = height;
        this.unit = unit;
        this.dpi = dpi;
        this.useExplicitDimensions = true;
        this.extractDimensionsFromZpl = false;
    }

    public ConversionOptions(String unit, int dpi) {
        this.unit = unit;
        this.dpi = dpi;
        this.useExplicitDimensions = false;
        this.extractDimensionsFromZpl = true;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getDpi() {
        return dpi;
    }

    public void setDpi(int dpi) {
        this.dpi = dpi;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public String getOutputFolderPath() {
        return outputFolderPath;
    }

    public void setOutputFolderPath(String outputFolderPath) {
        this.outputFolderPath = outputFolderPath;
    }

    public boolean isUseExplicitDimensions() {
        return useExplicitDimensions;
    }

    public void setUseExplicitDimensions(boolean useExplicitDimensions) {
        this.useExplicitDimensions = useExplicitDimensions;
    }

    public boolean isExtractDimensionsFromZpl() {
        return extractDimensionsFromZpl;
    }

    public void setExtractDimensionsFromZpl(boolean extractDimensionsFromZpl) {
        this.extractDimensionsFromZpl = extractDimensionsFromZpl;
    }

    public boolean isValid() {
        if (unit == null || unit.trim().isEmpty()) return false;
        if (dpi <= 0) return false;
        if (useExplicitDimensions) {
            if (width <= 0 || height <= 0) return false;
        }
        if (outputFolderPath == null || outputFolderPath.trim().isEmpty()) return false;
        return true;
    }

    public String getValidationError() {
        if (unit == null || unit.trim().isEmpty()) return "Unit cannot be null or empty";
        if (dpi <= 0) return "DPI must be greater than 0";
        if (useExplicitDimensions) {
            if (width <= 0) return "Width must be greater than 0";
            if (height <= 0) return "Height must be greater than 0";
        }
        if (outputFolderPath == null || outputFolderPath.trim().isEmpty()) return "Output folder path cannot be null or empty";
        return "";
    }

    @Override
    public ConversionOptions clone() {
        ConversionOptions options = new ConversionOptions();
        options.width = this.width;
        options.height = this.height;
        options.unit = this.unit;
        options.dpi = this.dpi;
        options.outputFileName = this.outputFileName;
        options.outputFolderPath = this.outputFolderPath;
        options.useExplicitDimensions = this.useExplicitDimensions;
        options.extractDimensionsFromZpl = this.extractDimensionsFromZpl;
        return options;
    }

    @Override
    public String toString() {
        if (useExplicitDimensions) {
            return String.format("ConversionOptions: %.2fx%.2f %s, Print Density: %.1f dpmm (%d dpi)",
                    width, height, unit, ApplicationConstants.convertDpiToDpmm(ApplicationConstants.DEFAULT_DPI), ApplicationConstants.DEFAULT_DPI);
        } else {
            return String.format("ConversionOptions: Extract from ZPL, Unit: %s, Print Density: %.1f dpmm (%d dpi)",
                    unit, ApplicationConstants.convertDpiToDpmm(ApplicationConstants.DEFAULT_DPI), ApplicationConstants.DEFAULT_DPI);
        }
    }
}
