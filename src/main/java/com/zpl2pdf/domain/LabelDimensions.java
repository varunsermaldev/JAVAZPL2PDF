package com.zpl2pdf.domain;

import com.zpl2pdf.shared.ApplicationConstants;
import java.util.Objects;
import java.io.Serializable;

/**
 * Represents label dimensions with validation and conversion methods
 */
public class LabelDimensions implements Serializable {
    private double width;
    private double height;
    private String unit = "mm";
    private int dpi = 203;

    public LabelDimensions() {
    }

    public LabelDimensions(double width, double height, String unit, int dpi) {
        this.width = width;
        this.height = height;
        this.unit = unit;
        this.dpi = dpi;
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

    public boolean isValid() {
        if (width <= 0) return false;
        if (height <= 0) return false;
        if (unit == null || unit.trim().isEmpty()) return false;
        if (dpi <= 0) return false;
        return isValidUnit(unit);
    }

    public String getValidationError() {
        if (width <= 0) return "Width must be greater than 0";
        if (height <= 0) return "Height must be greater than 0";
        if (unit == null || unit.trim().isEmpty()) return "Unit cannot be null or empty";
        if (dpi <= 0) return "DPI must be greater than 0";
        if (!isValidUnit(unit)) return "Invalid unit: " + unit + ". Valid units are: mm, cm, in";
        return "";
    }

    public static boolean isValidUnit(String unit) {
        if (unit == null || unit.trim().isEmpty()) return false;
        String[] validUnits = {"mm", "cm", "in"};
        for (String u : validUnits) {
            if (u.equalsIgnoreCase(unit)) return true;
        }
        return false;
    }

    public LabelDimensions toMillimeters() {
        if ("mm".equalsIgnoreCase(unit)) {
            return clone();
        }
        double widthMm = convertToMillimeters(width, unit);
        double heightMm = convertToMillimeters(height, unit);
        return new LabelDimensions(widthMm, heightMm, "mm", dpi);
    }

    public LabelDimensions toCentimeters() {
        LabelDimensions mmDimensions = toMillimeters();
        return new LabelDimensions(
            mmDimensions.width / 10.0,
            mmDimensions.height / 10.0,
            "cm",
            dpi
        );
    }

    public LabelDimensions toInches() {
        LabelDimensions mmDimensions = toMillimeters();
        return new LabelDimensions(
            mmDimensions.width / 25.4,
            mmDimensions.height / 25.4,
            "in",
            dpi
        );
    }

    private static double convertToMillimeters(double value, String fromUnit) {
        if (fromUnit == null) return value;
        switch (fromUnit.toLowerCase()) {
            case "mm": return value;
            case "cm": return value * 10.0;
            case "in": return value * 25.4;
            default: return value;
        }
    }

    public int[] toPoints() {
        LabelDimensions mmDimensions = toMillimeters();
        int widthPoints = (int) Math.round((mmDimensions.width / 25.4) * 72.0);
        int heightPoints = (int) Math.round((mmDimensions.height / 25.4) * 72.0);
        return new int[]{widthPoints, heightPoints};
    }

    @Override
    public LabelDimensions clone() {
        return new LabelDimensions(width, height, unit, dpi);
    }

    @Override
    public String toString() {
        return String.format("LabelDimensions: %.2fx%.2f %s, Print Density: %.1f dpmm (%d dpi)",
                width, height, unit, ApplicationConstants.convertDpiToDpmm(ApplicationConstants.DEFAULT_DPI), ApplicationConstants.DEFAULT_DPI);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LabelDimensions that = (LabelDimensions) o;
        return Math.abs(that.width - width) < 0.001 &&
               Math.abs(that.height - height) < 0.001 &&
               dpi == that.dpi &&
               unit.equalsIgnoreCase(that.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, unit.toLowerCase(), dpi);
    }
}
