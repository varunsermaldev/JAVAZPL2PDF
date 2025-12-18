package com.zpl2pdf;

public class ZPLConfig {
    private float width;
    private float height;
    private String unit;
    private int dpi;

    public ZPLConfig(float width, float height, String unit, int dpi) {
        this.width = width;
        this.height = height;
        this.unit = unit;
        this.dpi = dpi;
    }

    // Default 4x6 inch 203 DPI config
    public static ZPLConfig default4x6() {
        return new ZPLConfig(4, 6, "in", 203);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public String getUnit() {
        return unit;
    }

    public int getDpi() {
        return dpi;
    }

    @Override
    public String toString() {
        return "ZPLConfig{" +
                "width=" + width +
                ", height=" + height +
                ", unit='" + unit + '\'' +
                ", dpi=" + dpi +
                '}';
    }
}
