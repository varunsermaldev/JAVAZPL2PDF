package com.binarykits.zpl.label.elements;

public class ZplGraphicBox extends ZplPositionedElementBase {
    private int width;
    private int height;
    private int borderThickness;
    private String lineColor; // B or W
    private int cornerRounding;

    public ZplGraphicBox(int positionX, int positionY, int width, int height, int borderThickness, String lineColor, int cornerRounding) {
        super(positionX, positionY, false); // Box is top-left origin usually
        this.width = width;
        this.height = height;
        this.borderThickness = borderThickness;
        this.lineColor = lineColor;
        this.cornerRounding = cornerRounding;
    }
    
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getBorderThickness() { return borderThickness; }
    public String getLineColor() { return lineColor; }
    
    public boolean isLine() {
        // Naive line check: if filled or one dim is small
        // Actually ZPL GB is a box. If width == thickness, it's a solid box?
        return false; 
    }
}
